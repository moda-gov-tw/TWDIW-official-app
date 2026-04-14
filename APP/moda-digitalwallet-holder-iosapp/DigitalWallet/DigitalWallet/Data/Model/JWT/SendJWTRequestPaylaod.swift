//
//  SendJWTRequest.swift
//  DigitalWallet
//

struct SendJWTRequestPaylaod: Codable {
    let sendJWTRequest: SendJWTRequest
}

struct SendJWTRequest: Codable {
    let didFile: String
    let payload: String
    let url: String
}



