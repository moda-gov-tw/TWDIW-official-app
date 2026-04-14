//
//  ApplyVCRequest.swift
//  DigitalWallet
//

import Foundation

struct ApplyVCDataRequest: Codable {
    /***/
    var didFile: String
    /***/
    var qrCode: String
    
    var otp: String
    
    enum CodingKeys: String, CodingKey {
        case didFile = "didFile"
        case qrCode = "qrCode"
        case otp = "otp"
    }
}
