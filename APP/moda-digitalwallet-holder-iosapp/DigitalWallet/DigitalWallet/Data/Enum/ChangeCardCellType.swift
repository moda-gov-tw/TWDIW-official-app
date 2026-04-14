//
//  ChangeCardCellType.swift
//  DigitalWallet
//

import Foundation

/// 更改憑證的cell類型
enum ChangeCardCellType {
    /// 提示文字
    /// - Parameter message: 提示內容
    case hintMessage(message: String?)
    /// 卡片類型
    /// - Parameter cards: 卡片列表
    case cardGroup(cards: [CardInfoData])
}
