//
//  SendRequest.swift
//  DigitalWallet
//

import Foundation


struct SendRequest<T: Encodable>: Encodable {
    let url: String
    let type: RequestMethod
    let body: T
    
    enum CodingKeys: String, CodingKey {
        case url = "url"
        case type = "type"
        case body = "body"
    }
}

struct SendRequest2: Encodable {
    let url: String
    let type: RequestMethod
    let body: String?
    
    enum CodingKeys: String, CodingKey {
        case url = "url"
        case type = "type"
        case body = "body"
    }
}
