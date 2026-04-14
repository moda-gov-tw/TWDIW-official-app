//
//  DownloadAllVCListRequest.swift
//  DigitalWallet
//

import Foundation

struct DownloadAllVCListRequest: Codable {
    /***/
    var vcs: Array<String>
    
    var vcList: Array<Dictionary<String, Array<VCJWTAndSHA256Hash>>>
    
    enum CodingKeys: String, CodingKey {
        case vcs = "vcs"
        case vcList = "vcList"
    }
}
