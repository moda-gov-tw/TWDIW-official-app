//
//  PersonalManagementViewModel.swift
//  DigitalWallet
//

import Foundation
import UserNotifications
import Combine

class PersonalManagementViewModel {
    var settingItems: [SettingItem] = [
        SettingItem(itemName: NSLocalizedString("AutoLogoutTime", comment: ""), type: .autoLogoutSetting, styleType: .normal)
    ]
    
    let verifiableManager: VerifiableManagerProtocol
    let repository: UserRepositoryProtocol
    let openSettingSubject = PassthroughSubject<Void, Never>()
    let autoLogoutSettinSubject = PassthroughSubject<Void, Never>()
    
    init(verifiableManager: VerifiableManagerProtocol, repository: UserRepositoryProtocol) {
        self.verifiableManager = verifiableManager
        self.repository = repository
    }
    
    func doAction(type: SettingItemType) {
        switch type {
        case .autoLogoutSetting:
            autoLogoutSettinSubject.send()
        default:
            break
        }
    }
}
