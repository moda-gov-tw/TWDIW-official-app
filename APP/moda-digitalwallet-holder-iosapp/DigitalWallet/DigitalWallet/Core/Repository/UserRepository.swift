//
//  UserRepository.swift
//  DigitalWallet
//

import Combine
import Foundation

class UserRepository: UserRepositoryProtocol {
    var databaseManager: DatabaseModelProtocol
    var verifiableManager: VerifiableManagerProtocol
    var wallet: MyWallet
    private(set) var remindData: [Remind]? = []
    private(set) var updateErrorList: [UserVerifiableCredentailData]?
    private(set) var vp05Info: VP05DeepLinkInformation?
    private(set) var vp05Soruce: VPSource?
    
    init(databaseManager: DatabaseModelProtocol,
         verifiableManager: VerifiableManagerProtocol,
         wallet: MyWallet) {
        self.databaseManager = databaseManager
        self.verifiableManager = verifiableManager
        self.wallet = wallet
    }
    
    /**更新登入中Wallet資料*/
    func refreshWalletData(){
        if let wallet = databaseManager.fetchWallet(uuid: wallet.uuid) {
            self.wallet = wallet
        }
    }
    
    func getVerifiableCredential(qrcode: String, otp: String) async throws -> ApplyVCDataResponse {
        guard let didDataString = wallet.didFile else {
            throw SDKError.LocalDataIsEmpty
        }
        return try await verifiableManager.createVerifiableCredential(qrcode: qrcode,
                                                                      didDataString: didDataString,
                                                                      otp: otp)
    }
    
    func saveVerifiableCredential(verifiableCredential vc: ApplyVCDataResponse, state: CardStatus, iss: String, orgName: String, cardImage: Data?, remind: Int64, exp: Int64, displayName: String?, trustBadge: Bool) {
        guard
            let data = try? JSONEncoder().encode(vc),
            let jsonString = String(data: data, encoding: .utf8) else {
            return
        }
        let vcUUID = UUID()
        databaseManager.saveVerifiableCredential(vcUUID: vcUUID, userId: wallet.uuid, verifiableCredential: jsonString, state: state, iss: iss, orgName: orgName, cardImage: cardImage, remind: remind, exp: exp, displayName: displayName)
        databaseManager.saveVerifiableCredentialTrustBadge(vcUUID: vcUUID, trustBadge: trustBadge)
        let recordMessage = String(format: NSLocalizedString("AddCardWith", comment: "加入「%@」"), vc.credentialDefinition.display.first?.name ?? "")
        saveVerifiableCredentialRecord(vcUUID: vcUUID, cardType: .add, recordMessage: recordMessage, client: nil, purpose: nil, applyInfos: nil, createDate: Date())
    }
    
    func saveVerifiableCredentialRecord(vcUUID: UUID, cardType: CardType, recordMessage: String, client: String?, purpose: String?, applyInfos: String?, createDate: Date) {
        switch cardType {
        case .add, .remove, .changePassword:
            databaseManager.saveOperationRecord(uid: UUID(), walletId: wallet.uuid, vcId: vcUUID, text: recordMessage, status: cardType.rawValue, datetime: createDate)
            break
        case .verify:
            break
        case .expired:
            // 如果沒有紀錄過失效才紀錄
            let recordTypes = databaseManager.fetchCardRecords(vcId: vcUUID).map { record in
                record.type
            }
            if !recordTypes.contains(.expired) {
                databaseManager.saveOperationRecord(uid: UUID(), walletId: wallet.uuid, vcId: vcUUID, text: recordMessage, status: cardType.rawValue, datetime: createDate)
            }
            break
        }
    }
    
    /**更新VC卡片狀態*/
    func updateVerifiableCredentialStatus(cardId: UUID, state: CardStatus){
        databaseManager.updateVerifiableCredentialStatus(cardId: cardId, state: state)
    }
    
    func updateVerifiableCredentialRemind(cardId: UUID, remind: Int) {
        databaseManager.updateVerifiableCredentialRemind(cardId: cardId, remind: Int64(remind))
    }
    
    func getIss(verifiableCredential vc: ApplyVCDataResponse) async throws -> String {
        try await verifiableManager.getVerifiableCredential(applyVCData: vc).iss ?? ""
    }
    
