//
//  VerifyVPRequest.swift
//  DigitalWallet
//

import Foundation

struct VerifyVPRequest: Codable {
    var vp: String
    
    enum CodingKeys: String, CodingKey {
        case vp = "vp"//JWT格式之VP
    }
}
