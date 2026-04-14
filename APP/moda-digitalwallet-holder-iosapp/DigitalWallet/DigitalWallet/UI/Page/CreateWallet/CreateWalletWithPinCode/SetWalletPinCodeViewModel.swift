//
//  SetWalletPinCodeViewModel.swift
//  DigitalWallet
//

import Foundation
import Combine

class SetWalletPinCodeViewModel: SetWalletPinCodeProtocol {
    let repository: UserRepositoryProtocol
    /// 是否微生物辨識
    let isBiometric: Bool
    
    var title: String {
        switch type {
        case .create:
            return NSLocalizedString("ChangeWalletPinCode", comment: "設定皮夾密碼")
        case .update:
            return NSLocalizedString("UpdateWalletPinCode", comment: "修改皮夾密碼")
        }
    }
    var content: String {
        switch type {
        case .create:
            return NSLocalizedString("PinCodeSettingMessage", comment: "設定當前皮夾的皮夾密碼，若您已開啟生物辨識，預設登入模式將維持不變。")
        case .update:
            return NSLocalizedString("PinCodeUpdateMessage", comment: "修改當前皮夾的皮夾密碼，若您已開啟生物辨識，預設登入模式將維持不變。")
        }
    }
    var subTitle: String {
        NSLocalizedString("WalletBasicData", comment: "皮夾基本資料")
    }
    var textFieldTitle: String {
        switch type {
        case .create:
            return NSLocalizedString("ChangeWalletPinCode", comment: "設定皮夾密碼")
        case .update:
            return NSLocalizedString("SettingNewWalletPW", comment: "設定新皮夾密碼")
        }
    }
    var confirmTextFieldTitle: String {
        switch type {
        case .create:
            return NSLocalizedString("WalletPWConfirm", comment: "皮夾密碼確認")
        case .update:
            return NSLocalizedString("NewWalletPWConfirm", comment: "新皮夾密碼確認")
        }
    }
    var type: SetWalletPinCodeType {
        .update
    }
    var isLoadingSubject = PassthroughSubject<Bool, Never>()
    var inputErrorSubject = PassthroughSubject<PwdInputError, Never>()
    var finishSetupPinCodeSubject = PassthroughSubject<Void, Never>()
    var failSetupPinCodeSubject = PassthroughSubject<String, Never>()
    
    init(repository: UserRepositoryProtocol,
         isBiometric: Bool) {
        self.repository = repository
        self.isBiometric = isBiometric
    }
    
    func setupWalletPinCode(pinCode: String, confirmPinCode: String) {
        // 檢查密碼長度
        if pinCode.count < 4 {
            inputErrorSubject.send(.pwdLengthError)
            return
        }
        
        // 檢查密碼是否一樣
        guard pinCode == confirmPinCode else {
            inputErrorSubject.send(.pwdNotSameError)
            return
        }
        
        do {
            try repository.databaseManager.updatePinCode(userId: repository.wallet.uuid, pinCode: pinCode)
            repository.saveVerifiableCredentialRecord(vcUUID: UUID(), cardType: .changePassword, recordMessage: "修改「\(repository.wallet.name)」皮夾密碼", client: nil, purpose: nil, applyInfos: nil, createDate: Date())
            finishSetupPinCodeSubject.send()
        } catch {
            failSetupPinCodeSubject.send(error.localizedDescription)
        }
    }
}
