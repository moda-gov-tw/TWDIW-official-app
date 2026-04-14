//
//  CryptoManager.swift
//  DigitalWallet
//

import Foundation
import CryptoKit

class CryptoManager {
    static func sha256(input: String) -> String {
        let inputData = Data(input.utf8)
        let hashed = SHA256.hash(data: inputData)
        
        return hashed.compactMap { String(format: "%02x", $0) }.joined()
    }
    
    static func encryptData(data: Data, key: SymmetricKey) throws -> Data {
        let seealedBox = try AES.GCM.seal(data, using: key)
        return seealedBox.combined!
    }
    
    static func decryptData(ciphertext: Data, key: SymmetricKey) throws -> Data {
        let sealedBox = try AES.GCM.SealedBox(combined: ciphertext)
        return try AES.GCM.open(sealedBox, using: key)
    }
}
