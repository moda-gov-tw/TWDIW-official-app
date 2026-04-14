import Foundation

struct ECPublicJWK: Codable {
    let kty: String  // Key Type: 固定為 "EC"
    let crv: String  // Curve: 固定為 "P-256"
    let x: String    // x 座標 (base64url 編碼)
    let y: String    // y 座標 (base64url 編碼)
    //let kid: String? // Key ID (選擇性)
    //let use: String? // Key Use (選擇性): 通常是 "sig"
    //let alg: String? // Algorithm (選擇性): 通常是 "ES256"
}

class JWSCreator {
    
    // JWS Header 結構
    struct JWSHeader: Codable {
        let alg: String
        let typ: String
        let kid: String?
        
        init(algorithm: String = "ES256", keyId: String? = nil) {
            self.alg = algorithm
            self.typ = "JWS"
            self.kid = keyId
        }
    }
    
    // Base64URL 編碼擴展
    private func base64URLEncode(_ data: Data) -> String {
        return data.base64EncodedString()
            .replacingOccurrences(of: "+", with: "-")
            .replacingOccurrences(of: "/", with: "_")
            .replacingOccurrences(of: "=", with: "")
    }

    
    //MARK: 建立 JWS
    func createJWS(header: String, payload: String, privateKey: SecKey) -> (jws: String?, error: Error?) {
        // 1. 準備 Header
        guard let headerBase64 = header.toBase64URL() else{
            return (nil, Error.fromGeneral(SOFTWARE_ENCODING_FAILED))
        }
        
        // 2. 編碼 Payload
        guard let payloadBase64 = payload.toBase64URL() else{
            return (nil, Error.fromGeneral(SOFTWARE_ENCODING_FAILED))
        }
        
        // 3. 組合待簽名字串
        let signingInput = "\(headerBase64).\(payloadBase64)"
        guard let dataToSign = signingInput.data(using: .utf8) else {
            return (nil, Error.fromGeneral(SOFTWARE_ENCODING_FAILED))
        }
        
        // 4. 使用 SecKey 進行簽名
        var error: Unmanaged<CFError>?
        guard let derSignature = SecKeyCreateSignature(
            privateKey,
            .ecdsaSignatureMessageX962SHA256,
            dataToSign as CFData,
            &error
        ) as Data? else {
            if let cfError = error {
                return (nil, Error.fromCFError(cfError, method: "createJWS"))
            }
            return (nil, Error.fromGeneral(SOFTWARE_SIGNING_FAILED))
        }
        
        // 5. 將 DER 格式轉換為 raw format
        let (rawSignature,convertError) = convertDERtoRaw(signature: derSignature)
        if let convertError = convertError {
            return (nil, convertError)
        }
        
        // 6. 組合最終的 JWS
        guard let rawSignature = rawSignature else{return (nil, Error.fromGeneral(SOFTWARE_DER_TO_RAW_CONVERSION_FAILED))}
        let signatureBase64 = base64URLEncode(rawSignature)
        let jws = "\(headerBase64).\(payloadBase64).\(signatureBase64)"

        return (jws, nil)
    }
    
