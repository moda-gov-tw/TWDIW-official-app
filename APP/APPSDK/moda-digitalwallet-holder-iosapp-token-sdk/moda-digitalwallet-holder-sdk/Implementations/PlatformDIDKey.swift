import Foundation
import Security
import CoreFoundation

class PlatformDIDKey: ModaDidKeyProtocol {
    private var keyTag: String
    private var privateKey: SecKey?
    
    init(keyTag: String, pn: Data) {
        self.keyTag = keyTag
        self.privateKey = nil
    }
    
    deinit {
        secureClear()
    }
    
    func secureClear() {
        privateKey = nil
        
        let _ = keyTag.withUTF8 { ptr in
            memset(UnsafeMutableRawPointer(mutating: ptr.baseAddress!), 0, ptr.count)
        }
        keyTag = ""
    }
    
    func getP256Key() -> (String?, Error?) {
        PrintPro.print("platform")
        
        // 1. 檢查現有金鑰
        let (existingKey, error) = checkExistingKey()
        
        // 如果找到現有金鑰，直接返回
        if let publicKey = existingKey {
            let jwsCreator = JWSCreator()
            let (jwkKey,error) = jwsCreator.publicKeyToJWK(publicKey: publicKey)
            if let  error = error{
                return (nil, error)
            }
            return (jwkKey, nil)
        }
        
        // 如果錯誤不是 "找不到金鑰"，則返回錯誤
        if let error = error,
           let status = error.originalStatus,
           status != errSecItemNotFound {
            PrintPro.print("error:\(error),status:\(status)")
            return (nil, error)
        }
        
        // 2. 生成新金鑰
        return generateNewKey()
    }
    
    func sign(header:String, payload:String) -> (String?, Error?) {
        PrintPro.print("platform")
        
        let (publicKey, checkExistingKeyError) = self.checkExistingKey()
        if let checkExistingKeyError = checkExistingKeyError {
            return (nil, checkExistingKeyError)
        }
        
        guard let privateKey = self.privateKey else {
            let error = Error.fromGeneral(SOFTWARE_KEY_NOT_FOUND)
            return (nil, error)
        }
        
        let algorithm: SecKeyAlgorithm = .ecdsaSignatureMessageX962SHA256
        guard SecKeyIsAlgorithmSupported(privateKey, .sign, algorithm) else {
            let error = Error.fromGeneral(SOFTWARE_SIGNING_FAILED)
            return (nil, error)
        }
        
        let jwsCreator = JWSCreator()
        let (jwsString,jwsError) = jwsCreator.createJWS(header: header, payload: payload, privateKey: privateKey)
        
        return (jwsString, jwsError)
    }
    
    func delete() -> (Bool, Error?) {
        PrintPro.print("platform")
        
        let query: [String: Any] = [
            kSecClass as String: kSecClassKey,
            kSecAttrApplicationTag as String: self.keyTag.data(using: .utf8)!,
            kSecAttrKeyType as String: kSecAttrKeyTypeECSECPrimeRandom,
            kSecAttrTokenID as String: kSecAttrTokenIDSecureEnclave
        ]
        
        let status = SecItemDelete(query as CFDictionary)
        
        if status == errSecSuccess {
            return (true, nil)
        } else {
            let error = SecurityErrorUtil.createFromOSStatus(status: status)
            return (false, error)
        }
    }
    
    func verifyUser(publicKey: String) -> (Bool, Error?) {
        PrintPro.print("platform")
        
        // JWK 轉換成 SecKey
        let jwsCreator = JWSCreator()
        let (publicSecKey,secKeyError) = jwsCreator.jwkToSecKey(jwkString: publicKey)
        
        guard let publicSecKey = publicSecKey else {
            return (false, secKeyError)
        }
        
        let (secureEnclavePublicKey, checkExistingKeyError) = self.checkExistingKey()
        if let checkExistingKeyError = checkExistingKeyError {
            return (false, checkExistingKeyError)
        }
        
        guard let secureEnclavePublicKey = secureEnclavePublicKey else {
            return (false, checkExistingKeyError)
        }
        
        var unmanagedError: Unmanaged<CFError>?
        
        // 將公鑰轉換成外部表示形式(通常是 DER 格式)
        guard let data1 = SecKeyCopyExternalRepresentation(secureEnclavePublicKey, &unmanagedError) as Data?,
              let data2 = SecKeyCopyExternalRepresentation(publicSecKey, &unmanagedError) as Data? else {
            
            if let error = unmanagedError {
                return (false, Error.fromCFError(error, method: "verifyUser"))
            }
            return (false, Error.fromGeneral(SOFTWARE_VERIFICATION_FAILED))
        }
        
        // 直接比較兩個數據是否相同
        let result = data1 == data2
        return  (result,nil)
    }
    
    
    // MARK: - Private Methods
    private func checkExistingKey() -> (SecKey?, Error?) {
        let query: [String: Any] = [
            kSecClass as String: kSecClassKey,
            kSecAttrApplicationTag as String: self.keyTag.data(using: .utf8)!,
            kSecAttrKeyType as String: kSecAttrKeyTypeECSECPrimeRandom,
            kSecReturnRef as String: true,
            kSecAttrTokenID as String: kSecAttrTokenIDSecureEnclave
        ]
        
        var item: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &item)
        
