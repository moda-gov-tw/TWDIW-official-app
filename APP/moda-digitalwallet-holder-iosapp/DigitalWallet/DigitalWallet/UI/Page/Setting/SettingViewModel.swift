//
//  SettingViewModel.swift
//  DigitalWallet
//

import Foundation
import Combine

class SettingViewModel {
    
    let repository: UserRepositoryProtocol
    var settingItems: [SettingItem] {
        get {
            var items: [SettingItem] = []
            items.append(SettingItem(itemName: NSLocalizedString("WalletSetting", comment: "皮夾設定"), type: .walletSetting, styleType: .normal))
            items.append(SettingItem(itemName: NSLocalizedString("QuestionCenter", comment: "問題中心"), type: .questionCenter, styleType: .normal))
            items.append(SettingItem(itemName: NSLocalizedString("DigitalWeb", comment: "數位憑證皮夾網站"), type: .walletWeb(URL(string: Config.CORPORTE_WEBSITE_URL)!), styleType: .normal))
            items.append(SettingItem(itemName: NSLocalizedString("Logout", comment: "登出"), type: .logout, styleType: .warning))
            return items
        }
    }
    var pushToWalletSettingSubject = PassthroughSubject<Void, Never>()
    var pushToPersonalSettingSubject = PassthroughSubject<Void, Never>()
    var questionCenterSubject = PassthroughSubject<Void, Never>()
    var openURLSubject = PassthroughSubject<URL, Never>()
    var doLogoutSubject = PassthroughSubject<Void, Never>()
    
    init(repository: UserRepositoryProtocol) {
        self.repository = repository
    }
    
    func doAction(type: SettingItemType) {
        switch type {
            case .walletSetting:
                pushToWalletSettingSubject.send()
                break
            case .personalSetting:
                pushToPersonalSettingSubject.send()
                break
            case .questionCenter:
                questionCenterSubject.send()
            case .walletWeb(let url):
                openURLSubject.send(url)
            case .logout:
                doLogoutSubject.send()
            default:
                break
        }
    }
}
