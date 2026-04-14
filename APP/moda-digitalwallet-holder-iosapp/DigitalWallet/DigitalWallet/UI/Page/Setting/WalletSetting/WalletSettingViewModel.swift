//
//  WalletSettingViewModel.swift
//  DigitalWallet
//


import LocalAuthentication
import Combine

class WalletSettingViewModel {
    
    enum ActionType {
        case changeWalletPinCode
    }
    
    let verifyUserSubject = PassthroughSubject<(Bool, ActionType), Never>()
    let repository: UserRepositoryProtocol
    let biometricVerifyManager: BiometricVerifyProtocol
    
    var settingItems: [SettingItem] {
        let canUseBiometrics = LAContext().canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: nil)
        
        /*有設定PinCode*/
        let settingPinCodeName = if repository.wallet.pinCode == "" {
            NSLocalizedString("ChangeWalletPinCode", comment: "設定皮夾密碼")
        }else{
            NSLocalizedString("UpdateWalletPinCode", comment: "修改皮夾密碼")
        }
        
        var items: Array<SettingItem> = []
        items.append(contentsOf:[
            SettingItem(itemName: settingPinCodeName, type: .changePinCode, styleType: .normal),
            SettingItem(itemName: NSLocalizedString("BiometricSettings", comment: "生物辨識設定"), type: .settingBiometrics, styleType: .withSwitchButton(isOn: canUseBiometrics)),
        ])
       
        return items
    }
    
    init(repository: UserRepositoryProtocol,
         biometricVerifyManager: BiometricVerifyProtocol) {
        self.repository = repository
        self.biometricVerifyManager = biometricVerifyManager
    }
    
    func doAction(type: SettingItemType) {
        switch type {
        case .changePinCode:
            verifyUser(type: .changeWalletPinCode)
        default:
            break
        }
    }
    
    func verifyUser(type: ActionType) {
        if biometricVerifyManager.canEvaluatePolicy(policy: .deviceOwnerAuthentication) {
            biometricVerifyManager.verify(policy: .deviceOwnerAuthentication, message: BiometricLocalizedReason.verify){
                [weak self] isSuccess, error in
                guard let self else { return }
                if isSuccess {
                    verifyUserSubject.send((true, type))
                }
            }
        } else {
            if !repository.wallet.pinCode.isEmpty {
                verifyUserSubject.send((false, type))
            }
        }
    }
}