    /**dwsdk-601i:持有端(Holder)下載發行端(Issuer)機關清單狀態列表*/
    func downloadIssList() async throws {
        let issList =  try await verifiableManager.downloadIssList()
        UserDefaultManager.shared.setObject(value: issList, key: .IssList601i)
    }
    
    /**dwsdk-602i:持有端(Holder)下載VC狀態清冊列表*/
    func updateAllVCList() async {
        let vcs = getCards(userId: wallet.uuid, ascending: false).compactMap { element in
            return element.vc.credential
        }
        let vcList = UserDefaultManager.shared.getObject(key: .AllVCList602i, type: Array<Dictionary<String, Array<VCJWTAndSHA256Hash>>>.self) ?? []
        
        /*寫入成功資料，若本次失敗，則落地資料維持上一次的內容*/
        if let allVCList = try? await verifiableManager.downloadAllVCList(vcs: vcs, vcList: vcList),
           let result = allVCList.result {
            UserDefaultManager.shared.setObject(value: result, key: .AllVCList602i)
        }
    }
    
    /// 更新單張卡片清單
    /// - Parameter credential: ApplyVCDataResponse.credential
    func updateVCList(credential: String) async {
        var vcList = UserDefaultManager.shared.getObject(key: .AllVCList602i, type: Array<Dictionary<String, Array<VCJWTAndSHA256Hash>>>.self) ?? []
        
        if let allVCList = try? await verifiableManager.downloadAllVCList(vcs: [credential], vcList: vcList),
           let result = allVCList.result {
            vcList.append(contentsOf: result)
            UserDefaultManager.shared.setObject(value: vcList, key: .AllVCList602i)
        }
    }
    
    func getOrgName(iss: String, issList: [DownloadIssListResponse]) -> String {
        var orgName = NSLocalizedString("unknown", comment: "")
        
        issList.forEach({ issuer in
            if let data = issuer.did.decodeJWT(),
               iss == getJson(data)?.id {
                orgName = issuer.org["name"] ?? NSLocalizedString("unmatch", comment: "")
            }
        })
        
        return orgName
    }
    
    private func getJson(_ data: Data) -> IssuerDidItem? {
        guard let issuerDidItem = try? JSONDecoder().decode(IssuerDidItem.self, from: data) else {
            return nil
        }
        return issuerDidItem
    }
    
    /// `DB` 取得使用者卡片
    func getCards(userId: UUID, ascending: Bool) -> [UserVerifiableCredentailData] {
        let datas = databaseManager.fetchVerifiableCredentials(userId: userId, ascending: ascending)
        let userCards = datas.compactMap({ element -> UserVerifiableCredentailData? in
            guard
                let json = element.verifiableCredential?.data(using: .utf8),
                let verifiableCredential = try? JSONDecoder().decode(ApplyVCDataResponse.self, from: json)
            else {
                return nil
            }
            
            let cardName = verifiableCredential.credentialDefinition.display.first?.name ?? ""
            let orgName = element.orgName
            let iss = element.iss
            let createDate = element.createDate
            let cardImage = element.cardImage
            let displayName = element.displayName
            let updateDate = element.updateDate
            let remind = element.remind
            let exp = element.exp
            let state = CardStatus(rawValue: Int16(element.state ?? 0))
            let trustBadge = element.trustBadge ?? false
            
            return UserVerifiableCredentailData(vc: verifiableCredential, cardId: element.uuid, cardName: cardName, iss: iss, orgName: orgName, verificationStatus: state ?? .invalidation, createDate: createDate, cardImage: cardImage, remind: remind, exp: exp, displayName: displayName, updateDate: updateDate, trustBadge: trustBadge)
        })
        
        return userCards
    }
    
    func getParseVPData(qrcode: String) async throws -> ParseVPDataResponse {
        return try await verifiableManager.parseVerifiablePresentation(qrcode: qrcode)
    }
    
