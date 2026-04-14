//
//  VerifyVCOfflineRequest.swift
//  DigitalWallet
//

import Foundation

struct VerifyVCOfflineRequest: Codable {
    var credential: String
    var didFile: String
    var issList: Array<DownloadIssListResponse>
    var vcList: Array<Dictionary<String, Array<Dictionary<String, String>>>>
    
    enum CodingKeys: String, CodingKey {
        case credential = "credential"
        case didFile = "didFile"
        case issList = "issList"
        case vcList = "vcList"
    }
}
