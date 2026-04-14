//
//  ParseLinkResult.swift
//  DigitalWallet
//

import Foundation

/// 解析Link結果
enum ParseLinkResult {
    /// 解析失敗
    case parseError
    /// 1. 取得 `dwsdk-201i:持有端解析VC卡片申請請求與進行申請` 結果
    /// 2. 帶入 `dwsdk-201i` 取得 `dwsdk-301i:持有端(Holder)進行授權VC卡片資訊` 取得驗證結果
    /// 3. 帶入 `dwsdk-201i` 取得 `dwsdk-501i:持有端(Holder)進行查詢VC卡片資訊 ` `iss`
    /// 4. 由`dwsdk-601i:持有端(Holder)下載發行端(Issuer)機關清單狀態列表` 取得 `issList` 對照 `iss` 取得 `orgName`
    /// 5. 由 `dwsdk-501i:持有端(Holder)進行查詢VC卡片資訊` 取得 `displayName(顯示名稱)`
    /// 6. 由 `dwsdk-201i`取得透過 `FunctionUtil` 取得背景圖片
    /// 7. 將 `dwsdk-201i` 結果、`orgName`、`背景圖片`、`displayName(顯示名稱)`存入DB`VerifiableCredential` Table
    case parseVC(qrcode: String)
    /// 1. 取得 `dwsdk-401i 持有端(Holder)進行解析VP展示授權申請` 結果
    /// 2. 將結果轉換成 `VerifiableData`
    case parseVP(qrcode: String)
    /// 1. 呼叫 `DwModa201i` 帶入 `vcUID` 取得 `DwModa201iResponse`
    /// 2. 依據 `DwModa201iResponse` 的 `type` 判斷走外開webView，還是內嵌webView，並帶入`issuerServiceUrl`開啟webView
    /// 3. 如果為外開流程會走`parseVC`流程會來，若為內嵌流程，會透過`Javascript`回傳`deepLink`，一樣走上方`parseVC`流程
    case staticVC(vcUID: String)
    
    case staticVP(vpUID: String)
}