    func getMatchVCForPresentaion(parseVPData: ParseVPDataResponse) async -> VerifiableData {
        /// 取出使用者所有的VC卡片
        /// 依卡片建立時間，由新到舊排序
        /// 再以`IAL`等級排序
        /// 在篩選出無過期的卡
        let userCards: [UserVerifiableCredentailData] = getCards(userId: wallet.uuid, ascending: false)
            .sorted(by: {$0.createDate ?? Date() > $1.createDate ?? Date()})
            .sorted { first, second in
                    let firstIAL = Int(first.vc.credentialDefinition.display.first?.description?.ial?.uppercased().replacingOccurrences(of: "IAL ", with: "") ?? "0") ?? 0
                    let secondIAL = Int(second.vc.credentialDefinition.display.first?.description?.ial?.uppercased().replacingOccurrences(of: "IAL ", with: "") ?? "0") ?? 0
                    return firstIAL > secondIAL
                }
            .filter({$0.verificationStatus != .invalidation})
        /// 取得使用者的VC卡片
        var userVCDatas: [(userCard: UserVerifiableCredentailData, decodeVCData: DecodeVCDataResponse)] = []
        
        for userCard in userCards {
            guard let decodeData = try? await verifiableManager.getVerifiableCredential(applyVCData: userCard.vc) else {
                continue
            }
            userVCDatas.append((userCard, decodeData))
        }

        /// 組合VP所擁有的群組列表
        let resultDatas: [VerifyGroupData] = parseVPData.requestDatas?.map({ requestData in
            /// 篩選出群組內的卡
            var filterUserVCs: [(userCard: UserVerifiableCredentailData, decodeVCData: DecodeVCDataResponse, requestCard: VirtualCard)] = []
            userVCDatas.forEach { (userCard: UserVerifiableCredentailData, decodeVCData: DecodeVCDataResponse) in
                if let card = requestData.cards?.first(where: { virtualCard in
                    decodeVCData.vc?.type?.contains(virtualCard.card ?? "") ?? false
                }) {
                    filterUserVCs.append((userCard, decodeVCData, card))
                }
            }
            
            /// 對應的規則
            let isPickRule = VerifyCardRule(rawValue: requestData.rule?.lowercased() ?? VerifyCardRule.all.rawValue) == .pick
            /// 預設選取判斷
            var cardIDSet: Set<String> = []
            /// `count` `max`判斷
            let count = requestData.max ?? (requestData.count ?? nil)
            
            /// 整理出卡片列表，包含VP所需卡片欄位以及是否被選取到
            let cardList: [CardInfoData] = filterUserVCs.map { (userCard: UserVerifiableCredentailData, decodeVCData: DecodeVCDataResponse, requestCard: VirtualCard) in
                let subject = userCard.vc.credentialDefinition.credentialDefinition.credentialSubject
                let decodeSubject = decodeVCData.vc?.credentialSubject?.field?.data
                /// 取得VP需求的卡片欄位
                let fields = requestCard.fields.map({ fieldKey in
                    let fieldName = subject[fieldKey]?.display.first?.name ?? fieldKey
                    let fieldValue = decodeSubject?.first(where: {$0.first?.key == fieldKey})?.first?.value
                    return (fieldKey, fieldName, fieldValue)
                }).map { (id, name, value) in
                    CardRequirement(id: id, name: name, value: value)
                }
                let cardInfo = CardInfoData(cardUUID: userCard.cardId,
                                            cardId: requestCard.cardId,
                                            cardName: requestCard.card,
                                            card: userCard,
                                            infos: fields)
                /// 檢查是否符合規則
                if cardIDSet.contains(cardInfo.cardId ?? "") {
                    cardInfo.isSelect = false
                    /// 檢查是否有重複的`cardId`
                } else if isPickRule && cardIDSet.count >= (count ?? 0){
                    cardInfo.isSelect = false
                } else {
                    cardInfo.isSelect = true
                    cardIDSet.insert(cardInfo.cardId ?? "")
                }
                return cardInfo
            }
            
            return VerifyGroupData(groupName: requestData.name,
                                   cards: cardList,
                                   requestCards: requestData.cards ?? [],
                                   limitCount: cardList.filter({$0.isSelect}).count,
                                   rule: VerifyCardRule(rawValue: requestData.rule?.lowercased() ?? VerifyCardRule.all.rawValue),
                                   count: count)
        }) ?? []
        
        return VerifiableData(inTrustedList: parseVPData.inTrustedList, groupList: resultDatas)
    }
    
