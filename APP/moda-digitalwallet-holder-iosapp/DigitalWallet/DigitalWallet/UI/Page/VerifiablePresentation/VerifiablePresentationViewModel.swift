//
//  VerifiablePresentationViewModel.swift
//  DigitalWallet
//

import LocalAuthentication
import Combine

class VerifiablePresentationViewModel {
    /// 初始化提示
    enum InitHintCheck: CaseIterable {
        /// 沒在信任清單中
        case noInTrustedList
        /// 缺少卡片
        case missingCards
        /// 全部都顯示
        case all
    }
    
    let repository: VerifiablePresentationRepositoryProtocol
    let userRepository: UserRepositoryProtocol
    let biometricVerifyManager: BiometricVerifyProtocol
    let callTabbarProtocol: CallTabbarProtocol
    /// 客製化欄位
    var customFields: [DwModa201iCustomField] = []
    
    let updateVerifyButtonSubject = PassthroughSubject<Bool, Never>()
    let isVerifyingSubject = PassthroughSubject<Bool, Never>()
    let finishVerifySubject = PassthroughSubject<(Bool, VerifiablePresentationResult), Never>()
    let authenticationSubject = PassthroughSubject<Bool, Never>()
    /// 資料驗證不完整
    let incompleteInformationSubject = PassthroughSubject<Void, Never>()
    /// 正則表達式檢查後刷新
    let checkRegularExpressionSubject = PassthroughSubject<Void, Never>()
    /// 檢查展開內容結果
    let checkExpandSubject = PassthroughSubject<Bool, Never>()
    
    var verifiablePresentationItems: [VerifiablePresentationCellType] = []
    
    /// 前往結果頁面
    var resultPageType: VerifiablePresentationResult = .showResult
    
    var eyeOpen: Bool = true
    
    var isEnableVerify: Bool = false
     
    lazy var title: String = {
        repository.scenario ?? NSLocalizedString("VerificationApplication", comment: "")
    }()
    
    private var cancelSet = Set<AnyCancellable>()
    
    init(repository: VerifiablePresentationRepositoryProtocol,
         userRepository: UserRepositoryProtocol,
         biometricVerifyManager: BiometricVerifyProtocol,
         callTabbarProtocol: CallTabbarProtocol,
         resultPageType: VerifiablePresentationResult,
         customFields: [DwModa201iCustomField]) {
        self.repository = repository
        self.userRepository = userRepository
        self.biometricVerifyManager = biometricVerifyManager
        self.callTabbarProtocol = callTabbarProtocol
        self.resultPageType = resultPageType
        self.customFields = customFields
        setupList()
    }
    
    /// 設定畫面資料
    public func setupList() {
        verifiablePresentationItems.removeAll()
        verifiablePresentationItems.append(.Info(authorizingAgency: repository.client,
                                                 purpose: repository.purpose))
        verifiablePresentationItems.append(.ListInfo(eyeOpen: eyeOpen))
        verifiablePresentationItems.append(contentsOf: repository.verifyResp.groupList.map({.Requirement(verifyData: $0, eyeOpen: eyeOpen)}))
        verifiablePresentationItems.append(contentsOf: customFields.map({.CustomFields(field:$0, eyeOpen: eyeOpen)}))
    }
    
    
    /// 檢查初始化提示
    func checkInitHint() -> [InitHintCheck] {
        var result: [InitHintCheck] = []
        /// 1. 是否在信任清單內
        if !repository.verifyResp.inTrustedList {
            result.append(.noInTrustedList)
        }
        
        /// 2. 是否有缺少卡片
        let missingCards = repository.verifyResp.groupList.contains(where: {$0.status == .noAuthorized && $0.rule != .pick})
        if missingCards {
            result.append(.missingCards)
        }
        return result
    }
    
    /// 更換眼睛開啟關閉狀態
    /// - Parameter eyeOpen: 是否顯示開啟資訊
    func changeEyeOpen(_ eyeOpen: Bool) {
        self.eyeOpen = eyeOpen
        setupList()
    }
    
