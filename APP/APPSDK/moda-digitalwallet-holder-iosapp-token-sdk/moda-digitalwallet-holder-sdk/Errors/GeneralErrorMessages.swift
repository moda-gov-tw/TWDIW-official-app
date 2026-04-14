import Foundation

// 一般錯誤的錯誤碼定義
public let SOFTWARE_LICENSE_ASN1_ERROR: CUnsignedLong = 700
public let SOFTWARE_LICENSE_CREATE_KEY_ERROR: CUnsignedLong = 701
public let SOFTWARE_STRING_TO_DATA_ERROR: CUnsignedLong = 702
public let SOFTWARE_BASE64_TO_DATA_ERROR: CUnsignedLong = 703
public let SOFTWARE_HEX_TO_BASE64_ERROR: CUnsignedLong = 704
public let SOFTWARE_INVALID_PARAMETER_ERROR: CUnsignedLong = 705
public let SOFTWARE_VERIFY_SIGNATURE_ERROR: CUnsignedLong = 706
public let SOFTWARE_LOAD_FRAMEWORK_ERROR: CUnsignedLong = 710
public let SOFTWARE_GET_SYMBOL_ERROR: CUnsignedLong = 711

public let SOFTWARE_KEY_GENERATION_FAILED: CUnsignedLong = 800
public let SOFTWARE_KEY_NOT_FOUND: CUnsignedLong = 801
public let SOFTWARE_SIGNING_FAILED: CUnsignedLong = 802
public let SOFTWARE_VERIFICATION_FAILED: CUnsignedLong = 803
public let SOFTWARE_PUBLIC_KEY_EXTRACTION_FAILED: CUnsignedLong = 804
public let SOFTWARE_ENCODING_FAILED: CUnsignedLong = 805
public let SOFTWARE_INVALID_KEY: CUnsignedLong = 806
public let SOFTWARE_UNSUPPORTED_KEY_TYPE_OR_CURVE: CUnsignedLong = 807
public let SOFTWARE_INVALID_COORDINATE_DATA: CUnsignedLong = 808
public let SOFTWARE_DER_TO_RAW_CONVERSION_FAILED: CUnsignedLong = 809
public let SOFTWARE_INVALID_SIGNATURE_DATA: CUnsignedLong = 810
public let SOFTWARE_INVALID_JWK_FORMAT: CUnsignedLong = 811

// MARK: - Error Messages
extension Error {
    /// 一般錯誤的訊息
    static func generalErrorMessage(for code: CUnsignedLong) -> String {
        switch code {
        case SOFTWARE_LICENSE_ASN1_ERROR:
            return "License ASN1 錯誤"
        case SOFTWARE_LICENSE_CREATE_KEY_ERROR:
            return "License Key 無法建立"
        case SOFTWARE_STRING_TO_DATA_ERROR:
            return "字串轉換 Data 失敗"
        case SOFTWARE_BASE64_TO_DATA_ERROR:
            return "Base64 字串無法轉換成資料"
        case SOFTWARE_HEX_TO_BASE64_ERROR:
            return "Hex 轉換 Base64 失敗"
        case SOFTWARE_INVALID_PARAMETER_ERROR:
            return "無效的參數"
        case SOFTWARE_VERIFY_SIGNATURE_ERROR:
            return "簽章驗證失敗"
        case SOFTWARE_LOAD_FRAMEWORK_ERROR:
            return "讀取 Framework 失敗"
        case SOFTWARE_GET_SYMBOL_ERROR:
            return "無法取得 Symbol"
            
        //DID,JWS
        case SOFTWARE_KEY_GENERATION_FAILED:
            return "無法生成新的加密金鑰"
        case SOFTWARE_KEY_NOT_FOUND:
            return "在 Secure Enclave 中找不到指定的金鑰"
        case SOFTWARE_SIGNING_FAILED:
            return "使用私鑰進行簽署時發生錯誤"
        case SOFTWARE_VERIFICATION_FAILED:
            return "無法驗證簽署的數據"
        case SOFTWARE_PUBLIC_KEY_EXTRACTION_FAILED:
            return "無法從 Secure Enclave 中提取公鑰"
        case SOFTWARE_ENCODING_FAILED:
            return "編碼失敗"
        case SOFTWARE_INVALID_KEY:
            return "無效的金鑰"
        case SOFTWARE_UNSUPPORTED_KEY_TYPE_OR_CURVE:
            return "不支援的金鑰類型或曲線"
        case SOFTWARE_INVALID_COORDINATE_DATA:
            return "無效的座標數據"
        case SOFTWARE_DER_TO_RAW_CONVERSION_FAILED:
            return "無法將 DER 簽章轉換為 raw 格式"
        case SOFTWARE_INVALID_SIGNATURE_DATA:
            return "無效的簽章資料格式"
        case SOFTWARE_INVALID_JWK_FORMAT:
            return "無效的JWK格式"
            
        default:
            return "未知錯誤 (Code: \(code))"
        }
    }
    
}
