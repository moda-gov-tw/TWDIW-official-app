//
//  LoginSettingViewModel.swift
//  DigitalWallet
//


import Combine
import Foundation
import LocalAuthentication

class LoginSettingViewModel {
    
    let createWalletRepository: CreateWalletRepositoryProtocol
    let biometricVerifyManager: BiometricVerifyProtocol
    @Published var isLoading: Bool = false
    let finishCreateWalletSubject = PassthroughSubject<Void, Never>()
    let createWalletWithPinCodeSubject = PassthroughSubject<Void, Never>()
    let createWalletFailSubject = PassthroughSubject<String, Never>()
    var cancelSet = Set<AnyCancellable>()
    
    init(repository: CreateWalletRepositoryProtocol,
         biometricVerifyManager: BiometricVerifyProtocol) {
        self.createWalletRepository = repository
        self.biometricVerifyManager = biometricVerifyManager
        initBinding()
        initWalletName()
    }
    
    private func initWalletName() {
        createWalletRepository.setWalletName(walletName: "我的皮夾")
    }
    
    func initBinding() {
        createWalletRepository.finishCreateWalletSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                isLoading = false
                finishCreateWalletSubject.send()
            }.store(in: &cancelSet)
    }
    
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
                try await createWalletRepository.createWallet(type: type)
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
