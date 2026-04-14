//
//  CardStatus.swift
//  DigitalWallet
//


/// 卡片狀態
enum CardStatus: Int16 {
    /// 未授權
    case unverified = 0
    /// 已授權
    case verified = 1
    /// 失效
    case invalidation = 2
}
