//
//  VerifyVCResponse.swift
//  DigitalWallet
//

import Foundation

struct VerifyVCResponse: Codable{
    var trust: Bool?//issuer信任清單
    var vc: Bool?//vc是否有效
    var issuer: Bool?//issuer簽章是否有效
    var exp: Bool?//是否過期
    var holder: Bool?//holder did是否一致
    var trustBadge: Bool? // 藍勾勾
    
    enum CodingKeys: String, CodingKey {
        case trust = "trust"
        case vc = "vc"
        case issuer = "issuer"
        case exp = "exp"
        case holder = "holder"
        case trustBadge = "trust_badge"
    }
}
