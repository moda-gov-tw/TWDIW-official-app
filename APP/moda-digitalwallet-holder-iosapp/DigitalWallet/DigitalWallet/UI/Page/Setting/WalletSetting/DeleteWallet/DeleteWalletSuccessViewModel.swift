//
//  DeleteWalletSuccessViewModel.swift
//  DigitalWallet
//

import Foundation
import Combine

class DeleteWalletSuccessViewModel {
    
    enum ActionType {
        case toLogin
        case toCreateWalllet
    }
    
    let repository: UserRepositoryProtocol
    let actionFinishSubject = PassthroughSubject<ActionType, Never>()
    
    init(repository: UserRepositoryProtocol) {
        self.repository = repository
    }
    
    /// 判斷是否還有已建立的皮夾，如果沒有就跳到皮夾建立歡迎頁，有的話就到登入頁
    func done() {
        let wallets = repository.getWallets()
        if wallets.isEmpty {
            actionFinishSubject.send(.toCreateWalllet)
        } else {
            actionFinishSubject.send(.toLogin)
        }
    }
}
