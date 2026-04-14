//
//  ModaUrlPath.swift
//  DigitalWallet
//

import Foundation

enum ModaUrlPath: CustomStringConvertible {
    case dwModa201i(uid: String, mode: String)
    /// App VC 卡片申請清單 API (VC05)
    case dwModa301i(name: String, page: String, size: String)
    /// DW-MODA-401i  App VP offline
    case dwModa401i(name: String, page: String, size: String)
    case dwModaMgr401i(url: String, path: String)
    case dwModaMgr402i(url: String)
    case dwVerifierMock101i(url: String, uid: String)
    
    var description: String {
        switch self {
        case .dwModa201i(let uid, let mode):
            return Config.FRONTEND_URL + "/api/moda/dwapp/serviceUrl/\(uid)?mode=\(mode)"
        case .dwModa301i(let name, let page, let size):
            return Config.FRONTEND_URL + "/api/moda/dwapp/apply/vcList?name=\(name)&page=\(page)&size=\(size)"
        case .dwModa401i(let name, let page, let size):
            return Config.FRONTEND_URL + "/api/moda/dwapp/offline/vpList?name=\(name)&page=\(page)&size=\(size)"
        case .dwModaMgr401i(let url, let path):
            return "\(url)/api/ext/offline/qrcode/\(path)"
        case .dwModaMgr402i(let url):
            return url + "/api/ext/offline/getEncryptionData"
        case .dwVerifierMock101i(let url, let uid):
            return url + "/\(uid)"
        }
    }
}
