//
//  DownloadIssListRequest.swift
//  DigitalWallet
//

import Foundation

struct DownloadIssListRequest: Codable {
    var url: String
    var newFlag: Bool
    
    enum CodingKeys: String, CodingKey {
        case url = "url"
        case newFlag = "newFlag"
    }
}
