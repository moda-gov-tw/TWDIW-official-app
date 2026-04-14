import Foundation

class FrameworkLoader {
    
    enum LoadError: LocalizedError {
        case frameworkNotFound
        case symbolNotFound
        case invalidHandle
        case invalidPath
        
        var errorDescription: String? {
            switch self {
            case .frameworkNotFound:
                return "Framework not found"
            case .symbolNotFound:
                return "Symbol not found"
            case .invalidHandle:
                return "Invalid handle"
            case .invalidPath:
                return "Invalid framework path"
            }
        }
    }
        
    private var handle: UnsafeMutableRawPointer?
    
    private func checkPath(_ path: String, description: String) -> Bool {
        let exists = FileManager.default.fileExists(atPath: path)
        PrintPro.print(exists ? "✅" : "❌", "\(description):", path)
        return exists
    }
    
    private func findFrameworkPath(frameworkName: String) -> String? {
        let normalizedName = frameworkName.replacingOccurrences(of: "-", with: "_")
        
        // 基本路徑
        let frameworksPath = Bundle.main.bundlePath + "/Frameworks"
        
        PrintPro.print("\n🔍 Checking framework paths:")
        PrintPro.print("Main bundle path:", Bundle.main.bundlePath)
        PrintPro.print("Frameworks directory:", frameworksPath)
        
        let frameworkPath = frameworksPath + "/\(normalizedName).framework"
        
        if checkPath(frameworkPath, description: "Framework bundle") {
            PrintPro.print("✅ Found framework at:", frameworkPath)
            return frameworkPath
        } else {
            PrintPro.print("❌ Framework not found at:", frameworkPath)
        }
        
        return nil
    }
    
    func loadFramework(parentFramework: String, childFramework: String? = nil) -> LoadError? {
        PrintPro.print("\n🔍 Starting framework path search...")
        
        guard let parentPath = findFrameworkPath(frameworkName: parentFramework) else {
            return .frameworkNotFound
        }
        
        let finalPath: String
        if let childFramework = childFramework {
            let normalizedChildName = childFramework.replacingOccurrences(of: "-", with: "_")
            
            // 定義可能的路徑
            let possibleChildPaths = [
                // 1. 原始路徑 (向下相容)
                "\(parentPath)/Frameworks/\(normalizedChildName).framework/\(normalizedChildName)",
                // 2. 同級目錄路徑
                "\(parentPath)/../\(normalizedChildName).framework/\(normalizedChildName)",
                // 3. 主程式包路徑
                Bundle.main.bundlePath + "/Frameworks/\(normalizedChildName).framework/\(normalizedChildName)"
            ]
            
            PrintPro.print("\n🔍 Checking possible child framework paths:")
            
            // 尋找第一個存在的路徑
            var foundPath: String? = nil
            for path in possibleChildPaths {
                PrintPro.print("Checking path:", path)
                if FileManager.default.fileExists(atPath: path) {
                    foundPath = path
                    PrintPro.print("✅ Found child framework at:", path)
                    break
                }
            }
            
            // 如果找不到任何路徑，使用主程式包路徑
            finalPath = foundPath ?? possibleChildPaths[2]
            
        } else {
            let normalizedParentName = parentFramework.replacingOccurrences(of: "-", with: "_")
            finalPath = "\(parentPath)/\(normalizedParentName)"
        }
        
        guard FileManager.default.fileExists(atPath: finalPath) else {
            PrintPro.print("❌ Framework not found at:", finalPath)
            return .frameworkNotFound
        }
        
        PrintPro.print("\n📦 Loading framework from:", finalPath)
        handle = dlopen(finalPath, RTLD_LOCAL | RTLD_NOW)
        
        if let error = dlerror() {
            PrintPro.print("❌ dlopen error:", String(cString: error))
            return .invalidHandle
        }
        
        guard handle != nil else {
            return .invalidHandle
        }
        
        PrintPro.print("✅ Successfully loaded framework")
        return nil
    }
    
    func getSymbol<T>(named symbolName: String, symbol: inout T) -> LoadError? {
        guard let handle = handle else {
            PrintPro.print("❌ No valid handle when trying to get symbol: \(symbolName)")
            return .invalidHandle
        }
        
        PrintPro.print("Attempting to get symbol: \(symbolName)")
        
        // 清除之前的錯誤
        dlerror()
        
        let symbolPointer = dlsym(handle, symbolName)
        
        if let error = dlerror() {
            PrintPro.print("❌ dlsym error for symbol \(symbolName): \(String(cString: error))")
            return .symbolNotFound
        }
        
        guard symbolPointer != nil else {
            PrintPro.print("❌ Symbol is nil but no error was reported")
            return .symbolNotFound
        }
        
        PrintPro.print("✅ Successfully got symbol: \(symbolName)")
        symbol = unsafeBitCast(symbolPointer, to: T.self)
        return nil
    }
    
    func unloadFramework() {
        if let handle = handle {
            if dlclose(handle) != 0 {
                if let error = dlerror() {
                    PrintPro.print("❌ Error during dlclose: \(String(cString: error))")
                }
            } else {
                PrintPro.print("✅ Successfully unloaded framework")
            }
            self.handle = nil
        }
    }
    
    deinit {
        unloadFramework()
    }
}
