//
//  VerifiableCredentailResult.swift
//  DigitalWallet
//

import Foundation

struct VerifiableCredentailResult {
    var status: CardStatus
    var trustBadge: Bool
    
    init(status: CardStatus, trustBadge: Bool) {
        self.status = status
        self.trustBadge = trustBadge
    }
}
