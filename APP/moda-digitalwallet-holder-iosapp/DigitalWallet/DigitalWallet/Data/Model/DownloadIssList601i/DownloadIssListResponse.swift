//
//  DownloadIssListResponse.swift
//  DigitalWallet
//

import Foundation

struct DownloadIssListResponse: Codable {
    var did: String
    var org: Dictionary<String, String>
    var status: Double
    var createdAt: Double
    var updatedAt: Double
    
    enum CodingKeys: String, CodingKey {
        case did = "did"
        case org = "org"
        case status = "status"
        case createdAt = "createdAt"
        case updatedAt = "updatedAt"
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.did = try container.decode(String.self, forKey: .did)
        let rawData = try container.decode([String: RawToString].self, forKey: .org)
        self.org = rawData.mapValues { $0.stringValue }
        self.status = try container.decode(Double.self, forKey: .status)
        self.createdAt = try container.decode(Double.self, forKey: .createdAt)
        self.updatedAt = try container.decode(Double.self, forKey: .updatedAt)
    }
}