    func convertDERtoRaw(signature: Data) -> (rawSignature: Data?, error: Error?) {
        // 檢查最小長度
        guard signature.count >= 8 else {
            return (nil,Error.fromGeneral(SOFTWARE_INVALID_SIGNATURE_DATA))
        }
        
        var index = 2 // 跳過序列標記(30)和長度
        
        // 讀取 R
        guard signature[index] == 0x02 else { // 確認是整數標記
            return (nil,Error.fromGeneral(SOFTWARE_INVALID_SIGNATURE_DATA))
        }
        index += 1
        let rLength = Int(signature[index])
        index += 1
        var rValue = signature[index..<(index + rLength)]
        
        // 如果 R 值有前導的 00，則移除
        if rValue.first == 0x00 {
            rValue = rValue.dropFirst()
        }
        
        // 讀取 S
        index += rLength
        guard signature[index] == 0x02 else { // 確認是整數標記
            return (nil,Error.fromGeneral(SOFTWARE_INVALID_SIGNATURE_DATA))
        }
        index += 1
        let sLength = Int(signature[index])
        index += 1
        var sValue = signature[index..<(index + sLength)]
        
        // 如果 S 值有前導的 00，則移除
        if sValue.first == 0x00 {
            sValue = sValue.dropFirst()
        }
        
        // 確保 R 和 S 都是 32 bytes (對於 P-256 曲線)
        guard rValue.count <= 32 && sValue.count <= 32 else {
            return (nil,Error.fromGeneral(SOFTWARE_INVALID_SIGNATURE_DATA))
        }
        
        // 組合 raw signature (R || S)
        var rawSignature = Data(count: 64) // 32 bytes for R + 32 bytes for S

        // 複製 R 值（對齊到右邊）
        let rOffset = 32 - rValue.count
        rawSignature.replaceSubrange(rOffset..<(rOffset + rValue.count), with: rValue)

        // 複製 S 值（對齊到右邊）
        let sOffset = 64 - sValue.count
        rawSignature.replaceSubrange(sOffset..<(sOffset + sValue.count), with: sValue)
        
        return (rawSignature,nil)
    }
    
    //MARK: JWS 驗證
    func verifyJWS(jws: String, publicKeyJWK: String) -> (isValid: Bool, error: Error?) {
        
        // 1. 先將 JWK 轉換為 SecKey
        let (publicKey, jwkError) = jwkToSecKey(jwkString: publicKeyJWK)
        if let jwkError = jwkError {
            return (false, jwkError)
        }
        
        guard let publicKey = publicKey else {
            return (false, Error.fromGeneral(SOFTWARE_INVALID_KEY))
        }
        
        // 2. 解析 JWS 組件
        let components = jws.components(separatedBy: ".")
        guard components.count == 3 else {
            return (false, Error.fromGeneral(SOFTWARE_ENCODING_FAILED))
        }
        
        // 3. 重建簽名輸入
        let signingInput = components[0] + "." + components[1]
        guard let dataToVerify = signingInput.data(using: .utf8) else {
            return (false, Error.fromGeneral(SOFTWARE_ENCODING_FAILED))
        }
        
        // 4. 解碼簽名
        let signatureBase64 = components[2].base64UrlToBase64()
        guard let rawSignature = Data(base64Encoded: signatureBase64) else {
            return (false, Error.fromGeneral(SOFTWARE_ENCODING_FAILED))
        }
        
        // 5. 將 Raw Format 轉換為 DER 格式
        guard rawSignature.count == 64 else {
            return (false, Error.fromGeneral(SOFTWARE_INVALID_SIGNATURE_DATA))
        }
        
        // 分離 R 和 S 值
        let rData = rawSignature[..<32]
        let sData = rawSignature[32...]
        
        // 構建 DER 格式
        var derSignature = Data()
        derSignature.append(0x30) // 序列標記
        
        // 為 R 值準備資料
        var rBytes = Data(rData)
        if rBytes[0] >= 0x80 {
            rBytes.insert(0x00, at: 0)
        }
        
        // 為 S 值準備資料
        var sBytes = Data(sData)
        if sBytes[0] >= 0x80 {
            sBytes.insert(0x00, at: 0)
        }
        
        // 計算總長度
        let totalLength = 2 + rBytes.count + 2 + sBytes.count
        derSignature.append(UInt8(totalLength))
        
        // 加入 R 值
        derSignature.append(0x02)
        derSignature.append(UInt8(rBytes.count))
        derSignature.append(rBytes)
        
        // 加入 S 值
        derSignature.append(0x02)
        derSignature.append(UInt8(sBytes.count))
        derSignature.append(sBytes)
        
        // 驗證簽名
        var error: Unmanaged<CFError>?
        let result = SecKeyVerifySignature(
            publicKey,
            .ecdsaSignatureMessageX962SHA256,
            dataToVerify as CFData,
            derSignature as CFData,
            &error
        )
        
        if let cfError = error {
            PrintPro.print("SecKeyVerifySignature failed:", cfError.takeRetainedValue())
            return (false, Error.fromCFError(cfError, method: "verifyJWS"))
        }
        
        return (result, nil)
    }
    
