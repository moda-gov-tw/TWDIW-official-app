//
//  CrossAppStatus.swift
//  DigitalWallet
//

import Foundation

/// CrossApp狀態
enum CrossAppStatus: Equatable {
    /// 閒置
    case idle
    /// Universal Link
    case crossAppActive(url: URL, isShowFullScreen: Bool)
}
