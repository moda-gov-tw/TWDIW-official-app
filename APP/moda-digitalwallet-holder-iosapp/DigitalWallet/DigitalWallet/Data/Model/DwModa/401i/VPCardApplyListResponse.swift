//
//  Untitled.swift
//  DigitalWallet
//

struct VPCardApplyListResponse: Codable {
    /// 驗證情境清單
    let vpItems: [VPItems]?
    let currentPage: Int?
    let pageSize: Int?
    let totalItems: Int?
    let totalPages: Int?
}

struct VPItems: Codable, NameSortableProtocol {
    /// 統編_模板代碼
    let vpUid: String?
    /// VP情境名稱
    var name: String?
    /// 驗證端模組URL
    let verifierModuleUrl: String?
    /// 組織 Logo URL
    let logoUrl: String?
}