    /**進行授權*/
    func verify() {
        Task {
            isVerifyingSubject.send(true)
            var customData: VPCustomData?
            if !customFields.isEmpty {
                customData = VPCustomData(customData: customFields)
            }
            guard let isVerifySuccess = try? await repository.verifyVP(customData: customData) else {
                finishVerifySubject.send((false, resultPageType))
                return
            }
            
            if isVerifySuccess {
                repository.saveVerifySuccessRecord(customData: customData)
                finishVerifySubject.send((true, resultPageType))
            } else {
                finishVerifySubject.send((false, resultPageType))
            }
        }
    }
    
    /// 授權前檢查
    func authorizationCheck() {
        /// 輸入不符合正則表達式
        if customFields.contains(where: { customField in
            let isLegal = regularExpressionJudgment(customField.value, pattern: customField.regex)
            customField.isLegal = isLegal
            return !isLegal
        }) {
            checkRegularExpressionSubject.send()
            return
        }
        checkRegularExpressionSubject.send()
        
        /// 確認提供資料的完整性
        if confirmDataIntegrity() {
            /// 完整情境：進行授權前的驗證
            authentication()
        } else {
            /// 有缺少情境：跳窗提醒
            incompleteInformationSubject.send()
        }
    }
    
    // 正則表達式判斷
    private func regularExpressionJudgment(_ value: String?, pattern: String?) -> Bool {
        guard let pattern = pattern else {
            return true
        }
        // 使用 NSPredicate 檢查字串是否符合正則表達式
        let predicate = NSPredicate(format: "SELF MATCHES %@", pattern)
        return predicate.evaluate(with: value)
    }
    
    /**進行授權前的驗證*/
    func authentication(){
        if biometricVerifyManager.canEvaluatePolicy(policy: .deviceOwnerAuthentication) {
            biometricVerifyManager.verify(policy: .deviceOwnerAuthentication, message: BiometricLocalizedReason.verify){ isSuccess, error in
                if isSuccess {
                    self.authenticationSubject.send(true)
                }
            }
        } else {
            if !userRepository.wallet.pinCode.isEmpty {
                self.authenticationSubject.send(false)
            }
        }
    }
    
    /// 確認提供資料的完整性
    private func confirmDataIntegrity() -> Bool {
        /// 1. 檢查`VerifyCardData`的`status`是否都為`.authorized`
        /// 2. 檢查卡片項目是否都有授權
        let checkGroupList = repository.verifyResp.groupList
            .filter({$0.status == .authorized})
        if repository.verifyResp.groupList.count != checkGroupList.count ||
            checkGroupList.contains(where: {!$0.isAuthorizeAllCards}){
            return false
        }
        return true
    }
    
    /// 檢查進行授權按鈕是否可以選擇
    func checkVerifyButtonEnable() {
        /// 1. 將檢查分為兩組、`All` 與 `Pick`
        /// 2. 檢查`All` 為必須的檢查
        let allGroupList = repository.verifyResp.groupList.filter({$0.rule == .all})
        let hasAll = !allGroupList.isEmpty
        let allIsAuthorize = allGroupList.filter({$0.isAuthorizeAllCards && $0.isAllCardSelect}).count == allGroupList.count
        
        /// 3. 檢查`Pick`為整個VP檢查只要有就可以通過
        let pickGroupList = repository.verifyResp.groupList
            .filter({$0.rule == .pick})
            .filter({$0.status != .unselect})
        
        let grorpIsAuthorize = hasAll ? allIsAuthorize : !pickGroupList.isEmpty
        
        /// 4. 檢查客製化欄位是否有值
        let customFieldIsEmpty = customFields.contains(where: {$0.value?.isEmpty ?? true})
        
        let filterGroupList = repository.verifyResp.groupList
        /// 1. 檢查Group 授權狀態
            .filter({$0.status != .unselect})
        /// 2. 檢查Group 內是否有卡片
            .filter({!$0.cards.isEmpty})
            .compactMap { group -> VerifyGroupData? in
                /// 3. 檢查 Group 內的卡片是否被選擇
                if group.cards
                    .filter({$0.isSelect})
                    /// 4. 檢查 卡片內選項是否有被選擇
                    .filter({ card in
                        card.infos.contains(where: {$0.isSelect})
                    }).isEmpty {
                    return nil
                }
                return group
            }
        /// 5. 更新授權按鈕樣式
        updateVerifyButtonSubject.send(!filterGroupList.isEmpty && grorpIsAuthorize && !customFieldIsEmpty)
    }
}

