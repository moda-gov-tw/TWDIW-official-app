//
//  ParseLinkManager.swift
//  DigitalWallet
//

import Foundation
import Combine

class ParseLinkManager: ParseLinkProtocol {
    
    
    enum CrossAppURLScheme: String {
        case https = "https"
        case modadigitalwallet = "modadigitalwallet"
    }
    
    enum CrossAppPath: String {
        case staticQRCode = "/api/moda/qrcode"
        case vc01 = "/api/moda/vcqrcode"
        case vp01 = "/api/moda/vpqrcode"
    }
    
    enum CrossAppHost: String {
        case credential_offer = "credential_offer"
        case authorize = "authorize"
    }
    
    /// crossApp
    let crossAppCurrentValueSubject = CurrentValueSubject<CrossAppStatus, Never>(.idle)
    var inAppCurrentValueSubject = PassthroughSubject<Bool, Never>()
    
    func parseLink(_ url: URL) -> ParseLinkResult {
        if url.scheme == CrossAppURLScheme.https.rawValue {
            return handleUniversalLink(url: url)
        } else if url.scheme == CrossAppURLScheme.modadigitalwallet.rawValue {
            return handleQRCodeURL(url: url)
        } else {
            return .parseError
        }
    }
        
    private func handleUniversalLink(url: URL) -> ParseLinkResult {
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: false),
              let queryItems = components.queryItems,
              let mode = queryItems.first(where: {$0.name == "mode"})?.value
        else {
            return .parseError
        }
        
        if url.path == CrossAppPath.staticQRCode.rawValue {
            return handleStaticQRCode(mode: mode, queryItems: queryItems)
        } else if url.path == CrossAppPath.vc01.rawValue || url.path == CrossAppPath.vp01.rawValue {
            return handleQRCode(mode: mode, queryItems: queryItems)
        } else {
            return .parseError
        }
    }
    
    private func handleQRCodeURL(url: URL) -> ParseLinkResult {
        guard let host = url.host else {
            return .parseError
        }
        if host == CrossAppHost.credential_offer.rawValue {
            return .parseVC(qrcode: url.absoluteString)
        } else if host == CrossAppHost.authorize.rawValue {
            return .parseVP(qrcode: url.absoluteString)
        } else {
            return .parseError
        }
    }
    
    private func handleStaticQRCode(mode: String, queryItems: [URLQueryItem]) -> ParseLinkResult {
        if mode == Config.CrossApp.vc {
            let vcUID = queryItems.first(where: { $0.name == "vcUid" })?.value ?? ""
            return .staticVC(vcUID: vcUID)
        } else if mode == Config.CrossApp.vp {
            let vpUID = queryItems.first(where: { $0.name == "vpUid" })?.value ?? ""
            return .staticVP(vpUID: vpUID)
        } else {
            return .parseError
        }
    }
    
    private func handleQRCode(mode: String, queryItems: [URLQueryItem]) -> ParseLinkResult {
        if mode == "vc01" {
            let deepLink = queryItems.first(where: { $0.name == "deeplink"})?.value ?? ""
            return .parseVC(qrcode: decodeInnerDeeplink(from: deepLink))
        } else if mode == "vp01" {
            let deepLink = queryItems.first(where: { $0.name == "deeplink"})?.value ?? ""
            return .parseVP(qrcode: decodeInnerDeeplink(from: deepLink))
        } else {
            return .parseError
        }
    }
    
    private func decodeInnerDeeplink(from base64Param: String) -> String {
        guard let percentDecoded = base64Param.removingPercentEncoding,
              let data = Data(base64Encoded: percentDecoded),
              let innerString = String(data: data, encoding: .utf8) else {
            return ""
        }
        return innerString
    }
}
