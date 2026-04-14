//
//  VerifyVCRequest.swift
//  DigitalWallet
//

import Foundation

struct VerifyVCRequest: Codable {
    var credential: String
    var didFile: String
    var frontUrl: String
    
    enum CodingKeys: String, CodingKey {
        case credential = "credential"
        case didFile = "didFile"
        case frontUrl = "frontUrl"
    }
}
