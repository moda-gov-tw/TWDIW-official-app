//
//  Untitled.swift
//  DigitalWallet
//

import Combine
import Foundation

class RemindAlertViewViewModel {
    
    var cancelSet = Set<AnyCancellable>()
    @Published var remindDatas: [UserVerifiableCredentailData]?
    @Published var displayedRemindDatas: [UserVerifiableCredentailData]?
    
    init() {
    }
    
    private func dataBinding() {
        $remindDatas
            .sink { [weak self] data in
                guard let self = self else { return }
            }
            .store(in: &cancelSet)
    }
    
}
