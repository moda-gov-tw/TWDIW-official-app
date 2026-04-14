//
//  DownloadAllVCListResponse.swift
//  DigitalWallet
//

import Foundation

struct DownloadAllVCListResponse: Codable {
    /**成功回傳*/
    var result: Array<Dictionary<String, Array<VCJWTAndSHA256Hash>>>?
}

struct VCJWTAndSHA256Hash: Codable {
    let statusList: String?
    let error: Int?
}
