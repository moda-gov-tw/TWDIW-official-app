//
//  Config.swift
//  DigitalWallet
//

import UIKit

class Config: NSObject {
    /*xib name*/
    static let NAVIGATION_BAR_XIB: String = "NavigationBarViewController"//NavigationBar
    
    struct DID {
        /***/
        static let CHANNEL_NAME = "did_sdk_channel"
        /**dwsdk-101i:持有端(Holder) DID 金鑰產生*/
        static let generateKey = "generateKey"
        /**dwsdk-102i:持有端(Holder)DID 身分產生*/
        static let generateDID = "generateDID"
        /**dwsdk-103i:關楗SDK初始化*/
        static let generateKx = "initKx"
        /**dwsdk-201i:持有端解析 VC卡片申請請求與進行申請*/
        static let applyVC = "applyVC"
        /**dwsdk-301i:持有端(Holder)進行授權VC卡片資訊*/
        static let verifyVC = "verifyVC"
        /**dwsdk-302i:持有端(Holder)進行離線授權VC卡片資訊*/
        static let verifyVCOffline = "verifyVCOffline"
        /**dwsdk-401i:持有端(Holder)進行解析VP展示授權申請*/
        static let parseVPQrcode = "parseVPQrcode"
        /**dwsdk-402i:持有端(Holder)進行建立VP展示*/
        static let generateVP = "generateVP"
        /**dwsdk-501i:持有端(Holder)進行查詢VC卡片資訊*/
        static let decodeVC = "decodeVC"
        /**dwsdk-601i:持有端(Holder)下載發行端(Issuer)機關清單狀態列表*/
        static let downloadIssList = "downloadIssList"
        /**dwsdk-602i:持有端(Holder)下載VC狀態清冊列表*/
        static let downloadAllVCList = "downloadAllVCList"
        /**dwsdk-moda-101i: SDK轉發APP api*/
        static let sendRequest = "sendRequest"
        /**dwsdk-moda-201i: SDK轉發APP api*/
        static let sendJWTRequest = "sendJWTRequest"
    }
    
    struct Notification {
        /***/
        static let deviceID = "deviceID"
        /***/
        static let unsubscribeVC = "unsubscribeVC"
    }
    
    struct QRCode {
        static let scheme = "modadigitalwallet"
        static let vc = "credential_offer"
        static let vp = "authorize"
    }
    
    struct CrossApp {
        static let vc = "vc"
        static let vp = "vp"
    }
    
    // 範例網址，請替換為實際使用的網址
    static let CORPORTE_WEBSITE_URL = "https://example.com"
    /**常見問題*/
    static let CORPORTE_WEBSITE_QA_URL = "https://example.com/qa.html"
    /**問題回報網站*/
    static let QUESTION_REPORT_URL = "https://example.com/feedback"

    static var FRONTEND_URL: String {
        switch Config.environmentType {
        case .PROD:
            return "https://example.com/frontend"
        }
    }
    
    enum Environment: String {
        case PROD = "PROD"
    }
    
    /**取得當前運行環境*/
    static var environmentType: Environment {
        return .PROD
    }
    
    /// 檔案來源
    enum FileResource: String {
        /// 使用同意書
        case use_agreement_contract
        /// 隱私政策
        case privacy_contract
    }
    
    /// 檔案類型
    enum FileType: String {
        case html
    }
    
    static let userAgent = "navigator.userAgent"
    static let webViewFormate = "ModaDigitalWalletApp/%@ %@"
    
    /// 隱藏時文字
    static let hiddenText = "************"
}
