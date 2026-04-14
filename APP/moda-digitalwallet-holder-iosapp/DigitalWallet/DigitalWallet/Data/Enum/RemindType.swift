//
//  RemindType.swift
//  DigitalWallet
//

import Foundation

enum Remind {
    /// 網路錯誤
    case networkError
    /// 到期通知
    case remind(UserVerifiableCredentailData)
    /// 更新失敗
    case updateError(UserVerifiableCredentailData)
}
