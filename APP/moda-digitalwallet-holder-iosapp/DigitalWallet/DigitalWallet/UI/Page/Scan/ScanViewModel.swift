//
//  ScanViewModel.swift
//  DigitalWallet
//

import Foundation
import Combine

class ScanViewModel: BaseViewModel {
    let repository: UserRepositoryProtocol
    let parseLinkManager: ParseLinkProtocol
    let parseQRCodeErrorSubject = PassthroughSubject<String, Never>()
    let openURLPassthroughtSubject = PassthroughSubject<String, Never>()
    var isViewControllerWillDisappeared = false
    var scanParsering: Bool = false//是否正在解析QRCode
    
    init(respository: UserRepositoryProtocol,
         parseLinkManager: ParseLinkProtocol) {
        self.repository = respository
        self.parseLinkManager = parseLinkManager
        super.init()
    }
    
    func parseQRCode(text: String) {
        /*若正在解析QRCode，不再進行處理*/
        if scanParsering {
            return
        }
        
        scanParsering = true
        
        guard let url = URL(string: text) else {
            scanParsering = false
            parseQRCodeErrorSubject.send(NSLocalizedString("ScanQRCodeError", comment: ""))
            return
        }
        parseLinkManager.crossAppCurrentValueSubject.send(.crossAppActive(url: url, isShowFullScreen: false))
        scanParsering = false
    }
}
