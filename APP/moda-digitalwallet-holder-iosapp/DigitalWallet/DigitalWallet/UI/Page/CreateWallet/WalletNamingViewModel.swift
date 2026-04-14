//
//  CreateWalletViewModel.swift
//  DigitalWallet
//

import Foundation
import Combine
import LocalAuthentication

class WalletNamingViewModel {
    /// 命名類型
    enum NamingType {
        /// 第一次
        case first
        /// 後面新增
        case others
    }
    let repository: CreateWalletRepositoryProtocol
    let databaseManager: DatabaseModelProtocol
    let biometricVerifyManager: BiometricVerifyProtocol
    let namingType: NamingType
    
    @Published var isLoading: Bool = false
    
    let pushToSettingLoginSubject = PassthroughSubject<CreateWalletRepositoryProtocol, Never>()
    let walletNameErrorSubject = PassthroughSubject<Void, Never>()
    let hasSameWalletNameSubject = PassthroughSubject<Void, Never>()
    let createWalletWithPinCodeSubject = PassthroughSubject<Void, Never>()
    let createWalletFailSubject = PassthroughSubject<String, Never>()
    let finishCreateWalletSubject = PassthroughSubject<Void, Never>()
    var cancelSet = Set<AnyCancellable>()
    
    init(databaseManager: DatabaseModelProtocol,
         repository: CreateWalletRepositoryProtocol,
         biometricVerifyManager: BiometricVerifyProtocol,
         namingType: NamingType) {
        self.databaseManager = databaseManager
        self.repository = repository
        self.biometricVerifyManager = biometricVerifyManager
        self.namingType = namingType
        
        initBinding()
    }
    
    func initBinding() {
        repository.isSameWalletNameSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] isSameName, _ in
                guard let self else { return }
                if isSameName {
                    hasSameWalletNameSubject.send()
                } else {
                    switch namingType {
                    case .first:
                        /// 首次新建皮夾
                        pushToSettingLoginSubject.send(repository)
                        break
                    case .others:
                        /// 第二次以後新建皮夾
                        authenticateUser()
                        break
                    }
                }
            }.store(in: &cancelSet)
        repository.finishCreateWalletSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                isLoading = false
                finishCreateWalletSubject.send()
            }.store(in: &cancelSet)
    }
    
    func createWallet(walletName: String?) {
        guard
            let walletName = walletName,
            !walletName.isEmpty,
            isEnglishChineseOrNumber(walletName)
        else {
            walletNameErrorSubject.send()
            return
        }
        
        repository.setWalletName(walletName: walletName)
    }
    
    /// 取得標題
    func walletNamingTitle() -> String {
        switch namingType {
        case .first:
            return NSLocalizedString("WalletName", comment: "")
        case .others:
            return NSLocalizedString("SetWalletNameTitle", comment: "")
        }
    }
    
    /**取得預設皮夾名稱*/
    func getDefaultWalletName() -> String{
        let wallet: Array<MyWallet> = databaseManager.fetchWallets() ?? Array()
        return String(format: NSLocalizedString("DefaultWalletName", comment: ""), "\(wallet.count+1)")
    }
    
    private func isEnglishChineseOrNumber(_ string: String) -> Bool {
        // 正則表達式：僅允許英文、中文和數字
        let pattern = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$"
        
        // 使用 NSPredicate 檢查字串是否符合正則表達式
        let predicate = NSPredicate(format: "SELF MATCHES %@", pattern)
        return predicate.evaluate(with: string)
    }
    
    
}


/// 非首次登入流程
extension WalletNamingViewModel {
    
    /// 驗證用戶
    func authenticateUser() {
        if biometricVerifyManager.canEvaluatePolicy(policy: .deviceOwnerAuthentication) {
            biometricVerifyManager.verify(policy: .deviceOwnerAuthentication, message: BiometricLocalizedReason.verify){ isSuccess, error in
                if isSuccess {
                    self.createWallet(type: .biometrics)
                }
            }
        } else {
            createWalletWithPinCodeSubject.send()
        }
    }
    
    /**創建皮夾*/
    private func createWallet(type: CreateWalletType) {
        Task {
            isLoading = true
            
            do {
                try await repository.createWallet(type: type)
            } catch {
                isLoading = false
                
                if let error = error as? DIDError {
                    switch error {
                        case .responseError(_, let message):
                            createWalletFailSubject.send(message ?? NSLocalizedString("AppError", comment: ""))
                        default:
                            createWalletFailSubject.send(NSLocalizedString("AppError", comment: ""))
                            break
                    }
                }else{
                    createWalletFailSubject.send(NSLocalizedString("AppError", comment: ""))
                }
            }
        }
    }
}
