import Foundation

/// 錯誤處理
public enum Error: Swift.Error {
    /// 一般錯誤
    case general(code: CUnsignedLong)
    
    /// Security framework 錯誤
    case security(status: OSStatus, method: String?)
    
    /// CFError 錯誤
    case cfError(error: CFError, method: String?)
    
    /// 從一般錯誤碼建立錯誤物件
    /// - Parameter code: 一般錯誤碼
    /// - Returns: 錯誤物件
    public static func fromGeneral(_ code: CUnsignedLong) -> Error {
        return .general(code: code)
    }
    
    /// 從 OSStatus 建立錯誤物件
    /// - Parameters:
    ///   - status: OSStatus 錯誤碼
    ///   - method: 發生錯誤的方法名稱（可選）
    /// - Returns: 錯誤物件
    public static func fromOSStatus(_ status: OSStatus, method: String? = nil) -> Error {
        // 直接使用 OSStatus，保留負數資訊
        return .security(status: status, method: method)
    }
    
    /// 從 Unmanaged<CFError> 建立錯誤物件
    /// - Parameters:
    ///   - unmanagedError: Unmanaged<CFError> 錯誤物件
    ///   - method: 發生錯誤的方法名稱（可選）
    /// - Returns: 錯誤物件
    public static func fromCFError(_ unmanagedError: Unmanaged<CFError>, method: String? = nil) -> Error {
        let error = unmanagedError.takeRetainedValue()
        return .cfError(error: error, method: method)
    }
    
    /// 取得原始錯誤碼
    public var code: Int {
        switch self {
        case .general(let code):
            return Int(code)
        case .security(let status, _):
            return Int(status)
        case .cfError(let error, _):
            return CFErrorGetCode(error)
        }
    }
    
    /// 新增一個方法來獲取原始的 OSStatus
    public var originalStatus: OSStatus? {
        switch self {
        case .security(let status, _):
            return status
        default:
            return nil
        }
    }
    
    /// 取得方法名稱
    public var method: String? {
        switch self {
        case .general:
            return nil
        case .security(_, let method):
            return method
        case .cfError(_, let method):
                    return method
        }
    }
}

// MARK: - LocalizedError
extension Error: LocalizedError {
    public var errorDescription: String? {
        switch self {
        case .general(let code):
            let description = Self.generalErrorMessage(for: code)
            return "General Error: \(description) (Code: \(code))"
            
        case .security(let status, let method):
            let description = SecCopyErrorMessageString(status, nil) as String? ?? "Unknown error"
            if let method = method {
                return "Security Error in \(method): \(description) (Status: \(status))"
            } else {
                return "Security Error: \(description) (Status: \(status))"
            }
            
        case .cfError(let error, let method):
            let description = CFErrorCopyDescription(error) as String? ?? "Unknown error"
            if let method = method {
                return "CFError in \(method): \(description) (Code: \(CFErrorGetCode(error)))"
            } else {
                return "CFError: \(description) (Code: \(CFErrorGetCode(error)))"
            }
            
        }
        
    }
}

// MARK: - CustomDebugStringConvertible
extension Error: CustomDebugStringConvertible {
    public var debugDescription: String {
        switch self {
        case .general(let code):
            return "General Error: \(Self.generalErrorMessage(for: code)) (Code: \(code))"
            
        case .security(let status, let method):
            let description = SecCopyErrorMessageString(status, nil) as String? ?? "Unknown error"
            if let method = method {
                return "Security Error in \(method): \(description) (Status: \(status))"
            } else {
                return "Security Error: \(description) (Status: \(status))"
            }
            
        case .cfError(let error, let method):
            let description = CFErrorCopyDescription(error) as String? ?? "Unknown error"
            let domain = CFErrorGetDomain(error) as String
            if let method = method {
                return "CFError in \(method): [\(domain)] \(description) (Code: \(CFErrorGetCode(error)))"
            } else {
                return "CFError: [\(domain)] \(description) (Code: \(CFErrorGetCode(error)))"
            }
        }
    }
}

// MARK: - Equatable
extension Error: Equatable {
    public static func == (lhs: Error, rhs: Error) -> Bool {
        switch (lhs, rhs) {
        case (.general(let code1), .general(let code2)):
            return code1 == code2
        case (.cfError(let error1, let method1), .cfError(let error2, let method2)):
            return CFErrorGetCode(error1) == CFErrorGetCode(error2) &&
            CFErrorGetDomain(error1) as String == CFErrorGetDomain(error2) as String &&
            method1 == method2
        default:
            return false
        }
    }
}

// MARK: - Helpers
extension Error {
    /// 轉換錯誤碼為十六進位字串
    public var hexErrorCode: String {
        return String(format: "0x%08X", code)
    }
}

// MARK: - 轉換Error用
extension Error {
    /// 從自訂錯誤資訊建立錯誤物件
    /// - Parameters:
    ///   - domain: 錯誤域
    ///   - code: 錯誤碼
    ///   - description: 錯誤描述
    ///   - recoverySuggestion: 錯誤恢復建議
    ///   - failureReason: 錯誤原因
    /// - Returns: 錯誤物件
    public static func createError(
        domain: String,
        code: CUnsignedLong,
        description: String,
        recoverySuggestion: String? = nil,
        failureReason: String? = nil
    ) -> Error {
        /// 建立錯誤資訊結構
        let errorInfo = ErrorInfo(
            domain: domain,
            description: description,
            recoverySuggestion: recoverySuggestion,
            failureReason: failureReason
        )
        
        /// 儲存自訂錯誤訊息
        customErrorInfos[code] = errorInfo
        
        var methodName = ""
        if let startRange = description.range(of: "in "),
           let endRange = description.range(of: ":") {
            let start = startRange.upperBound
            methodName = String(description[start..<endRange.lowerBound])
            PrintPro.print(methodName)
        }
        
        switch domain {
        case "com.digitalwallet.general":
            return .general(code: code)
        default:
            return .general(code: code)
        }
        
    }
    
    /// 錯誤資訊結構
    private struct ErrorInfo {
        let domain: String
        let description: String
        let recoverySuggestion: String?
        let failureReason: String?
    }
    
    /// 儲存自訂錯誤訊息的字典
    private static var customErrorInfos: [CUnsignedLong: ErrorInfo] = [:]
    
    /// 取得錯誤域
    public var domain: String {
        switch self {
        case .general(let code):
            return Error.customErrorInfos[code]?.domain ?? "com.digitalwallet.general"
        case .security:
            return "com.digitalwallet.security"
        case .cfError(let error, _):
            return CFErrorGetDomain(error) as String
        }
    }
}


