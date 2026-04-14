//
//  DecodeVCDataRequest.swift
//  DigitalWallet
//

import Foundation

struct DecodeVCDataRequest: Codable {
    /***/
    var credential: String
    
    enum CodingKeys: String, CodingKey {
        case credential = "credential"
    }
}
