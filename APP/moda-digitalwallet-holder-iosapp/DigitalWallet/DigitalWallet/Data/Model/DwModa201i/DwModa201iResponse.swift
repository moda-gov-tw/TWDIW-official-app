//
//  DwModa201iResponse.swift
//  DigitalWallet
//

struct DwModa201iResponse: Codable {
    // MARK: 共用欄位
    /// 模式 vc or vp
    let mode: String?
    /// 名稱
    let name: String?
    /// icon URL
    let logoUrl: String?
    
    // MARK: VC欄位
    /// 卡片申請URL
    let issuerServiceUrl: String?
    /// 是否為外開或是內崁 1: 外開網頁 2: webView
    let type: Int?
    
    // MARK: VP欄位
    /// 驗證端業務系統URL
    let verifierServiceUrl: String?
    /// 是否為靜態QRCode模式
    let isStatic: Bool?
    /// 是否為Offline模式
    let isOffline: Bool?
    /// 客製化欄位
    let custom: DwModa201iCustomItem?
}

struct DwModa201iCustomItem: Codable {
    /// 客製化欄位資料
    let customData: [DwModa201iCustomField]?
    
    enum CodingKeys: String, CodingKey {
        case customData = "fields"
    }
}

class DwModa201iCustomField: Codable {
    /// 欄位中文名稱
    let cname: String?
    /// 欄位英文名稱
    let ename: String?
    /// 客製化欄位描述
    let description: String?
    /// 欄位預設值
    var value: String?
    /// 該欄位需符合的正則
    let regex: String?
    
    enum CodingKeys: CodingKey {
        case cname
        case ename
        case description
        case value
        case regex
    }
    /// 是合法的內容
    var isLegal: Bool = true
    
    func encode(to encoder: any Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encodeIfPresent(self.cname, forKey: .cname)
        try container.encodeIfPresent(self.ename, forKey: .ename)
        try container.encodeIfPresent(self.value, forKey: .value)
    }
}
