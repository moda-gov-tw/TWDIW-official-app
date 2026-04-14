//
//  VCCardApplyListResponse.swift
//  DigitalWallet
//

import Foundation

struct VCCardApplyListResponse: Codable {
    /// VC卡片清單
    let vcItems: [VCItems]?
    /// 目前頁碼
    let currentPage: Int?
    /// 每頁size
    let pageSize: Int?
    /// VC卡片總數
    let totalItems: Int?
    /// 分頁總數
    let totalPages: Int?
}

struct VCItems: Codable, NameSortableProtocol {
    enum UrlOpenType: Int {
        case openUrl = 1
        case embedWebView = 2
        case none = 999
    }
    
    /// 統編_模板代碼
    let vcUid: String?
    /// VC卡片名稱
    let name: String?
    /// VC卡片申請開啟方式
    /// 1: 外開網頁
    /// 2: webview
    let type: Int?
    /// 組織 Logo URL
    let logoUrl: String?
    /// VC申請網頁的URL
    let issuerServiceUrl: String?
    /// 開啟類型
    var openType: UrlOpenType {
        guard let result = UrlOpenType(rawValue: type ?? 999) else {
            return UrlOpenType.none
        }
        return result
    }
    
    enum CodingKeys: CodingKey {
        case vcUid
        case name
        case type
        case logoUrl
        case issuerServiceUrl
    }
}