    /// `DB` 更新 `PinCode`
    func updatePinCode(pinCode: String) -> Bool {
        do{
            let shaPinCode = CryptoManager.sha256(input: pinCode)
            try databaseManager.updatePinCode(userId: wallet.uuid, pinCode: shaPinCode)
            return true
        } catch {
            return false
        }
    }
    
    /// `dwsdk-301i` + `dwsdk-501i`  查詢卡片狀態+藍勾勾
    func getVerifyVCResult(applyVCData: ApplyVCDataResponse) async throws -> VerifiableCredentailResult {
        guard let didDataString = wallet.didFile,
              let verifyVCData = try await verifiableManager.verifyVC(applyVCData: applyVCData, didDataString: didDataString)
        else {
            return VerifiableCredentailResult(status: .verified, trustBadge: false)
        }
        // 實作邏輯
        if !(verifyVCData.holder ?? true) {
            // Log
            let id = UserDefaultManager.shared.getObject(key: .GenerateDID, type: DIDDataResponse.self)?.id ?? "userDefault 找不到"
            let sub = try? await verifiableManager.getVerifiableCredential(applyVCData: applyVCData).sub
            
            LogUtil.shared.write(text: "皮夾的DID:\(id),卡片的DID:\(sub ?? "無法取得501i的結果")")
        }
        
        let checks: [Bool?] = [
            verifyVCData.vc,
            verifyVCData.trust,
            verifyVCData.issuer,
            verifyVCData.holder,
            verifyVCData.exp
        ]

        if checks.contains(where: { $0 == false }) {
            return VerifiableCredentailResult(status: .invalidation, trustBadge: verifyVCData.trustBadge ?? false)
        } else {
            return VerifiableCredentailResult(status: .unverified, trustBadge: verifyVCData.trustBadge ?? false)
        }
    }
    
    /// `dwsdk-302i` 離線驗證結果
    func getVerifyVCResultOffLine(applyVCData: ApplyVCDataResponse, issList: [DownloadIssListResponse], vcList: Array<Dictionary<String, Array<Dictionary<String, String>>>>) async throws -> VerifiableCredentailResult {
        guard
            let didDataString = wallet.didFile,
            let verifyVCData = try await verifiableManager.verifyVCOffline(applyVCData: applyVCData, issList: issList, vcList: vcList, didDataString: didDataString) else {
            return VerifiableCredentailResult(status: .verified, trustBadge: false)
        }

        let checks: [Bool?] = [
            verifyVCData.vc,
            verifyVCData.trust,
            verifyVCData.issuer,
            verifyVCData.holder,
            verifyVCData.exp
        ]

        if checks.contains(where: { $0 == false }) {
            return VerifiableCredentailResult(status: .invalidation, trustBadge: verifyVCData.trustBadge ?? false)
        } else {
            return VerifiableCredentailResult(status: .verified, trustBadge: verifyVCData.trustBadge ?? false)
        }
    }
    
    /// `dwsdk-501i` 取得顯示名稱
    func getDecodeVCData(applyVCData: ApplyVCDataResponse) async -> DecodeVCDataResponse? {
        let decodeVCData = try? await verifiableManager.getVerifiableCredential(applyVCData: applyVCData)
        return decodeVCData
    }
    
    /// `DB` 取得錢包
    func getWallets() -> [MyWallet] {
        guard let wallets = databaseManager.fetchWallets() else {
            return []
        }
        return wallets.sorted(by: {
            switch ($0.createDate, $1.createDate) {
               case let (date1?, date2?):
                   return date1 < date2 // 兩者皆有日期，比較日期
               case (nil, _?):
                   return true // nil 視為比較早
               case (_?, nil):
                   return false
               case (nil, nil):
                   return false
               }
        })
    }
    
    /// `DB` 刪除錢包
    func deleteWallet(uuid: UUID) {
        databaseManager.deleteWallet(uuid: uuid)
    }
    
    
    func saveVerifiableCredentialUpdateDate(vcUUID: UUID?, remind: Int64, updateDate: Date) {
        guard let vcUUID else { return }
        databaseManager.saveVerifiableCredentialUpdateDate(vcUUID: vcUUID, remind: remind, updateDate: updateDate)
    }
    
