//
//  DIDDataResponse.swift
//  DigitalWallet
//

import Foundation

struct DIDDataResponse: Codable {
    /***/
    var context: [String]
    /***/
    var id: String
    /***/
    var verificationMethod: [VerificationMethod]
    
    enum CodingKeys: String, CodingKey {
        case context = "@context"
        case id = "id"
        case verificationMethod = "verificationMethod"
    }
}

struct VerificationMethod: Codable {
    /***/
    var id: String
    /***/
    var type: String
    /***/
    var controller: String
    /***/
    var publicKeyJwk: PublicKeyJwk
    
    enum CodingKeys: String, CodingKey {
        case id = "id"
        case type = "type"
        case controller = "controller"
        case publicKeyJwk = "publicKeyJwk"
    }
}

struct PublicKeyJwk: Codable {
    /***/
    var crv: String
    /***/
    var kty: String
    /***/
    var x: String
    /***/
    var y: String
    
    enum CodingKeys: String, CodingKey {
        case crv = "crv"
        case kty = "kty"
        case x = "x"
        case y = "y"
    }
}
