//
//  VerifiableManagerProtocol.swift
//  DigitalWallet
//

import Foundation

protocol VerifiableManagerProtocol {
    
    /// 依序呼叫 103i, 101i, 102i，進行身分建立
    /// - Parameter keyTag: 皮夾的uuid string
    /// - Returns: DIDDataResponse 作為後續流程會使用到的資料
    func createDecentralizedIdentifier(uuid keyTag: String) async throws -> DIDDataResponse
    
    /// 初始化關鍵SDK，帶入對應皮夾的UUID
    /// - Parameter keyTag: 對應皮夾的UUID
    func initializationKxSDK(walletUUID keyTag: String) async throws
    
    /// 透過qrcode加入卡片
    /// - Parameters:
    ///   - qrcode: 動態qrcode
    ///   - didDataString: 使用者的DID身分
    ///   - otp: 單次驗證碼，確保為本人使用
    /// - Returns: 成功加卡後的資料
    func createVerifiableCredential(qrcode: String,
                                    didDataString: String,
                                    otp: String) async throws -> ApplyVCDataResponse
    
    /**dwsdk-301i:持有端(Holder)進行授權VC卡片資訊*/
    func verifyVC(applyVCData: ApplyVCDataResponse, didDataString: String) async throws -> VerifyVCResponse?
    /**dwsdk-302i:持有端(Holder)進行離線授權VC卡片資訊*/
    func verifyVCOffline(applyVCData: ApplyVCDataResponse, issList: Array<DownloadIssListResponse>, vcList: Array<Dictionary<String, Array<Dictionary<String, String>>>>, didDataString: String) async throws -> VerifyVCResponse?
    /**dwsdk-401i:持有端(Holder)進行解析VP展示授權申請*/
    func parseVerifiablePresentation(qrcode: String) async throws -> ParseVPDataResponse
    
    func verifyVerifiablePresentationWithKx(parseData: ParseVPDataResponse, selectVCs: [SelectVC], didDataString: String, customData: VPCustomData?) async throws -> Bool
    /**dwsdk-501i:持有端(Holder)進行查詢VC卡片資訊*/
    func getVerifiableCredential(applyVCData: ApplyVCDataResponse) async throws -> DecodeVCDataResponse
    /**dwsdk-601i:持有端(Holder)下載發行端(Issuer)機關清單狀態列表*/
    func downloadIssList() async throws -> [DownloadIssListResponse]
    /**dwsdk-602i:持有端(Holder)下載VC狀態清冊列表*/
    func downloadAllVCList(vcs: Array<String>, vcList: Array<Dictionary<String, Array<VCJWTAndSHA256Hash>>>) async throws -> DownloadAllVCListResponse
    /**dwsdk-moda-101i: SDK轉發APP api(無payload)*/
    func requestFromSDK<T: Codable, RS: Codable>(url: String, type: RequestMethod, bodyModel: T) async throws -> RS
    /**dwsdk-moda-101i: SDK轉發APP api (payload)*/
    func requestPayloadFromSDK<T: Codable>(type: RequestMethod,
                                           payload: T,
                                           url: String,
                                           didFile: String) async throws -> DwVerIfierMgr402iResponse
}
