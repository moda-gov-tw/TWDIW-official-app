import Foundation

/// JWK金鑰比對器類別
class JWKComparer {
    
    /// 表示不同類型的JWK資料
    enum JWKType {
        case ec
        case rsa
        case oct
        case okp
        case unknown
    }
    
    /// EC類型的JWK結構
    struct ECJWK: Equatable {
        let kty: String
        let crv: String
        let x: String
        let y: String
        
        static func == (lhs: ECJWK, rhs: ECJWK) -> Bool {
            return lhs.kty == rhs.kty &&
                   lhs.crv == rhs.crv &&
                   lhs.x == rhs.x &&
                   lhs.y == rhs.y
        }
    }
    
    /// RSA類型的JWK結構
    struct RSAJWK: Equatable {
        let kty: String
        let n: String
        let e: String
        
        static func == (lhs: RSAJWK, rhs: RSAJWK) -> Bool {
            return lhs.kty == rhs.kty &&
                   lhs.n == rhs.n &&
                   lhs.e == rhs.e
        }
    }
    
    /// 比較兩個JWK JSON字串是否表示相同的金鑰
    /// - Parameters:
    ///   - jwk1String: 第一個JWK JSON字串
    ///   - jwk2String: 第二個JWK JSON字串
    /// - Returns: 如果兩個JWK表示相同的金鑰則返回true
    static func areEqual(jwk1String: String, jwk2String: String) -> Bool {
        guard let jwk1Data = jwk1String.data(using: .utf8),
              let jwk2Data = jwk2String.data(using: .utf8) else {
            return false
        }
        
        do {
            guard let jwk1Json = try JSONSerialization.jsonObject(with: jwk1Data) as? [String: Any],
                  let jwk2Json = try JSONSerialization.jsonObject(with: jwk2Data) as? [String: Any] else {
                return false
            }
            
            return areEqual(jwk1Json: jwk1Json, jwk2Json: jwk2Json)
        } catch {
            PrintPro.print("JSON解析錯誤: \(error)")
            return false
        }
    }
    
    /// 比較兩個JWK字典是否表示相同的金鑰
    /// - Parameters:
    ///   - jwk1Json: 第一個JWK字典
    ///   - jwk2Json: 第二個JWK字典
    /// - Returns: 如果兩個JWK表示相同的金鑰則返回true
    static func areEqual(jwk1Json: [String: Any], jwk2Json: [String: Any]) -> Bool {
        guard let kty1 = jwk1Json["kty"] as? String,
              let kty2 = jwk2Json["kty"] as? String,
              kty1 == kty2 else {
            return false
        }
        
        let jwkType = getJWKType(kty: kty1)
        
        switch jwkType {
        case .ec:
            return compareECJWK(jwk1Json, jwk2Json)
        case .rsa:
            return compareRSAJWK(jwk1Json, jwk2Json)
        default:
            // 對於其他類型，可以擴展此方法
            return false
        }
    }
    
    /// 比較兩個EC類型的JWK
    private static func compareECJWK(_ jwk1: [String: Any], _ jwk2: [String: Any]) -> Bool {
        guard let crv1 = jwk1["crv"] as? String,
              let crv2 = jwk2["crv"] as? String,
              let x1 = jwk1["x"] as? String,
              let x2 = jwk2["x"] as? String,
              let y1 = jwk1["y"] as? String,
              let y2 = jwk2["y"] as? String else {
            return false
        }
        
        let ecJwk1 = ECJWK(kty: "EC", crv: crv1, x: x1, y: y1)
        let ecJwk2 = ECJWK(kty: "EC", crv: crv2, x: x2, y: y2)
        
        return ecJwk1 == ecJwk2
    }
    
    /// 比較兩個RSA類型的JWK
    private static func compareRSAJWK(_ jwk1: [String: Any], _ jwk2: [String: Any]) -> Bool {
        guard let n1 = jwk1["n"] as? String,
              let n2 = jwk2["n"] as? String,
              let e1 = jwk1["e"] as? String,
              let e2 = jwk2["e"] as? String else {
            return false
        }
        
        let rsaJwk1 = RSAJWK(kty: "RSA", n: n1, e: e1)
        let rsaJwk2 = RSAJWK(kty: "RSA", n: n2, e: e2)
        
        return rsaJwk1 == rsaJwk2
    }
    
    /// 從kty值確定JWK類型
    private static func getJWKType(kty: String) -> JWKType {
        switch kty.uppercased() {
        case "EC":
            return .ec
        case "RSA":
            return .rsa
        case "OCT":
            return .oct
        case "OKP":
            return .okp
        default:
            return .unknown
        }
    }
    
    /// 提取JWK中的特定字段
    /// - Parameters:
    ///   - jwkString: JWK JSON字串
    ///   - field: 要提取的字段名稱
    /// - Returns: 字段值，若不存在則返回nil
    static func extractField(from jwkString: String, field: String) -> String? {
        guard let jwkData = jwkString.data(using: .utf8) else {
            return nil
        }
        
        do {
            guard let jwkJson = try JSONSerialization.jsonObject(with: jwkData) as? [String: Any] else {
                return nil
            }
            
            return jwkJson[field] as? String
        } catch {
            PrintPro.print("JSON解析錯誤: \(error)")
            return nil
        }
    }
}
