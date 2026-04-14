//
//  VerifiableCardStatus.swift
//  DigitalWallet
//

import Foundation

/// VP驗證卡片狀態
enum VerifiableCardStatus {
    /// 未選擇卡片
    case unselect
    /// 無可授權卡片
    case noAuthorized
    /// 已授權
    case authorized
}
