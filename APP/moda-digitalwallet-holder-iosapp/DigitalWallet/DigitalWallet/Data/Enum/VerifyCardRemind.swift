//
//  VerifyCardRemind.swift
//  DigitalWallet
//

import Foundation

enum VerifyCardRemind: Int64 {
    /// 有效
    case effective = 0
    /// 七天到期
    case sevenDays = 7
    /// 一天到期
    case oneDay = 1
    /// 失效
    case invalid = -1
}
