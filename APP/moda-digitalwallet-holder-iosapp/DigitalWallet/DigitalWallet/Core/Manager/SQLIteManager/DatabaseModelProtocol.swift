//
//  DatabaseModelProtocol.swift
//  DigitalWallet
//

import Combine
import Foundation
import CoreData

protocol DatabaseModelProtocol {
    
    func configure() throws
    // MARK: VC
    /// 儲存卡片
    func saveVerifiableCredential(vcUUID: UUID, userId: UUID, verifiableCredential: String, state: CardStatus, iss: String, orgName: String, cardImage: Data?, remind: Int64, exp: Int64, displayName: String?)
    /// 更新日期
    func saveVerifiableCredentialUpdateDate(vcUUID: UUID, remind: Int64, updateDate: Date)
    /// 刪除卡片
    func deleteVerifiableCredential(cardId: UUID)
    /// 儲存信任藍勾勾欄位
    /// - Parameters:
    ///   - vcUUID: 卡片`UUID`
    ///   - trustBadge: 是否有藍勾勾
    func saveVerifiableCredentialTrustBadge(vcUUID: UUID, trustBadge: Bool)
    /// 更新狀態
    func updateVerifiableCredentialStatus(cardId: UUID, state: CardStatus)
    /// 更新remind狀態
    func updateVerifiableCredentialRemind(cardId: UUID, remind: Int64)
    /// 取得使用者卡片
    func fetchVerifiableCredentials(userId: UUID, ascending: Bool) -> [UserCard]
    
    // MARK: Wallet
    func createWallet(walletName: String, pinCode: String?, uuid: UUID, didFile: String, keyData: String?, createDate: Date)
    func deleteWallet(uuid: UUID)
    func setAutoUpdate(walletUUID: UUID, isAutoUpdate: Bool)
    func updateWalletName(userId: UUID, name: String) throws -> Bool
    func updateWalletAutoLogout(userId: UUID, autoLogout: Int16) throws -> Bool
    func updatePinCode(userId: UUID, pinCode: String) throws
    func fetchWallets() -> [MyWallet]?
    func fetchWallet(uuid: UUID) -> MyWallet?
    
    // MARK: 卡片紀錄
    func fetchCardRecords(vcId: UUID) -> [CardRecord]
    func saveOperationRecord(uid: UUID, walletId: UUID, vcId: UUID, text: String, status: Int64, datetime: Date)
    func saveCredentialRecord(uid: UUID, walletId: UUID, vcId: UUID, text: String, authorizationUnit: String, authorizationPurpose: String, authorizationField: String, datetime: Date)
    func savePresentationRecord(uid: UUID, walletId: UUID, text: String, vcIds: String, vcNames: String, authorizationUnit: String, authorizationPurpose: String, authorizationFields: String, datetime: Date)
    func saveCardRecord(vcUUID: UUID, cardType: CardType, recordMessage: String, client: String?, purpose: String?, applyInfos: String?, createDate: Date)
    func getIsAutoUpdate(walletUUID: UUID) -> Bool
    /// 新增我的最愛
    func saveFavoriteCertificate(walletId: String,
                                 favoriteCertificateId: UUID,
                                 vpItem: VPItems,
                                 creatDate: Date,
                                 updateDate: Date) -> AnyPublisher<Void, Error>
    
    // MARK: VP 我的最愛
    /// 更新我的最愛
    func updateFavorite(favoriteCertificateId: UUID,
                        vpItem: VPItems,
                        update: Date) throws 
    /// 刪除我的最愛
    func deleteFavoriteCertificate(favoriteCertificateId: UUID) -> AnyPublisher<Void, Error>
    /// 獲得我的最愛
    func getFavoriteCertificates(walletId: String) -> AnyPublisher<[MyFavoriteList]?, Never>
    
    // MARK: VP 搜尋紀錄
    /// 新增搜尋憑證紀錄
    func saveSearchCertificatesLog(walletId: String,
                                   searchCertificateLogId: UUID,
                                   searchCertificateLogName: String,
                                   creatDate: Date,
                                   updateDate: Date) -> AnyPublisher<Void, Error>
    /// 刪除搜尋紀錄
    func deleteSearchCertificateLog(searchCertificateLogUUID: UUID) -> AnyPublisher<Void, Error>
    /// 獲得搜尋紀錄
    func getSearchCertificatesLog(walletId: String) -> AnyPublisher<[SearchCertificateLog]?, Error>
    
    // MARK: VC 搜尋紀錄
    /// 新增搜尋憑證紀錄
    func saveSearchVCLog(walletId: String,
                         searchVCLogId: UUID,
                         searchVCLogName: String,
                         creatDate: Date,
                         updateDate: Date) -> AnyPublisher<Void, Error>
    /// 刪除搜尋紀錄
    func deleteSearchVCLog(searchVCLogUUID: UUID) -> AnyPublisher<Void, Error>
    /// 獲得搜尋紀錄
    func getSearchVCsLog(walletId: String) -> AnyPublisher<[SearchVCLog]?, Error>
    /// 取得授權紀錄
    /// - Returns: 將DB資料轉化成畫面可用的資料
    func fetchAuthorizationRecords(ascending: Bool,
                                   limit: Int?,
                                   offset: Int,
                                   start: Date?,
                                   end: Date?) -> [CardRecord]
    
    /// 取得異動紀錄
    /// - Returns: 將DB資料轉化成畫面可用的資料
    func fetchChangeRecords(walletId: UUID,
                            ascending: Bool,
                            limit: Int?,
                            offset: Int,
                            start: Date?,
                            end: Date?) -> [CardRecord]
}
