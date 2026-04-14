//
//  CreateWalletRepository.swift
//  DigitalWallet
//

import Foundation
import Combine

class CreateWalletRepository: CreateWalletRepositoryProtocol {
    var databaseManager: DatabaseModelProtocol
    let verifiableManager: VerifiableManagerProtocol
    var finishCreateWalletSubject = PassthroughSubject<Void, Never>()
    var isSameWalletNameSubject = PassthroughSubject<(Bool, String), Never>()
    
    private(set) var walletName: String = ""
    
    init(databaseManager: DatabaseModelProtocol, verifiableManager: VerifiableManagerProtocol) {
        self.databaseManager = databaseManager
        self.verifiableManager = verifiableManager
    }
    
    /// 檢查皮夾名稱是否重複
    /// - Parameter walletName: 皮夾名稱
    func setWalletName(walletName: String) {
        if checkWalletName(walletName: walletName) {
            isSameWalletNameSubject.send((true, walletName))
        } else {
            self.walletName = walletName
            isSameWalletNameSubject.send((false, walletName))
        }
    }
    
    /// 檢查是否有一樣的皮夾名稱
    /// - Returns: 是否有一樣的皮夾名稱
    private func checkWalletName(walletName: String) -> Bool {
        guard let wallets = databaseManager.fetchWallets() else {
            return false
        }
        
        return wallets.map { wallet in
            wallet.name
        }.contains(where: { name in
            name == walletName
        })
    }
    
    func createWallet(type: CreateWalletType) async throws {
        // 建立皮夾
        let uuid = UUID()
        let didDataResponse = try await verifiableManager.createDecentralizedIdentifier(uuid: uuid.uuidString)
        let didDataString = try JSONEncoder().encode(didDataResponse).base64EncodedString()
        
        switch type {
        case .biometrics:
            databaseManager.createWallet(walletName: walletName, pinCode: nil, uuid: uuid, didFile: didDataString, keyData: nil, createDate: Date())
            
        case .pinCode(let pinCode):
            databaseManager.createWallet(walletName: walletName, pinCode: pinCode, uuid: uuid, didFile: didDataString, keyData: nil, createDate: Date())
        }
        
        finishCreateWalletSubject.send()
    }
}
