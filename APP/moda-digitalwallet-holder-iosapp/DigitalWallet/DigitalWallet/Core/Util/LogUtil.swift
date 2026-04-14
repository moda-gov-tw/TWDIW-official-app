//
//  LogUtil.swift
//  DigitalWallet
//

import Foundation

enum LogActionType: String {
    case request = "RQ"
    case response = "RS"
}

enum LogType: String {
    case info = "Info"
    case error = "Error"
}

class LogUtil {
    static var showLog: Bool = false
    static let shared = LogUtil()
    var fileManager = FileManager.default
    var now: String {
        get {
            let formatter = DateFormatter()
            formatter.dateFormat = "yyyy/MM/dd HH:mm:ss SSS"
            let now = formatter.string(from: Date())
            return now
        }
    }
    var fileName: String {
        get {
            let documentURL = fileManager.urls(for: .documentDirectory, in: .userDomainMask)[0]
            let formatter = DateFormatter()
            formatter.dateFormat = "yyyyMMddHHmm"
            
            let now = formatter.string(from: Date())
            guard let contentsOfURL = try? fileManager.contentsOfDirectory(atPath: documentURL.path) else {
                return now + "iOS.log"
            }
            
            for fileName in contentsOfURL {
                if fileName.contains("iOS.log") {
                    return fileName
                }
            }
            
            return now + "iOS.log"
        }
    }
    var filePath: URL {
        get {
            let documentURL = fileManager.urls(for: .documentDirectory, in: .userDomainMask)[0]
            var filePath = documentURL.appendingPathComponent(fileName)
            var values = URLResourceValues()
            values.isExcludedFromBackup = true
            try? filePath.setResourceValues(values)
            return filePath
        }
    }
    /// bytes
    var fileSize: UInt64 {
        get {
            guard let attr = try? fileManager.attributesOfItem(atPath: filePath.path) else {
                return 0
            }
            let fileSize = attr[FileAttributeKey.size] as! UInt64
            return fileSize
        }
    }
    
    private init() {}
    
    func write(actionType: LogActionType, type: LogType, actionName: String, action: String) {
        
        let logText = now + "|" + type.rawValue + "|" + actionName + " " + actionType.rawValue + "|" + action + "\n"
        
        guard let fileHandle = FileHandle(forWritingAtPath: filePath.path) else {
            fileManager.createFile(atPath: filePath.path, contents: nil)
            write(actionType: actionType, type: type, actionName: actionName, action: action)
            return
        }
        
        fileHandle.seekToEndOfFile()
        fileHandle.write(logText.data(using: .utf8)!)
        fileHandle.closeFile()
    }
    
    func write(text: String) {
        guard let fileHandle = FileHandle(forWritingAtPath: filePath.path) else {
            return
        }
        fileHandle.seekToEndOfFile()
        fileHandle.write(text.data(using: .utf8)!)
        fileHandle.closeFile()
    }
    
    func getLog(maxLines: Int = 50) -> String {
        guard let fileData = try? Data(contentsOf: filePath) else {
            return ""
        }
        
        guard let fileContent = String(data: fileData, encoding: .utf8) else {
            return ""
        }
        
        let logLines = fileContent.components(separatedBy: "\n").filter{ !$0.isEmpty }
        let lastLines = logLines.suffix(maxLines)
        
        return lastLines.joined(separator: "\n")
    }
    
    func getLogFile() -> Data?{
        guard let file = try? Data(contentsOf: filePath) else {
            return nil
        }
        return file
    }
    
    func checkFileCreate() {
        let index = fileName.index(fileName.startIndex, offsetBy: 12)
        let createDateString = String(fileName[..<index])
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyyMMddHHmm"
        guard let date = dateFormatter.date(from: createDateString) else {
            return
        }
        
        let calendar = Calendar.current
        let createDate = calendar.startOfDay(for: date)
        let nowDate = calendar.startOfDay(for: Date())
        let components = calendar.dateComponents([.day], from: createDate, to: nowDate)
        guard let daysDifference = components.day else {
            return
        }
        if daysDifference > 1 {
            deleteLog()
        }
    }
    
    private func deleteLog(){
        try? fileManager.removeItem(at: filePath)
    }
}
