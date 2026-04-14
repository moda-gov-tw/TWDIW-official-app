//
//  CustomPickerItem.swift
//  DigitalWallet
//

import Foundation

struct CustomPickerItem {
    /// 類型
    enum ItemStatus {
        /// 一般
        case normal
        /// 刪除
        case delete
        /// 憑證紀錄
        case certificateLog
    }
    
    /// 選項名稱
    var name: String
    /// 選項類型
    var status: ItemStatus = .normal
    /// tag
    var tag: String?
}