    func getIsAutoRefreshCard() -> Bool {
        databaseManager.getIsAutoUpdate(walletUUID: wallet.uuid)
    }
    
    func setupAutoUpdate(isOn: Bool) {
        databaseManager.setAutoUpdate(walletUUID: wallet.uuid, isAutoUpdate: isOn)
    }
    
    /// 儲存信任藍勾勾欄位
    /// - Parameters:
    ///   - vcUUID: 卡片`UUID`
    ///   - trustBadge: 是否有藍勾勾
    func saveVerifiableCredentialTrustBadge(vcUUID: UUID, trustBadge: Bool) {
        databaseManager.saveVerifiableCredentialTrustBadge(vcUUID: vcUUID, trustBadge: trustBadge)
    }
}

// MARK: 靜態QR
extension UserRepository {
    /// 透過靜態QR取得vc or vp的UID，換取對應發卡與驗證要使用的資料
    /// - Parameters:
    ///   - bodyModel: 儲存mode，用於判斷為vc還是vp的流程
    ///   - uid: vcUID or vpUID
    /// - Returns: 接續流程所使用到的資料，根據vc or vp有不同的資料內容回傳
    func getStaticVerifiableCredential(bodyModel: DwModa201iRequest, uid: String) async throws -> DwModa201iResponse {
        let result: DwModa201iResponse = try await verifiableManager.requestFromSDK(url: ModaUrlPath.dwModa201i(uid: uid, mode: bodyModel.mode).description,
                                                                                    type: .get,
                                                                                    bodyModel: bodyModel)
        return result
    }
    
    func getVerifiablePresentationDeepLink(bodyModel: DwVerifierMock101iRequest, verifierServiceUrl: String) async throws -> DwVerifierMock101iResponse {
        let url = ModaUrlPath.dwVerifierMock101i(url: verifierServiceUrl, uid: bodyModel.vpUid).description
        let result: DwVerifierMock101iResponse = try await verifiableManager.requestFromSDK(url: url,
                                                                                            type: .get,
                                                                                            bodyModel: bodyModel)
        return result
    }
}

// MARK: VC列表 301i
extension UserRepository {
    /// DW-MODA-301i  App VC 卡片申請清單 API (VC05)
    func getVCApplyList(bodyModel: GetCertificatesListRequest) async throws -> VCCardApplyListResponse {
        let result: VCCardApplyListResponse = try await verifiableManager.requestFromSDK(url: ModaUrlPath.dwModa301i(name: "", page: "\(bodyModel.page)", size: "\( bodyModel.size)").description,
                                                                                         type: .get,
                                                                                         bodyModel: bodyModel)
        return result
    }
}

// MARK: 我的最愛
extension UserRepository {
    /**DW-MODA-401i  App VP offline 驗證情境清單*/
    func getCertificateList(bodyModel: GetCertificatesListRequest) async throws -> VPCardApplyListResponse {
        let result: VPCardApplyListResponse = try await verifiableManager.requestFromSDK(url: ModaUrlPath.dwModa401i(name: "", page: "\(bodyModel.page)", size: "\(bodyModel.size)").description,
                                                                                         type: .get,
                                                                                         bodyModel: bodyModel)
        return result
    }
    
    func getFavoriteCertificates() -> AnyPublisher<[MyFavoriteList]?, Never> {
        databaseManager.getFavoriteCertificates(walletId: wallet.uuid.uuidString)
    }
    
    func saveFavoriteCertificates(vpItem: VPItems) -> AnyPublisher<Void, Error> {
        databaseManager.saveFavoriteCertificate(walletId: wallet.uuid.uuidString,
                                                favoriteCertificateId: UUID(),
                                                vpItem: vpItem,
                                                creatDate: Date(),
                                                updateDate: Date())
    }
    
    func deleteFavoriteCertificates(favoriteCertificateId: UUID) -> AnyPublisher<Void, Error> {
        databaseManager.deleteFavoriteCertificate(favoriteCertificateId: favoriteCertificateId)
    }
}

