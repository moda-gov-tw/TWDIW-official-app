//
//  ScriptMessage.swift
//  DigitalWallet
//

import Foundation

struct ScriptMessage: Decodable {
    let type: String?
    let data: DeeplinkData?
}

struct DeeplinkData: Decodable {
    let qrCode: URL?
    let type: String?
    let deeplink: URL?
    let transactionId: UUID?
}