    //MARK: 解析 JWS
    func decodeJWS(_ jwsString: String) -> (decodedHeader: String?, decodedPayload: String?, decodedSignature: String) {
        let components = jwsString.components(separatedBy: ".")
        guard components.count == 3 else {
            return (nil, nil, components.last ?? "")
        }
        
        let header = components[0].base64UrlToBase64()
        let payload = components[1].base64UrlToBase64()
        
        if let headerData = Data(base64Encoded: header),
           let headerString = String(data: headerData, encoding: .utf8),
           let payloadData = Data(base64Encoded: payload),
           let payloadString = String(data: payloadData, encoding: .utf8) {
            return (headerString, payloadString, components[2])
        }
        
        return (nil, nil, components[2])
    }
    
    //MARK: SecKey 公鑰轉 JWK
    func publicKeyToJWK(publicKey: SecKey) -> (jwk: String?, error: Error?) {
        // 1. 從 SecKey 取得原始公鑰資料
        var error: Unmanaged<CFError>?
        guard let publicKeyData = SecKeyCopyExternalRepresentation(publicKey, &error) as Data? else {
            if let cfError = error {
                return (nil, Error.fromCFError(cfError, method: "publicKeyToJWK"))
            }
            return (nil, Error.fromGeneral(SOFTWARE_INVALID_KEY))
        }
        
        // 2. 解析 x, y 座標
        guard publicKeyData.count == 65 else {
            return (nil,Error.fromGeneral(SOFTWARE_INVALID_KEY))
        }
        
        let x = publicKeyData.subdata(in: 1..<33)
        let y = publicKeyData.subdata(in: 33..<65)
        
        // 3. 建立 JWK
        let jwk = ECPublicJWK(
            kty: "EC",
            crv: "P-256",
            x: base64URLEncode(x),
            y: base64URLEncode(y)
        )
        
        let (jwkString,jwkError) = jwkToJsonString(jwk)
        if let  jwkError = jwkError{
            return (nil, jwkError)
        }
        return (jwkString, nil)
    }
    
    func publicKeyToJWK(publicKeyData: Data) -> (jwk: String?, error: Error?) {
       guard let tlvData = parseTLV(data: publicKeyData) else {
           return (nil, Error.fromGeneral(SOFTWARE_INVALID_KEY))
       }
       
       guard let ecPoint = extractECPoint(from: tlvData) else {
           return (nil, Error.fromGeneral(SOFTWARE_INVALID_KEY))
       }
       
       guard ecPoint.count == 65, ecPoint[0] == 0x04 else {
           return (nil, Error.fromGeneral(SOFTWARE_INVALID_KEY))
       }
       
       let x = ecPoint.subdata(in: 1..<33)
       let y = ecPoint.subdata(in: 33..<65)
       
       let jwk = ECPublicJWK(
           kty: "EC",
           crv: "P-256",
           x: base64URLEncode(x),
           y: base64URLEncode(y)
       )
       
       let (jwkString, jwkError) = jwkToJsonString(jwk)
       if let jwkError = jwkError {
           return (nil, jwkError)
       }
       return (jwkString, nil)
    }
    
    private func parseTLV(data: Data) -> Data? {
        // 先尋找 86 41 04 (實際公鑰開始的標記)
        if let range = data.range(of: Data([0x86, 0x41, 0x04])) {
            let startIndex = range.lowerBound + 2  // 跳過 86 41，保留 04
            return data.subdata(in: startIndex..<data.endIndex)
        }
        
        // 備用方案：尋找 84 41 04
        if let range = data.range(of: Data([0x84, 0x41, 0x04])) {
            let startIndex = range.lowerBound + 2
            return data.subdata(in: startIndex..<data.endIndex)
        }
        
        return nil
    }

