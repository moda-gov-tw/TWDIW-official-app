//
//  CardOverViewViewModel.swift
//  DigitalWallet
//

import Foundation
import Combine
import Network

class CardOverViewViewModel {
    
    enum SortType {
        case newToOld
        case oldToNew
    }
    
    /// 驗證結果
    enum VerifyResult {
        /// 網路錯誤
        case verifyingInternetError
        /// 驗證完畢
        case verifyFinish([Remind])
    }
    
    let repository: UserRepositoryProtocol
    
    let callTabbarProtocol: CallTabbarProtocol
    
    @Published var userCards: [UserVerifiableCredentailData] = []
    let errorSubject = PassthroughSubject<String, Never>()
    let startVerifySubject = PassthroughSubject<Void, Never>()
    let verifyingSubject = PassthroughSubject<Int, Never>()
    let verifyingInternetErrorSubject = PassthroughSubject<Void, Never>()
    let verifyFinishSubject = PassthroughSubject<[Remind], Never>()
    let isVerifySubject = PassthroughSubject<Void, Never>()
    let showRemindAlertSubject = PassthroughSubject<Void, Never>()
    
    let checkVerifySubject = PassthroughSubject<Bool, Never>()
    
    /// 檢查清單
    var vcCount: Int = 0
    var remindList: [Remind] = []
    var updateFailedList: [Remind] = []
    var verifyCardTask: Task<Void, Never>?
    var isVerifying: Bool = false
    @Published var sortType: SortType = .newToOld
    
    private var lastUpdateVCTime: Date?
    private var issListPublisher: AnyPublisher<Void, Never>?
    private var isDownloadedIssList = false
    
    private var cancelSet = Set<AnyCancellable>()
    
    init(repository: UserRepositoryProtocol,
         callTabbarProtocol: CallTabbarProtocol) {
        self.repository = repository
        self.callTabbarProtocol = callTabbarProtocol
    }
    
