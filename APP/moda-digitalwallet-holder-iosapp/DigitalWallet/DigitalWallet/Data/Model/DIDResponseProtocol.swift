//
//  DIDResponseProtocol.swift
//  DigitalWallet
//

import Foundation

protocol DIDResponseProtocol {
    
    associatedtype T: Codable
    
    var code: String { get set }
    
    var message: String { get set }
    
    var data: T { get set }
}
