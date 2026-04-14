//
//  CardInfomationViewModel.swift
//  DigitalWallet
//

import LocalAuthentication
import Combine

class CardInfomationViewModel {
    
    struct DisplayData {
        let displayName: String
        let value: String
        var isHide: Bool
    }
    
    let verifiableManager: VerifiableManagerProtocol
    let repository: UserRepositoryProtocol
    let databaseManager: DatabaseModelProtocol
    let biometricVerifyManager: BiometricVerifyProtocol
    
    var userVerifiableCredentialData: UserVerifiableCredentailData
    var displayDatas: [DisplayData] = []
    let finishDecodeSubject = PassthroughSubject<Void, Never>()
    let finishDeleteSubject = PassthroughSubject<Void, Never>()
    let decodeVCFailSubject = PassthroughSubject<String, Never>()
    let removeCardSubject = PassthroughSubject<Bool, Never>()
    
    init(verifiableManager: VerifiableManagerProtocol,
         databaseManager: DatabaseModelProtocol,
         verifiableCredential: UserVerifiableCredentailData,
         repository: UserRepositoryProtocol,
         biometricVerifyManager: BiometricVerifyProtocol) {
        self.verifiableManager = verifiableManager
        self.databaseManager = databaseManager
        self.userVerifiableCredentialData = verifiableCredential
        self.repository = repository
        self.biometricVerifyManager = biometricVerifyManager
    }
    
    func getDisplayData() {
        Task {
            do {
                let decodeVCData = try await verifiableManager.getVerifiableCredential(applyVCData: userVerifiableCredentialData.vc)
                
                if let datas = decodeVCData.vc?.credentialSubject?.field?.data {
                    for data in datas {
                        let key = data.first?.key ?? ""
                        let displayName = userVerifiableCredentialData.vc.credentialDefinition.credentialDefinition.credentialSubject[key]?.display.first?.name ?? key
                        let value = data.first?.value ?? ""
                        
                        displayDatas.append(DisplayData(displayName: displayName, value: value, isHide: false))
                    }
                    
                    finishDecodeSubject.send()
                } else {
                    decodeVCFailSubject.send(NSLocalizedString("AppError", comment: ""))
                }
            } catch {
                if let error = error as? DIDError {
                    switch error {
                        case .responseError(_, let message):
                            decodeVCFailSubject.send(message ?? NSLocalizedString("AppError", comment: ""))
                        default:
                            decodeVCFailSubject.send(NSLocalizedString("AppError", comment: ""))
                            break
                    }
                }else{
                    decodeVCFailSubject.send(NSLocalizedString("AppError", comment: ""))
                }
            }
        }
    }
    
    /**移除卡片前的驗證*/
    func deleteAuthentication(){
        
        if biometricVerifyManager.canEvaluatePolicy(policy: .deviceOwnerAuthentication) {
            biometricVerifyManager.verify(policy: .deviceOwnerAuthentication, message: BiometricLocalizedReason.verify){ isSuccess, error in
                if isSuccess {
                    self.removeCardSubject.send(true)
                }
            }
        } else {
            if !repository.wallet.pinCode.isEmpty {
                self.removeCardSubject.send(false)
            }
        }
    }
    
    /**移除卡片*/
    func deleteCard() {
        if let cardId = userVerifiableCredentialData.cardId {
            databaseManager.deleteVerifiableCredential(cardId: cardId)
            finishDeleteSubject.send()
        }
    }
    
    func recordDelete() {
        guard let cardId = userVerifiableCredentialData.cardId,
              let cardName = userVerifiableCredentialData.cardName else { return }
        repository.saveVerifiableCredentialRecord(vcUUID: cardId, cardType: .remove, recordMessage: "已移除「\(cardName)」", client: nil, purpose: nil, applyInfos: nil, createDate: Date())
    }
}
