//
//  RawDecodable.swift
//  DigitalWallet
//

import Foundation

struct RawToString: Decodable {
    let stringValue: String
    
    init(from decoder: Decoder) throws {
        if let stringValue = try? decoder.singleValueContainer().decode(String.self) {
            self.stringValue = stringValue
        } else if let intValue = try? decoder.singleValueContainer().decode(Int.self) {
            self.stringValue = String(intValue)
        } else if let boolValue = try? decoder.singleValueContainer().decode(Bool.self) {
            self.stringValue = String(boolValue)
        } else if let doubleValue = try? decoder.singleValueContainer().decode(Double.self) {
            self.stringValue = String(doubleValue)
        } else {
            throw DecodingError.dataCorruptedError(in: try decoder.singleValueContainer(), debugDescription: "Unsupported type")
        }
    }
}
