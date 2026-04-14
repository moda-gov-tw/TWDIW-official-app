//
//  DwVerIfierMgr401iResponse.swift
//  DigitalWallet
//

import Foundation

struct DwVerIfierMgr401iResponse: Codable {
    /// 本次請求的唯一交易序號
    let transactionId: String?
    /// 可觸發數位憑證皮夾 APP 開啟的專用連結
    let deepLink: String?
    // 描述
    let description: String?
    /// 資料欄位
    let fields: [FieldItems]?
}

struct FieldItems: Codable {
    /// 欄位中文名稱
    let cname: String?
    /// 欄位英文名稱
    let ename: String?
    /// 該欄位正規表示法
    let regex: String?
}
