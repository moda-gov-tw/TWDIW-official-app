//
//  VerifiablePresentationCellType.swift
//  DigitalWallet
//

import Foundation

/// VP驗證Cell類型
enum VerifiablePresentationCellType {
    /// 授權資料
    /// - Parameter authorizingAgency: 授權單位
    /// - Parameter purpose: 授權目的
    case Info(authorizingAgency: String?,
              purpose: String?)
    /// 授權清單
    /// - Parameter eyeOpen: 顯示或隱藏項目
    case ListInfo(eyeOpen: Bool)
    /// 卡片內容
    /// - Parameter verifyData: 卡片群組資料
    /// - Parameter eyeOpen: 顯示或隱藏項目
    case Requirement(verifyData: VerifyGroupData, eyeOpen: Bool)
    /// 客製化欄位
    case CustomFields(field:DwModa201iCustomField, eyeOpen: Bool)
}