    func initBinding() {
        checkVerifySubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] bVerifyViewIsHidden in
                guard let self else { return }
                if !bVerifyViewIsHidden && !isVerifying {
                    verifyFinishSubject.send(updateFailedList)
                    if !remindList.isEmpty {
                        showRemindAlertSubject.send()
                    }
                }
            }.store(in: &cancelSet)
    }
    
    func getCards() {
        switch sortType {
        case .newToOld:
            userCards = repository.getCards(userId: repository.wallet.uuid, ascending: false)
            break
        case .oldToNew:
            userCards = repository.getCards(userId: repository.wallet.uuid, ascending: true)
            break
        }
    }
    
    /// 線上卡片驗證
    func verifyCards() {
        verifyCardTask = Task {
            if isVerifying || userCards.isEmpty {
                isVerifySubject.send()
                return
            }
            isVerifying = true
            /// 檢核張數
            var verifiedVCCount = 0
            
            // 清除提醒清單與更新失敗清單
            remindList.removeAll()
            updateFailedList.removeAll()
            
            // 驗證前先判斷有沒有網路，沒有網路就跳出憑證更新失敗(網路錯誤)
            if await !checkNetworkStatus() {
                verifyingInternetErrorSubject.send()
                isVerifying = false
                return
            }
            // 篩選出有效的憑證，只驗證有效的憑證
            let validCards = userCards.filter({$0.verificationStatus != .invalidation})
            
            // 一張一張更新卡片
            for card in validCards {
                // 如果Task已經取消了，就不驗證卡片了
                if Task.isCancelled {
                    isVerifying = false
                    return
                }
                
                guard let vcUUID = card.cardId else {
                    // 無法取得卡片的UUID，跳過這張卡片
                    continue
                }
                // 更新卡片的更新時間
                let updateDate = Date()
                card.updateDate = updateDate
                repository.saveVerifiableCredentialUpdateDate(vcUUID: vcUUID,
                                                              remind: card.remind ?? VerifyCardRemind.effective.rawValue,
                                                              updateDate: updateDate)
                // 進行驗證
                do {
                    // 呼叫301i進行卡片驗證
                    let vcVerificationResult = try await repository.getVerifyVCResult(applyVCData: card.vc)
                    // 判斷卡片是否有效
                    if vcVerificationResult.status == .invalidation {
                        // 卡片已失效，寫入卡片記錄
                        let recordMessage = String(format: NSLocalizedString("InvalidationWith", comment: "已失效「%@」"), card.cardName ?? "")
                        repository.saveVerifiableCredentialRecord(vcUUID: vcUUID, cardType: .expired, recordMessage: recordMessage, client: nil, purpose: nil, applyInfos: nil, createDate: Date())
                    }
                    // 更新狀態後重新拉取 DB 資料
                    // 更新藍勾勾
                    repository.saveVerifiableCredentialTrustBadge(vcUUID: vcUUID, trustBadge: vcVerificationResult.trustBadge)
                    
                    // 更新卡片狀態
                    repository.updateVerifiableCredentialStatus(cardId: vcUUID, state: vcVerificationResult.status)
                    // 更新已撈出來的卡片狀態
                    card.verificationStatus = vcVerificationResult.status
                    card.trustBadge = vcVerificationResult.trustBadge
                    // 刷新更新卡片進度
                    verifiedVCCount += 1
                    verifyingSubject.send(verifiedVCCount)
                } catch {
                    // 加入更新失敗的卡片
                    verifiedVCCount += 1
                    verifyingSubject.send(verifiedVCCount)
                    updateFailedList.append(.updateError(card))
                    continue
                }
                // 把尚未提醒過的卡片資料加入待提醒清單，後續會根據exp與remind欄位判斷是否要跳出7日內到期通知，或是判斷是不是已失效
                if card.checkRemind().rawValue != card.remind {
                    remindList.append(.remind(card))
                }
            }
            
            // 全部卡片驗證完後，判斷是否有更新失敗的卡片，如果有，再檢查是否有網路，若沒有網路顯示網路失效，若有網路則要顯示更新失敗
            if !updateFailedList.isEmpty {
                if await !checkNetworkStatus() {
                    // 沒有網路
                    isVerifying = false
                    verifyingInternetErrorSubject.send()
                    return
                } 
            }
            
            if !updateFailedList.isEmpty {
                isVerifying = false
                verifyFinishSubject.send(updateFailedList)
                return 
            }
            
            // 完成更新
            self.lastUpdateVCTime = Date()
            isVerifying = false
            verifyFinishSubject.send(updateFailedList)
            // 判斷是否有需要提醒的卡片內容
            if !remindList.isEmpty {
                showRemindAlertSubject.send()
            }
        }
    }
    
    /// 刷新卡片
    func refreshCards() {
        Task {
            // 先判斷是否下載過IssList
            if !isDownloadedIssList {
                // 下載發行端機關清單
                try? await downloadIssList()
            }
            // 進行離線卡片驗證
            await verifyCardsOffline()
        }
    }
    
    /// 檢查網路狀態
    /// - Returns: 是否有網路
    func checkNetworkStatus() async -> Bool {
        await withCheckedContinuation { continuation in
            let monitor = NWPathMonitor()
            let queue = DispatchQueue.global(qos: .background)

            monitor.pathUpdateHandler = { path in
                if path.status == .satisfied {
                    continuation.resume(returning: true)   // 有網路
                } else {
                    continuation.resume(returning: false)  // 無網路
                }
                monitor.cancel() // 只檢查一次就取消
            }

            monitor.start(queue: queue)
        }
    }
    
    /// 離線卡片驗證
    private func verifyCardsOffline() async {
        // 1) 先判斷上次更新時間是否在一個小時之內，如果上次更新時間在一個小時內，就不更新
        guard let lastUpdateVCTime else {
            verifyCards()
            return
        }
        if lastUpdateVCTime.timeIntervalSinceNow > -3600 || isVerifying || userCards.isEmpty {
            return
        }
        isVerifying = true
        // 2) 更新602i卡片清單
        await downloadVCList()
        // 3) 定義檢核張數
        var verifiedVCCount = 0
        // 4) 清空提醒list
        remindList.removeAll()
        updateFailedList.removeAll()
        // 5) 離線更新卡片
        let cards = userCards.filter({$0.verificationStatus != .invalidation})
        for card in cards {
            guard let vcUUID = card.cardId else {
                // 無法取得卡片的UUID，跳過這張卡片
                continue
            }
            // 更新卡片的更新時間
            let updateDate = Date()
            card.updateDate = updateDate
            repository.saveVerifiableCredentialUpdateDate(vcUUID: vcUUID,
                                                          remind: card.remind ?? VerifyCardRemind.effective.rawValue,
                                                          updateDate: updateDate)
            // 進行卡片離線驗證
            do {
                // 呼叫302i進行離線卡片驗證
                let vcVerificationResult = try await verifyCardOffLine(applyVCData: card.vc)
                // 判斷卡片是否有效
                if vcVerificationResult.status == .invalidation {
                    // 卡片已失效，寫入卡片記錄
                    let recordMessage = String(format: NSLocalizedString("InvalidationWith", comment: "已失效「%@」"), card.cardName ?? "")
                    repository.saveVerifiableCredentialRecord(vcUUID: vcUUID, cardType: .expired, recordMessage: recordMessage, client: nil, purpose: nil, applyInfos: nil, createDate: Date())
                }
                // 更新狀態後重新拉取 DB 資料
                // 更新藍勾勾
                repository.saveVerifiableCredentialTrustBadge(vcUUID: vcUUID, trustBadge: vcVerificationResult.trustBadge)
                // 更新卡片狀態
                repository.updateVerifiableCredentialStatus(cardId: vcUUID, state: vcVerificationResult.status)
            } catch {
                // 驗卡失敗
                verifiedVCCount += 1
                verifyingSubject.send(verifiedVCCount)
                updateFailedList.append(.updateError(card))
            }
            // 把尚未提醒過的卡片資料加入待提醒清單，後續會根據exp與remind欄位判斷是否要跳出7日內到期通知，或是判斷是不是已失效
            if card.checkRemind().rawValue != card.remind {
                remindList.append(.remind(card))
            }
            // 刷新更新卡片進度
            verifiedVCCount += 1
            verifyingSubject.send(verifiedVCCount)
        }
        // 6) 全部卡片驗證完後，判斷是否有更新失敗的卡片，如果有，再檢查是否有網路，若沒有網路顯示網路失效，若有網路則要顯示更新失敗
        if !updateFailedList.isEmpty {
            if await !checkNetworkStatus() {
                // 沒有網路
                isVerifying = false
                verifyingInternetErrorSubject.send()
                return
            }
        }
        // 7) 完成更新
        self.lastUpdateVCTime = Date()
        isVerifying = false
        verifyFinishSubject.send(updateFailedList)
        // 判斷是否有需要提醒的卡片內容
        if !remindList.isEmpty {
            showRemindAlertSubject.send()
        }
    }
    
    /// 下載機關清單
    private func downloadIssList() async throws {
        try await self.repository.downloadIssList()
        isDownloadedIssList = true
    }
    
    /// 下載發行卡片清單
    private func downloadVCList() async {
        await repository.updateAllVCList()
    }
    
    /**dwsdk-302i:持有端(Holder)進行離線授權VC卡片資訊*/
    private func verifyCardOffLine(applyVCData: ApplyVCDataResponse) async throws -> VerifiableCredentailResult {
        let issList: Array<DownloadIssListResponse>? = UserDefaultManager.shared.getObject(key: .IssList601i, type: Array<DownloadIssListResponse>.self)
        let offLineIssList: Array<Dictionary<String, Array<Dictionary<String, String>>>>? = UserDefaultManager.shared.getObject(key: .AllVCList602i, type: Array<Dictionary<String, Array<Dictionary<String, String>>>>.self)
        
        guard let issList = issList,
              let offLineIssList = offLineIssList else {
            return VerifiableCredentailResult(status: .unverified, trustBadge: false)
        }
        
        let result = try await repository.getVerifyVCResultOffLine(applyVCData: applyVCData, issList: issList, vcList: offLineIssList)
        return result
    }
    
    
    /// 卡片排序
    func sortCards() {
        userCards.sort { firstCard, secondCard in
            guard let firstDate = firstCard.createDate,
                  let secondDate = secondCard.createDate else {
                return false
            }
            
            if sortType == .newToOld {
                return firstDate > secondDate
            } else {
                return firstDate < secondDate
            }
        }
    }
}
