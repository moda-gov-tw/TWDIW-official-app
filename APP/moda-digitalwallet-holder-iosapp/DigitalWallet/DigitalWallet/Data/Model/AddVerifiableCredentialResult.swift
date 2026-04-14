//
//  AddVerifiableCredentialResult.swift
//  DigitalWallet
//

import Foundation

struct AddVerifiableCredentialResult {
    let verifiableCredential: ApplyVCDataResponse
    let state: CardStatus
    let orgName: String
    let cardImage: Data?
    let displayName: String?
    let verifyResult: VerifiableCredentailResult
}
