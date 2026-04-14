//
//  AuthorizedCardListViewModel.swift
//  DigitalWallet
//

import Foundation

class AuthorizedCardListViewModel {
    let groupData: VerifyGroupData
    
    /// 需要被授權的清單
    var needAuthorizedCardList: [VirtualCard] {
        let cards = groupData.cards.map({$0.cardId})
        return groupData.requestCards.filter { requestCard in
            !cards.contains(where: {$0 == requestCard.cardId})
        }
    }
    
    init(groupData: VerifyGroupData) {
        self.groupData = groupData
    }
}