        PrintPro.print("checkExistingKey status:\(status)")
        /*
         errSecSuccess (0): 成功
         errSecItemNotFound (-25300): 找不到項目
         errSecDecode (-26275): 解碼錯誤
         errSecAuthFailed (-25293): 認證失敗
         */
        switch status {
        case errSecSuccess:
            if let privateKey = item as! SecKey? {
                guard let publicKey = SecKeyCopyPublicKey(privateKey) else {
                    let error = Error.fromGeneral(SOFTWARE_PUBLIC_KEY_EXTRACTION_FAILED)
                    return (nil, error)
                }
                self.privateKey = privateKey
                PrintPro.print("金鑰已存在")
                return (publicKey, nil)
            }
            let error = Error.fromGeneral(SOFTWARE_KEY_NOT_FOUND)
            return (nil, error)
            
        case errSecItemNotFound:
            return (nil, SecurityErrorUtil.createFromOSStatus(status: status))
        default:
            return (nil, SecurityErrorUtil.createFromOSStatus(status: status))
        }
    }
    
    private func generateNewKey() -> (String?, Error?) {
        // 1. 創建訪問控制
        guard let access = SecAccessControlCreateWithFlags(
            kCFAllocatorDefault,
            kSecAttrAccessibleWhenUnlockedThisDeviceOnly,
            [.privateKeyUsage],
            nil
        ) else {
            return (nil, Error.fromGeneral(SOFTWARE_KEY_GENERATION_FAILED))
        }
        
        // 2. 設定金鑰生成參數
        let attributes: [String: Any] = [
            kSecAttrKeyType as String: kSecAttrKeyTypeECSECPrimeRandom,
            kSecAttrKeySizeInBits as String: 256,
            kSecAttrTokenID as String: kSecAttrTokenIDSecureEnclave,
            kSecPrivateKeyAttrs as String: [
                kSecAttrIsPermanent as String: true,
                kSecAttrApplicationTag as String: keyTag.data(using: .utf8)!,
                kSecAttrAccessControl as String: access
            ]
        ]
        
        // 3. 生成金鑰對
        var cfError: Unmanaged<CFError>?
        guard let privateKey = SecKeyCreateRandomKey(attributes as CFDictionary, &cfError) else {
            if let error = cfError {
                return (nil, Error.fromCFError(error, method: "cfError"))
            }
            return (nil, Error.fromGeneral(SOFTWARE_KEY_GENERATION_FAILED))
        }
        
        self.privateKey = privateKey
        
        // 4. 取得並返回公鑰
        guard let publicKey = SecKeyCopyPublicKey(privateKey) else {
            return (nil, Error.fromGeneral(SOFTWARE_PUBLIC_KEY_EXTRACTION_FAILED))
        }
        
        let jwsCreator = JWSCreator()
        let (publicKeyString,error) = jwsCreator.publicKeyToJWK(publicKey: publicKey)
        
        return (publicKeyString, error)
    }
    
}

private class SecurityErrorUtil {
    
    static func createFromOSStatus(status: OSStatus) -> Error? {
        let error = Error.fromOSStatus(status)
        return error
    }
    
    static func convertToError(_ cfError: CFError) -> Error? {
        return NSError(domain: CFErrorGetDomain(cfError) as String,
                      code: CFErrorGetCode(cfError),
                      userInfo: (CFErrorCopyUserInfo(cfError) as? [String: Any]) ?? [:]) as? Error
    }
    
}

