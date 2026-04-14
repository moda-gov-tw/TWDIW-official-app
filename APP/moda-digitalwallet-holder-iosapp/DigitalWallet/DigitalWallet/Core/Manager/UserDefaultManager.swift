//
//  UserDefaultManager.swift
//  DigitalWallet
//

import Foundation
import CryptoKit

enum UserDefaultKeys: String {
    /// 當前預設登入的皮夾
    case DefaultLoginWallet
    /// 持有端(Holder)下載發行端(Issuer)機關清單狀態列表
    case IssList601i
    /// 持有端(Holder)下載VC狀態清冊列表
    case AllVCList602i
    /// 使用者螢幕亮度
    case Brightness
    /// 新模式
    case New601iFlag = "601iFlag"
    /// 推播Token
    case PushToken = "Push Token"
    /// dwsdk-102i:持有端(Holder)DID 身分產生
    case GenerateDID = "generateDID"
}

class UserDefaultManager {
    
    public static let shared = UserDefaultManager()
    
    private init() {}
    
    func getObject<T: Codable>(key: UserDefaultKeys, type: T.Type) -> T? {
        guard
            let data = UserDefaults.standard.value(forKey: key.rawValue) as? Data,
            let value = try? JSONDecoder().decode(T.self, from: data)
        else {
            return nil
        }
        
        return value
    }
    
    func setObject(value: Codable, key: UserDefaultKeys) {
        guard
            let jsonData = try? JSONEncoder().encode(value)
        else {
            return
        }
        
        UserDefaults.standard.setValue(jsonData, forKey: key.rawValue)
    }
    
    func removeObject(key: UserDefaultKeys) {
        UserDefaults.standard.removeObject(forKey: key.rawValue)
    }
    
    /**清除全部資料*/
    func clearUserDefaults() {
        let defaults = UserDefaults.standard
        
        if let domainName = Bundle.main.bundleIdentifier {
            defaults.removePersistentDomain(forName: domainName)
        }
        
        defaults.synchronize()
    }


}
