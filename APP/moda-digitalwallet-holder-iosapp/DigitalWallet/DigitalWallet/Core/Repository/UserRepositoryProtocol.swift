//
//  UserRepositoryProtocol.swift
//  DigitalWallet
//

import Combine
import Foundation

protocol UserRepositoryProtocol {
    var databaseManager: DatabaseModelProtocol {get}
    var verifiableManager: VerifiableManagerProtocol {get}
    var wallet: MyWallet { get }
    var vp05Info: VP05DeepLinkInformation? { get }
    var vp05Soruce: VPSource? { get }
    var remindData: [Remind]? { get }
    var updateErrorList: [UserVerifiableCredentailData]? { get }
    
    func getVerifiableCredential(qrcode: String, otp: String) async throws -> ApplyVCDataResponse
    func saveVerifiableCredential(verifiableCredential vc: ApplyVCDataResponse, state: CardStatus, iss: String, orgName: String, cardImage: Data?, remind: Int64, exp: Int64, displayName: String?, trustBadge: Bool)
    func updateVerifiableCredentialStatus(cardId: UUID, state: CardStatus)
    func updateVerifiableCredentialRemind(cardId: UUID, remind: Int)
    func getIss(verifiableCredential vc: ApplyVCDataResponse) async throws -> String
    func downloadIssList() async throws
    func updateAllVCList() async
    func updateVCList(credential: String) async
    func getOrgName(iss: String, issList: [DownloadIssListResponse]) -> String
    func getCards(userId: UUID, ascending: Bool) -> [UserVerifiableCredentailData]
    func getParseVPData(qrcode: String) async throws -> ParseVPDataResponse
    func getMatchVCForPresentaion(parseVPData: ParseVPDataResponse) async -> VerifiableData
    func updatePinCode(pinCode: String) -> Bool
    /// `dwsdk-301i` + `dwsdk-501i`  查詢卡片狀態+藍勾勾
    func getVerifyVCResult(applyVCData: ApplyVCDataResponse) async throws -> VerifiableCredentailResult
    /// `dwsdk-302i` 離線驗證結果
    func getVerifyVCResultOffLine(applyVCData: ApplyVCDataResponse, issList: [DownloadIssListResponse], vcList: Array<Dictionary<String, Array<Dictionary<String, String>>>>) async throws -> VerifiableCredentailResult
    /// `dwsdk-501i` 取得DecodeVC
    func getDecodeVCData(applyVCData: ApplyVCDataResponse) async -> DecodeVCDataResponse?
    /// `DB` 取得錢包
    func getWallets() -> [MyWallet]
    func refreshWalletData()
    /// `DB` 刪除錢包
    func deleteWallet(uuid: UUID)
    func saveVerifiableCredentialRecord(vcUUID: UUID, cardType: CardType, recordMessage: String, client: String?, purpose: String?, applyInfos: String?, createDate: Date)
    func saveVerifiableCredentialUpdateDate(vcUUID: UUID?, remind: Int64, updateDate: Date)
    func getIsAutoRefreshCard() -> Bool
    func setupAutoUpdate(isOn: Bool)
    /// 儲存信任藍勾勾欄位
    /// - Parameters:
    ///   - vcUUID: 卡片`UUID`
    ///   - trustBadge: 是否有藍勾勾
    func saveVerifiableCredentialTrustBadge(vcUUID: UUID, trustBadge: Bool)
    /// 獲取卡片清單 301i
    func getVCApplyList(bodyModel: GetCertificatesListRequest) async throws -> VCCardApplyListResponse
    /// 獲取憑證清單
    func getCertificateList(bodyModel: GetCertificatesListRequest) async throws -> VPCardApplyListResponse
    /// 獲取我的最愛清單
    func getFavoriteCertificates() -> AnyPublisher<[MyFavoriteList]?, Never>
    /// 存入我的最愛清單
    func saveFavoriteCertificates(vpItem: VPItems) -> AnyPublisher<Void, Error>
    /// 刪除我的最愛清單
    func deleteFavoriteCertificates(favoriteCertificateId: UUID) -> AnyPublisher<Void, Error>
    /// 新增搜尋憑證紀錄
    func saveSearchCertificatesLog(searchCertificateLogName: String) -> AnyPublisher<Void, Error>
    /// 刪除搜尋紀錄
    func deleteSearchCertificateLog(searchCertificateLogUUID: UUID) -> AnyPublisher<Void, Error>
    /// 獲得搜尋紀錄
    func getSearchCertificatesLog() -> AnyPublisher<[SearchCertificateLog]?, Error>
    /// 新增搜尋VC紀錄
    func saveSearchVCsLog(searchVCLogName: String) -> AnyPublisher<Void, Error>
    /// 刪除VC搜尋紀錄
    func deleteSearchVCLog(searchVCLogUUID: UUID) -> AnyPublisher<Void, Error>
    /// 獲得VC搜尋紀錄
    func getSearchVCsLog() -> AnyPublisher<[SearchVCLog]?, Error>
    /// 獲取deepLink以及交易序號
    func getDeepLinkAndTransactionId(bodyModel: DwVerIfierMgr401iRequest, url: String) async throws -> DwVerIfierMgr401iResponse
    /// 獲得QRCode
    func getTransactionIdQRCode(url: String, request: DwVerIfierMgr402iRequest) async throws -> DwVerIfierMgr402iResponse
    /// VP05相關資訊
    func saveVP05Info(data: VP05DeepLinkInformation)
    func updateVP05Source(data: VPSource?)
    /// 透過靜態QR的UID取得發卡端加卡連結or驗證端的驗證連結
    func getStaticVerifiableCredential(bodyModel: DwModa201iRequest, uid: String) async throws -> DwModa201iResponse
    func getVerifiablePresentationDeepLink(bodyModel: DwVerifierMock101iRequest, verifierServiceUrl: String) async throws -> DwVerifierMock101iResponse
    /// 取得授權紀錄
    /// - Returns: 將DB資料轉化成畫面可用的資料
    func fetchAuthorizationRecords(ascending: Bool,
                                   limit: Int?,
                                   offset: Int,
                                   start: Date?,
                                   end: Date?) -> [CardRecord]
    /// 取得異動紀錄
    /// - Returns: 將DB資料轉化成畫面可用的資料
    func fetchChangeRecords(ascending: Bool,
                            limit: Int?,
                            offset: Int,
                            start: Date?,
                            end: Date?) -> [CardRecord]
    /// 存過期清單
    func saveRemindList(_ list: Remind?)
    /// 更新失敗清單
    func saveErrorList(_ list: UserVerifiableCredentailData)
}
