//
//  CreateWalletWithPinCodeViewModel.swift
//  DigitalWallet
//

import Foundation
import Combine

enum SetWalletPinCodeType {
    case create//建立皮夾密碼
    case update//修改皮夾密碼
}

protocol SetWalletPinCodeProtocol {
    var title: String { get }
    var content: String { get }
    var subTitle: String { get }
    var textFieldTitle: String { get }
    var confirmTextFieldTitle: String { get }
    var type: SetWalletPinCodeType { get }
    var isLoadingSubject: PassthroughSubject<Bool, Never> { get }
    var inputErrorSubject: PassthroughSubject<PwdInputError, Never> { get }
    var finishSetupPinCodeSubject: PassthroughSubject<Void, Never> { get }
    var failSetupPinCodeSubject: PassthroughSubject<String, Never> { get }
    func setupWalletPinCode(pinCode: String, confirmPinCode: String)
}

class CreateWalletWithPinCodeViewModel: SetWalletPinCodeProtocol {
    let createWalletRepository: CreateWalletRepositoryProtocol
    
    var title: String
    var content: String
    var subTitle: String
    var textFieldTitle: String
    var confirmTextFieldTitle: String
    var type: SetWalletPinCodeType = .create
    var isLoadingSubject = PassthroughSubject<Bool, Never>()
    var inputErrorSubject = PassthroughSubject<PwdInputError, Never>()
    var finishSetupPinCodeSubject = PassthroughSubject<Void, Never>()
    var failSetupPinCodeSubject = PassthroughSubject<String, Never>()
    var cancelSet = Set<AnyCancellable>()
    
    init(title: String, content: String, subTitle: String, textFieldTitle: String, confirmTextFieldTitle: String, createWalletRepository: CreateWalletRepositoryProtocol) {
        self.title = title
        self.content = content
        self.subTitle = subTitle
        self.textFieldTitle = textFieldTitle
        self.confirmTextFieldTitle = confirmTextFieldTitle
        self.createWalletRepository = createWalletRepository
    }
    
    func initBinding() {
        createWalletRepository.finishCreateWalletSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                finishSetupPinCodeSubject.send()
            }.store(in: &cancelSet)
    }
    
    func setupWalletPinCode(pinCode: String, confirmPinCode: String) {
        Task {
            // 檢查密碼長度
            if pinCode.count < 4 {
                inputErrorSubject.send(.pwdLengthError)
                return
            }
            
            // 檢查密碼是否一樣
            guard pinCode == confirmPinCode else {
                inputErrorSubject.send(.pwdNotSameError)
                return
            }
            
            do {
                isLoadingSubject.send(true)
                
                // 建立皮夾
                try await createWalletRepository.createWallet(type: .pinCode(pinCode: pinCode))
            } catch {
                isLoadingSubject.send(false)
                
                if let error = error as? DIDError {
                    switch error {
                        case .responseError(_, let message):
                            failSetupPinCodeSubject.send(message ?? NSLocalizedString("AppError", comment: ""))
                        default:
                            failSetupPinCodeSubject.send(NSLocalizedString("AppError", comment: ""))
                    }
                }else{
                    failSetupPinCodeSubject.send(NSLocalizedString("AppError", comment: ""))
                }
            }
        }
    }
}