    private func extractECPoint(from data: Data) -> Data? {
       guard data.count >= 65 else { return nil }
       return data.prefix(65)
    }

    
    //MARK:  將 JWK 轉換為 JSON 字串
    func jwkToJsonString(_ jwk: ECPublicJWK) -> (jsonString: String?, error: Error?) {
        let encoder = JSONEncoder()
        //encoder.outputFormatting = .prettyPrinted //顯示跳脫字元
        
        // 嘗試編碼
        guard let data = try? encoder.encode(jwk) else {
            return (nil, Error.fromGeneral(SOFTWARE_ENCODING_FAILED))
        }
        
        // 轉換為字串
        guard let jsonString = String(data: data, encoding: .utf8) else {
            return (nil, Error.fromGeneral(SOFTWARE_ENCODING_FAILED))
        }
        
        return (jsonString, nil)
    }
    
    //MARK: JWK 轉 SecKey
    func jwkToSecKey(jwkString: String) -> (key: SecKey?, error: Error?) {
        // 1. 解析 JWK JSON 字串
        guard let jwkData = jwkString.data(using: .utf8),
              let jwk = try? JSONDecoder().decode(ECPublicJWK.self, from: jwkData) else {
            return (nil, Error.fromGeneral(SOFTWARE_INVALID_JWK_FORMAT))
        }
        
        // 2. 檢查 JWK 參數
        guard jwk.kty == "EC",
              jwk.crv == "P-256" else {
            return (nil, Error.fromGeneral(SOFTWARE_UNSUPPORTED_KEY_TYPE_OR_CURVE))
        }
        
        // 3. 解碼 x, y 座標
        guard let xData = Data(base64URLString: jwk.x),
              let yData = Data(base64URLString: jwk.y),
              xData.count == 32,
              yData.count == 32 else {
            return (nil, Error.fromGeneral(SOFTWARE_INVALID_COORDINATE_DATA))
        }
        
        // 4. 組合成 P-256 公鑰格式
        var publicKeyData = Data([0x04])
        publicKeyData.append(xData)
        publicKeyData.append(yData)
        
        // 5. 設定金鑰參數
        let attributes: [String: Any] = [
            kSecAttrKeyType as String: kSecAttrKeyTypeECSECPrimeRandom,
            kSecAttrKeyClass as String: kSecAttrKeyClassPublic,
            kSecAttrKeySizeInBits as String: 256
        ]
        
        // 6. 創建 SecKey
        var error: Unmanaged<CFError>?
        guard let secKey = SecKeyCreateWithData(
            publicKeyData as CFData,
            attributes as CFDictionary,
            &error
        ) else {
            if let cfError = error {
                return (nil, Error.fromCFError(cfError, method: "jwkToSecKey"))
            }
            return (nil, Error.fromGeneral(SOFTWARE_KEY_GENERATION_FAILED))
        }
        
        return (secKey, nil)
    }
    
    func jwkToSecKey(jwk: ECPublicJWK) -> (key: SecKey?, error: Error?) {
        // 1. 檢查 JWK 參數
        guard jwk.kty == "EC",
              jwk.crv == "P-256" else {
            return (nil, Error.fromGeneral(SOFTWARE_UNSUPPORTED_KEY_TYPE_OR_CURVE))
        }
        
        // 2. 解碼 x, y 座標
        guard let xData = Data(base64URLString: jwk.x),
              let yData = Data(base64URLString: jwk.y),
              xData.count == 32,
              yData.count == 32 else {
            return (nil, Error.fromGeneral(SOFTWARE_INVALID_COORDINATE_DATA))
        }
        
        // 3. 組合成 P-256 公鑰格式
        var publicKeyData = Data([0x04])
        publicKeyData.append(xData)
        publicKeyData.append(yData)
        
        // 4. 設定金鑰參數
        let attributes: [String: Any] = [
            kSecAttrKeyType as String: kSecAttrKeyTypeECSECPrimeRandom,
            kSecAttrKeyClass as String: kSecAttrKeyClassPublic,
            kSecAttrKeySizeInBits as String: 256
        ]
        
        // 5. 創建 SecKey
        var error: Unmanaged<CFError>?
        guard let secKey = SecKeyCreateWithData(
            publicKeyData as CFData,
            attributes as CFDictionary,
            &error
        ) else {
            if let cfError = error {
                return (nil, Error.fromCFError(cfError, method: "jwkToSecKey"))
            }
            return (nil, Error.fromGeneral(SOFTWARE_KEY_GENERATION_FAILED))
        }
        return (secKey, nil)
    }
}
