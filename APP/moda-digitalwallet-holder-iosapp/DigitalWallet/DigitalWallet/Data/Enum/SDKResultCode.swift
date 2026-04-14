//
//  SDKResultCode.swift
//  DigitalWallet
//

import Foundation

enum SDKResultCode: String {
    /*成功*/
    case Success = "0"
    /*SDK 失敗*/
    case SDKFail = "1"
    /*網路失敗*/
    case NetworkFail = "2"
    /*授權失敗*/
    case VerifyFail = "3"
    /*關楗SDK失敗*/
    case SDKKeyFail = "4"
    /*系統底層未知錯誤*/
    case SystemFail = "C9998"
    /*App自生錯誤*/
    case AppError = "C9999"
    /// 不在信任清單內
    case NotInTrustedList = "4012"
}

enum SDKErrorEnum: String, CaseIterable{
    /*dwverifier-oidvp*/
    case Error2001 = "2001"
    case Error2002 = "2002"
    case Error2003 = "2003"
    case Error2004 = "2004"
    case Error2005 = "2005"
    case Error2006 = "2006"
    case Error2007 = "2007"
    case Error2008 = "2008"
    case Error2009 = "2009"
    case Error2010 = "2010"
    case Error2011 = "2011"
    case Error2012 = "2012"
    case Error2013 = "2013"
    case Error2014 = "2014"
    case Error3000 = "3000"
    case Error4001 = "4001"
    case Error5000 = "5000"
    case Error5001 = "5001"
    case Error5002 = "5002"
    case Error5003 = "5003"
    case Error5005 = "5005"
    
    /*dwissuer-oidvci*/
    case Error11001 = "11001"
    case Error11002 = "11002"
    case Error11003 = "11003"
    case Error11005 = "11005"
    case Error11007 = "11007"
    case Error11008 = "11008"
    case Error11009 = "11009"
    case Error11010 = "11010"
    case Error11011 = "11011"
    case Error11012 = "11012"
    case Error11013 = "11013"
    case Error11014 = "11014"
    case Error11015 = "11015"
    case Error11016 = "11016"
    case Error11017 = "11017"
    case Error11018 = "11018"
    case Error11019 = "11019"
    case Error11020 = "11020"
    case Error11021 = "11021"
    case Error11022 = "11022"
    case Error11023 = "11023"
    case Error11032 = "11032"
    case Error11500 = "11500"
    case Error11901 = "11901"
    case Error12000 = "12000"
    case Error12003 = "12003"
    case Error12006 = "12006"
    case Error12007 = "12007"
    case Error12009 = "12009"
    
    var message: String{
        switch self {
            case .Error2001: NSLocalizedString("SDKError2001", comment: "")
            case .Error2002: NSLocalizedString("SDKError2002", comment: "")
            case .Error2003: NSLocalizedString("SDKError2003", comment: "")
            case .Error2004: NSLocalizedString("SDKError2004", comment: "")
            case .Error2005: NSLocalizedString("SDKError2005", comment: "")
            case .Error2006: NSLocalizedString("SDKError2006", comment: "")
            case .Error2007: NSLocalizedString("SDKError2007", comment: "")
            case .Error2008: NSLocalizedString("SDKError2008", comment: "")
            case .Error2009: NSLocalizedString("SDKError2009", comment: "")
            case .Error2010: NSLocalizedString("SDKError2010", comment: "")
            case .Error2011: NSLocalizedString("SDKError2011", comment: "")
            case .Error2012: NSLocalizedString("SDKError2012", comment: "")
            case .Error2013: NSLocalizedString("SDKError2013", comment: "")
            case .Error2014: NSLocalizedString("SDKError2014", comment: "")
            case .Error3000: NSLocalizedString("SDKError3000", comment: "")
            case .Error4001: NSLocalizedString("SDKError4001", comment: "")
            case .Error5000: NSLocalizedString("SDKError5000", comment: "")
            case .Error5001: NSLocalizedString("SDKError5001", comment: "")
            case .Error5002: NSLocalizedString("SDKError5002", comment: "")
            case .Error5003: NSLocalizedString("SDKError5003", comment: "")
            case .Error5005: NSLocalizedString("SDKError5005", comment: "")
            
            case .Error11001: NSLocalizedString("SDKError11001", comment: "")
            case .Error11002: NSLocalizedString("SDKError11002", comment: "")
            case .Error11003: NSLocalizedString("SDKError11003", comment: "")
            case .Error11005: NSLocalizedString("SDKError11005", comment: "")
            case .Error11007: NSLocalizedString("SDKError11007", comment: "")
            case .Error11008: NSLocalizedString("SDKError11008", comment: "")
            case .Error11009: NSLocalizedString("SDKError11009", comment: "")
            case .Error11010: NSLocalizedString("SDKError11010", comment: "")
            case .Error11011: NSLocalizedString("SDKError11011", comment: "")
            case .Error11012: NSLocalizedString("SDKError11012", comment: "")
            case .Error11013: NSLocalizedString("SDKError11013", comment: "")
            case .Error11014: NSLocalizedString("SDKError11014", comment: "")
            case .Error11015: NSLocalizedString("SDKError11015", comment: "")
            case .Error11016: NSLocalizedString("SDKError11016", comment: "")
            case .Error11017: NSLocalizedString("SDKError11017", comment: "")
            case .Error11018: NSLocalizedString("SDKError11018", comment: "")
            case .Error11019: NSLocalizedString("SDKError11019", comment: "")
            case .Error11020: NSLocalizedString("SDKError11020", comment: "")
            case .Error11021: NSLocalizedString("SDKError11021", comment: "")
            case .Error11022: NSLocalizedString("SDKError11022", comment: "")
            case .Error11023: NSLocalizedString("SDKError11023", comment: "")
            case .Error11032: NSLocalizedString("SDKError11032", comment: "")
            case .Error11500: NSLocalizedString("SDKError11500", comment: "")
            case .Error11901: NSLocalizedString("SDKError11901", comment: "")
            case .Error12000: NSLocalizedString("SDKError12000", comment: "")
            case .Error12003: NSLocalizedString("SDKError12003", comment: "")
            case .Error12006: NSLocalizedString("SDKError12006", comment: "")
            case .Error12007: NSLocalizedString("SDKError12007", comment: "")
            case .Error12009: NSLocalizedString("SDKError12009", comment: "")
        }
    }
    
    /**用錯誤代碼取出對應Enum*/
    static func fromCode(_ code: String) -> SDKErrorEnum? {
        return Self.allCases.first { $0.rawValue == code }
    }
}

enum DIDError: Error {
    case encodeFail
    case responseError(code: String?, message: String?)
    
    var message: String {
        switch self {
        case .encodeFail:
            return "encode fail"
        case .responseError(_, let message):
            return message ?? ""
        }
    }
}

enum SDKError: Error {
    case keyDataIsEmpty
    case didDataIsEmpty
    case LocalDataIsEmpty
}