// MARK: 獲得該憑證deepLink以及交易序號
extension UserRepository {
    /**DW-VERIFIER_MGR-401i  取得於 offline 模式的自主揭露 qrcode*/
    func getDeepLinkAndTransactionId(bodyModel: DwVerIfierMgr401iRequest, url: String) async throws -> DwVerIfierMgr401iResponse {
        let result: DwVerIfierMgr401iResponse = try await verifiableManager.requestFromSDK(url: ModaUrlPath.dwModaMgr401i(url: url, path: bodyModel.vpUid).description,
                                                                                           type: .get,
                                                                                           bodyModel: bodyModel)
        return result
    }
    
    /**DW-VERIFIER_MGR-402i  取得加密資料 QR Code */
    func getTransactionIdQRCode(url: String, request: DwVerIfierMgr402iRequest) async throws -> DwVerIfierMgr402iResponse {
        let result: DwVerIfierMgr402iResponse = try await verifiableManager.requestPayloadFromSDK(type: .post,
                                                                                payload: request,
                                                                                url: ModaUrlPath.dwModaMgr402i(url: url).description,
                                                                                didFile: wallet.didFile ?? "")
        return result
    }
}

// MARK: 搜尋憑證紀錄
extension UserRepository {
    func saveSearchCertificatesLog(searchCertificateLogName: String) -> AnyPublisher<Void, Error> {
        databaseManager.saveSearchCertificatesLog(walletId: wallet.uuid.uuidString,
                                                  searchCertificateLogId:  UUID(),
                                                  searchCertificateLogName: searchCertificateLogName,
                                                  creatDate: Date(),
                                                  updateDate: Date())
    }
    
    func deleteSearchCertificateLog(searchCertificateLogUUID: UUID) -> AnyPublisher<Void, Error> {
        databaseManager.deleteSearchCertificateLog(searchCertificateLogUUID: searchCertificateLogUUID)
    }
    
    func getSearchCertificatesLog() -> AnyPublisher<[SearchCertificateLog]?, Error> {
        databaseManager.getSearchCertificatesLog(walletId: wallet.uuid.uuidString)
    }
}

// MARK: 搜尋卡片紀錄
extension UserRepository {
    func saveSearchVCsLog(searchVCLogName: String) -> AnyPublisher<Void, Error> {
        databaseManager.saveSearchVCLog(walletId: wallet.uuid.uuidString,
                                        searchVCLogId: UUID(),
                                        searchVCLogName: searchVCLogName,
                                        creatDate: Date(),
                                        updateDate: Date())
    }
    
    func deleteSearchVCLog(searchVCLogUUID: UUID) -> AnyPublisher<Void, Error> {
        databaseManager.deleteSearchVCLog(searchVCLogUUID: searchVCLogUUID)
    }
    
    func getSearchVCsLog() -> AnyPublisher<[SearchVCLog]?, Error> {
        databaseManager.getSearchVCsLog(walletId: wallet.uuid.uuidString)
    }
}

extension UserRepository {
    func saveVP05Info(data: VP05DeepLinkInformation) {
        vp05Info = data
    }
    
    func updateVP05Source(data: VPSource?) {
        vp05Soruce = data
    }
}

extension UserRepository {
    /// 取得授權紀錄
    /// - Returns: 將DB資料轉化成畫面可用的資料
    func fetchAuthorizationRecords(ascending: Bool,
                                   limit: Int?,
                                   offset: Int,
                                   start: Date?,
                                   end: Date?) -> [CardRecord] {
        databaseManager.fetchAuthorizationRecords(ascending: ascending,
                                                  limit: limit,
                                                  offset: offset,
                                                  start: start,
                                                  end: end)
    }
    
    /// 取得異動紀錄
    /// - Returns: 將DB資料轉化成畫面可用的資料
    func fetchChangeRecords(ascending: Bool,
                            limit: Int?,
                            offset: Int,
                            start: Date?,
                            end: Date?) -> [CardRecord] {
        databaseManager.fetchChangeRecords(walletId: wallet.uuid,
                                           ascending: ascending,
                                           limit: limit,
                                           offset: offset,
                                           start: start,
                                           end: end)
    }
}

extension UserRepository {
    func saveRemindList(_ list: Remind?) {
        guard let list = list else {
            self.remindData = []
            return
        }
        self.remindData?.append(list)
    }
    
    func saveErrorList(_ list: UserVerifiableCredentailData) {
        self.updateErrorList?.append(list)
    }
    
}
