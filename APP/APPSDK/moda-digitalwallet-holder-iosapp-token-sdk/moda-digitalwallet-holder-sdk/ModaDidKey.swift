import Foundation
import Security
import CryptoKit
import CoreFoundation

protocol ModaDidKeyProtocol {
    func getP256Key() -> (String?, Error?)
    func sign(header:String, payload:String) -> (String?, Error?)
    func delete() -> (Bool, Error?)
    func verifyUser(publicKey: String) -> (Bool, Error?)
}

public enum SourceType: String {
    case platform = "platform"
}

public class ModaDidKey: ModaDidKeyProtocol {
    private var implementation: ModaDidKeyProtocol
    
    /// 初始化。
    ///
    /// - Parameters:
    ///   - keyTag: 金鑰識別標籤（用於查找既有金鑰或建立新金鑰時的命名/標記）。
    ///   - type: 金鑰來源類型（`.platform` ）。
    ///   - pin: 使用者 PIN。用於解鎖/驗證對應的安全元件或模組。
    public init(keyTag: String, type: SourceType, pin: Data) {
        switch type {
        case .platform:
            self.implementation = PlatformDIDKey(keyTag: keyTag, pn: pin)
        }
    }
    
    /// 讀取既有的 P-256 公鑰（JWK 格式字串）。
    ///
    /// - Returns: `(jwk, error)` — `jwk` 為 JWK 公鑰字串（例如 JSON）；查無則為 `nil`，`error` 為失敗時的錯誤。
    public func getP256Key() -> (String?, Error?) {
        return implementation.getP256Key()
    }
    
    /// 以指定公鑰對 header/payload 進行簽章，回傳簽章結果。
    ///
    /// - Parameters:
    ///   - header: 符合你方定義的 JWS/JWT header 字串。
    ///   - payload: 待簽內容。
    /// - Returns: `(signature, error)` — `signature` 為簽章輸出（JWS），`error` 為失敗時的錯誤。
    public func sign(header:String, payload:String) -> (String?, Error?){
        return implementation.sign(header: header, payload: payload)
    }
    
    /// 刪除金鑰（包含私鑰）。
    ///
    /// - Returns: `(success, error)` — `success` 表示是否成功刪除，`error` 為失敗時的錯誤。
    public func delete() -> (Bool, Error?) {
        return implementation.delete()
    }
    
    /// 針對指定 `publicKey` 進行使用者驗證，確認該金鑰存在。
    ///
    /// - Parameter publicKey: 目標公鑰的識別（JWK 字串）。
    /// - Returns: `(verified, error)` — `verified` 表示驗證是否通過，`error` 為驗證過程中的錯誤。
    public func verifyUser(publicKey: String) -> (Bool, Error?) {
        return implementation.verifyUser(publicKey: publicKey)
    }
    
}
