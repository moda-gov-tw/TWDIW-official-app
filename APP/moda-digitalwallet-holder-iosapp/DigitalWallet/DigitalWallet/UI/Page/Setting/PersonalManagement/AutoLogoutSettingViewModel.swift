//
//  AutoLogoutSettingViewModel.swift
//  DigitalWallet
//

import Foundation
import Combine

class AutoLogoutSettingViewModel {
    let repository: UserRepositoryProtocol
    var selectItemCode: Int = TimeSelectEnum.Permanent.rawValue
    
    var timeSelectItem: [String] {
        get {
            var items: [String] = []
            
            items.append(TimeSelectEnum.Minute3.itemName)
            items.append(TimeSelectEnum.Minute5.itemName)
            items.append(TimeSelectEnum.Minute10.itemName)
            items.append(TimeSelectEnum.Minute15.itemName)
            items.append(TimeSelectEnum.Permanent.itemName)
            
            return items
        }
    }
    
    init(repository: UserRepositoryProtocol) {
        self.repository = repository
    }
    
    /**更新自動登出時間設定*/
    func updateWalletAutoLogout(autoLogout: Int){
        do{
            _ = try repository.databaseManager.updateWalletAutoLogout(userId: repository.wallet.uuid, autoLogout: Int16(autoLogout))
        }
        catch{
        }
        
        repository.refreshWalletData()
        AppDelegate.shared.countdownTimer?.startCountdown()
    }
}
