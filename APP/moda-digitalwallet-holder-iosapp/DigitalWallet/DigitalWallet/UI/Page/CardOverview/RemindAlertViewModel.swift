//
//  RemindAlertViewModel.swift
//  DigitalWallet
//

import Combine
import Foundation

class RemindAlertViewModel {
    /// 更新UI
    let refreshUISubject = PassthroughSubject<Void, Never>()
    /// 檢查清單
    var repository: UserRepositoryProtocol
    var showList: [ShowRemindListType] = []
    var remindList: [Remind]
    
    var finishUpdateRemind: (() -> Void)
    
    init(repository: UserRepositoryProtocol, remindList: [Remind], finishUpdateRemind: @escaping (() -> Void)) {
        self.repository = repository
        self.remindList = remindList
        self.finishUpdateRemind = finishUpdateRemind
    }
    
    public func handleRemindData() {
        if remindList.contains(where: {
            if case .networkError = $0 {
                return true
            } else {
                return false
            }
        }) {
            showList.append(.networkError)
        }
        
        let filterNoNetworkErrorData = remindList.filter {
            if case .networkError = $0 {
                return false
            } else {
                return true
            }
        }
        
        var sevenDaysList: [Remind] = []
        var invalidList: [Remind] = []
        var updateErrorList: [Remind] = []
        
        filterNoNetworkErrorData.forEach {
            switch $0 {
            case .networkError: break
            case .remind(let data):
                /// 檢查 `Remind` 是否有差別
                let remind = data.remind
                let updateRemind = data.checkRemind().rawValue
                let bUpdate = remind != updateRemind
                if bUpdate {
                    switch data.checkRemind() {
                    case .sevenDays:
                        sevenDaysList.append($0)
                    case .oneDay:
                        sevenDaysList.append($0)
                    case .invalid:
                        invalidList.append($0)
                    default: break
                    }
                }
                break
            case .updateError:
                updateErrorList.append($0)
            }
        }
        
        if !sevenDaysList.isEmpty {
            showList.append(.expiringList(sevenDaysList))
        }
        
        if !invalidList.isEmpty {
            showList.append(.expiredList(invalidList))
        }
        
        if !updateErrorList.isEmpty {
            showList.append(.updateErrorList(updateErrorList))
        }

        refreshUISubject.send()
    }
    
    public func updateRemind() {
        let datas: [UserVerifiableCredentailData] = remindList.compactMap({ remind in
            switch remind {
            case .networkError:
                return nil
            case .remind(let userVerifiableCredentailData):
                return userVerifiableCredentailData
            case .updateError(_):
                return nil
            }
        })
        
        datas.forEach { card in
            guard let cardId = card.cardId else { return }
            var remind = 0
            switch card.checkRemind() {
            case .effective:
                remind = 0
            case .sevenDays:
                remind = 7
            case .oneDay:
                remind = 1
            case .invalid:
                remind = -1
            }
            repository.updateVerifiableCredentialRemind(cardId: cardId, remind: remind)
        }
        finishUpdateRemind()
    }
}
