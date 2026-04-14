//
//  LoginRepository.swift
//  DigitalWallet
//
import UIKit
import LocalAuthentication
import Combine

protocol LoginRepositoryProtocol {
    var loginSuccessSubject: PassthroughSubject<MyWallet, Never> { get }
    var loginFailSubject: PassthroughSubject<String?, Never> { get }
    func getWallets() -> [MyWallet]
    func getWallet(name: String) -> MyWallet?
    func doLoginWithBiometric(wallet: MyWallet)
    func doLoginWithPinCode(pinCode: String, wallet: MyWallet)
}


class LoginRepository: LoginRepositoryProtocol {
    
    private let databaseManager: DatabaseModelProtocol
    private let verifiableManager: VerifiableManagerProtocol
    private let biometricVerifyManager: BiometricVerifyManager
    let loginSuccessSubject = PassthroughSubject<MyWallet, Never>()
    let loginFailSubject = PassthroughSubject<String?, Never>()
    
    init(databaseManager: DatabaseModelProtocol,
         verifiableManager: VerifiableManagerProtocol,
         biometricVerifyManager: BiometricVerifyManager) {
        self.databaseManager = databaseManager
        self.verifiableManager = verifiableManager
        self.biometricVerifyManager = biometricVerifyManager
    }
    
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
    
    func getWallet(name: String) -> MyWallet? {
        let wallets = getWallets()
        let matchWallet = wallets.first { wallet in
            wallet.name == name
        }
        return matchWallet
    }
    
    func doLoginWithBiometric(wallet: MyWallet) {
        biometricVerifyManager.verify(policy: .deviceOwnerAuthentication, message: BiometricLocalizedReason.verify) { [weak self] isSuccess, error in
            guard let self else { return }
            if isSuccess {
                self.setUpKxLogin(wallet: wallet)
            }
        }
    }
    
    func doLoginWithPinCode(pinCode: String, wallet: MyWallet) {
        let walletPinCode = wallet.pinCode
        
        if pinCode == walletPinCode {
            setUpKxLogin(wallet: wallet)
        } else {
            loginFailSubject.send(nil)
        }
    }
    
    /// 登入時要切換keyTag
    /// - Parameter wallet: 皮夾物件
    private func setUpKxLogin(wallet: MyWallet) {
        Task {
            do {
                let uuid = wallet.uuid.uuidString
                try await verifiableManager.initializationKxSDK(walletUUID: uuid)
                loginSuccessSubject.send(wallet)
            } catch {
                loginFailSubject.send(error.localizedDescription)
            }
        }
    }
}
