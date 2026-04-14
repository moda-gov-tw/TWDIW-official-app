//
//  BiometricVerifyManager.swift
//  DigitalWallet
//

import Foundation
import LocalAuthentication

protocol BiometricVerifyProtocol {
    
    /// 是否可進行生物辨識驗證
    /// - Parameter policy: 評估類別
    func canEvaluatePolicy(policy: LAPolicy) -> Bool
    
    /// 生物辨識驗證
    /// - Parameters:
    ///   - policy: 評估類別
    ///   - message: 顯示訊息
    ///   - completionHandler: 結果 是否成功 以及 錯誤訊息
    func verify(policy: LAPolicy, message: String , completionHandler: @escaping (_ success: Bool, _ error: Error?) -> Void)
}

struct BiometricLocalizedReason {
    static let verify = "驗證使用者"
}

class BiometricVerifyManager: BiometricVerifyProtocol {
    
    /// 是否可進行生物辨識驗證
    /// - Parameter policy: 評估類別
    func canEvaluatePolicy(policy: LAPolicy) -> Bool {
        let context = LAContext()
        return context.canEvaluatePolicy(policy, error: nil)
    }
    
    /// 生物辨識驗證
    /// - Parameters:
    ///   - policy: 評估類別
    ///   - message: 顯示訊息
    ///   - completionHandler: 結果 是否成功 以及 錯誤訊息
    func verify(policy: LAPolicy, message: String, completionHandler: @escaping (Bool, (any Error)?) -> Void) {
        let context = LAContext()
        context.evaluatePolicy(policy, localizedReason: message) { isSuccess, error in
            completionHandler(isSuccess, error)
        }
    }
}
