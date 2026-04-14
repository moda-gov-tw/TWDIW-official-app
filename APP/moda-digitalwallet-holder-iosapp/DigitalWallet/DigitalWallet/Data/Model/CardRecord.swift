//
//  CardRecord.swift
//  DigitalWallet
//

import UIKit

struct CardRecord: Codable {
    /**紀錄ID*/
    let recordUUID: UUID?
    /**VC卡片ID*/
    let vcUUID: UUID?
    /**卡片紀錄種類*/
    let cardRecordType: Int64
    /// 加入卡片與卡片失效時為標題內容，VP驗證紀錄時為VP名稱
    let recordMessage: String
    /// 時間
    let createDate: Date?
    /// 授權單位
    let client: String?
    /// 授權目的
    let purpose: String?
    /// 授權資料：ex. 姓名、電話...
    let applyInfos: String?
    /// VP驗證用到的所有卡片名稱
    let vcNames: String?
    
    enum CodingKeys: String, CodingKey {
        case recordUUID
        case vcUUID = "vcId"
        case cardRecordType = "status"
        case recordMessage = "text"
        case createDate = "datetime"
        case client = "authorizationUnit"
        case purpose = "authorizationPurpose"
        case applyInfos = "authorizationField"
        case vcNames
    }
    
    var type: CardType? {
        CardType(rawValue: cardRecordType)
    }
    
    /// 展開狀態
    var isExpand: Bool = false
    
    /**取得記錄時間*/
    func getCreateDate() -> String{
        return FunctionUtil.shared.dateFormat(format: FunctionUtil.shared.YYMMDD, dateTime: createDate ?? Date())
    }
    
}

/// 卡片記錄狀態
enum CardType: Int64{
    /// 新增
    case add = 1
    /// 授權
    case verify = 2
    /// 失效
    case expired = 3
    /// 移除
    case remove = 4
    /// 修改皮夾密碼
    case changePassword = 5
    
    /// 卡片記錄圖標
    var icon: UIImage? {
        var imageName: String = ""
        switch self {
        case .add:
            imageName = "recordAddCard"
        case .verify:
            imageName = "recordAuthorized"
        case .expired:
            imageName = "recordExpired"
        case .remove:
            imageName = "RecordRemoved"
        case .changePassword:
            imageName = "ChangeIcon"
        }
        return UIImage.init(named: imageName)
    }
    
    /// 標題
    var title: String {
        switch self {
        case .add:
            return NSLocalizedString("recordAddCard", comment: "")
        case .verify:
            return NSLocalizedString("recordAuthorized", comment: "")
        case .expired:
            return NSLocalizedString("recordExpired", comment: "")
        case .remove:
            return NSLocalizedString("DeleteCard", comment: "")
        case .changePassword:
            return NSLocalizedString("UpdateWalletPinCode", comment: "")
        }
    }
    
    /// 背景底色
    var backgroundColor: UIColor {
        switch self {
        case .add:
            return .FEF_7_EF
        case .verify:
            return .F_6_F_7_FA
        case .expired, .remove, .changePassword:
            return .FCF_6_F_7
        }
    }
    
    /// 標題顏色
    var titleColor: UIColor {
        switch self {
        case .add:
            return .D_07744
        case .verify:
            return ._4_E_61_A_7
        case .expired, .remove, .changePassword:
            return .C_94_D_70
        }
    }
}
