//
//  EnterPinCodeViewModel.swift
//  DigitalWallet
//


import Foundation
import Combine

class EnterPinCodeViewModel {
    let repository: UserRepositoryProtocol
    let pinCodeErrorSubject = PassthroughSubject<Void, Never>()
    let veriflySuccessSubject = PassthroughSubject<Void, Never>()
    let title: String?
    
    init(repository: UserRepositoryProtocol, title: String? = nil) {
        self.repository = repository
        self.title = title
    }
    
    func veriflyUser(pinCode: String) {
        /*驗證成功*/
        if pinCode == repository.wallet.pinCode {
            veriflySuccessSubject.send()
        } else {
            pinCodeErrorSubject.send()
        }
    }
}
