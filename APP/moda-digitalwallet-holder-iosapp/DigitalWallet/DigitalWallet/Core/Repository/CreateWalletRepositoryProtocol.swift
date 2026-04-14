//
//  CreateWalletRepositoryProtocol.swift
//  DigitalWallet
//

import Foundation
import Combine

protocol CreateWalletRepositoryProtocol {
    var walletName: String { get }
    var finishCreateWalletSubject: PassthroughSubject<Void, Never> { get }
    var isSameWalletNameSubject: PassthroughSubject<(Bool, String), Never> { get }
    func setWalletName(walletName: String)
    func createWallet(type: CreateWalletType) async throws
}
