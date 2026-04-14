//
//  KeyData.swift
//  DigitalWallet
//

import Foundation

struct KeyData: Codable {
    /***/
    var publicKey: PublicKey
}

struct PublicKey: Codable {
    /***/
    var kty: String
    /***/
    var crv: String
    /***/
    var x: String
    /***/
    var y: String
}
