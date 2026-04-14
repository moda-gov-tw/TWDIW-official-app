//
//  PinCodeLoginViewModel.swift
//  DigitalWallet
//


import Foundation
import Combine

class PinCodeLoginViewModel {
    
    let loginRepository: LoginRepositoryProtocol
    let loginSuccessSubject = PassthroughSubject<MyWallet, Never>()
    let loginFailSubject = PassthroughSubject<String?, Never>()
    var cancelSet = Set<AnyCancellable>()
    let wallet: MyWallet
    
    init(loginRepository: LoginRepositoryProtocol, wallet: MyWallet) {
        self.loginRepository = loginRepository
        self.wallet = wallet
        initBinding()
    }
    
    private func initBinding() {
        loginRepository.loginSuccessSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                loginSuccessSubject.send(wallet)
            }.store(in: &cancelSet)
        
        loginRepository.loginFailSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] errorMessage in
                guard let self = self else { return }
                loginFailSubject.send(errorMessage)
            }.store(in: &cancelSet)
    }
    
    func doLogin(pinCode: String) {
        loginRepository.doLoginWithPinCode(pinCode: pinCode, wallet: wallet)
    }
}
