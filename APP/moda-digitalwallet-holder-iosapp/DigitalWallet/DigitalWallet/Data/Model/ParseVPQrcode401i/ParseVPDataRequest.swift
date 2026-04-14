//
//  ParseVPDataRequest.swift
//  DigitalWallet
//

import Foundation

/// `dwsdk-401i`
struct ParseVPDataRequest: Codable {
    
    /// `QRCODE` `base64string`
    var qrCode: String
    
    /// 前台服務 `url`
    var frontUrl: String
    
    enum CodingKeys: String, CodingKey {
        case qrCode = "qrCode"
        case frontUrl = "frontUrl"
    }
}
