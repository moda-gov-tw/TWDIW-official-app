//
//  GenerateVPRequest.swift
//  DigitalWallet
//

import Foundation

struct GenerateVPRequest: Codable  {
    /***/
    var reqT: String
    /***/
    var didFile: String
    /***/
    var vcs: [SelectVC]
    
    var frontUrl: String
    
    var customData: String?
    
    enum CodingKeys: String, CodingKey {
        case reqT = "request_token"
        case didFile = "didFile"
        case vcs = "vcs"
        case frontUrl = "frontUrl"
        case customData = "custom_data"
    }
}

struct SelectVC: Codable {
    /***/
    var vc: String
    /***/
    var field: [String]
    
    var cardId: String?
    
    enum CodingKeys: String, CodingKey {
        case vc = "vc"
        case field = "field"
        case cardId = "card_id"
    }
}

struct VPCustomData: Codable {
    
    var customData: [DwModa201iCustomField]?
    
    enum CodingKeys: String, CodingKey {
        case customData = "customData"
    }
    
    var jsonString: String? {
        guard let jsonData = try? JSONEncoder().encode(self),
              let json = String(data: jsonData, encoding: .utf8) else {
            return ""
        }
        return json
    }
}
