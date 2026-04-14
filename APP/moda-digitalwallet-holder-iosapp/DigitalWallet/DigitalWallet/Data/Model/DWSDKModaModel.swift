//
//  DWSDKModaModel.swift
//  DigitalWallet
//

import Foundation

struct DWSDKModaModel: Codable {
    let response: String?
}

struct DWSDKModaBaseModelResult<T: Codable>: Codable {
    let code: String?
    
    let message: String?
    
    let data: T?
}
