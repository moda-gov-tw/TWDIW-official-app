//
//  GenerateKxRequest.swift
//  DigitalWallet
//

import Foundation

struct GenerateKxRequest: Codable {
    var keyTag: String
    var type: String
    var pinForKx: String?
    
    enum CodingKeys: String, CodingKey {
        case keyTag = "keyTag"
        case type = "type"
        case pinForKx = "PIN"
    }
}
