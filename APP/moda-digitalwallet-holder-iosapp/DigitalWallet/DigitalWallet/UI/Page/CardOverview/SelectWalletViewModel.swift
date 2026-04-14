//
//  SelectWalletViewModel.swift
//  DigitalWallet
//

import Foundation
import Combine

class SelectWalletViewModel {
    
    let repository: UserRepositoryProtocol
    var wallets: [MyWallet] {
        get {
            repository.getWallets()
        }
    }
    let logoutSubject = PassthroughSubject<Void, Never>()
    
    init(repository: UserRepositoryProtocol) {
        self.repository = repository
    }
    
    func selectedWallet(wallet: MyWallet) {
        // 把上次登入的皮夾改為本次選取的
        UserDefaultManager.shared.setObject(value: wallet.uuid, key: .DefaultLoginWallet)
        logoutSubject.send()
    }
}
