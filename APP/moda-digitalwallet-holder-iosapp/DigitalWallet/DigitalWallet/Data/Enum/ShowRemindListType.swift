//
//  ShowRemindListType.swift
//  DigitalWallet
//

import Foundation

enum ShowRemindListType {
    /// 更新失敗
    case networkError
    /// 即將失效
    case expiringList([Remind])
    /// 失效
    case expiredList([Remind])
    /// 更新失敗
    case updateErrorList([Remind])
}
