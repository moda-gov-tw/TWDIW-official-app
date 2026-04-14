//
//  WebViewViewModelProtocol.swift
//  DigitalWallet
//

import Combine
import WebKit

enum WebNavigationDecision {
    case allow
    case cancel
}

protocol WebViewViewModelProtocol {
    var openURLPublisher: AnyPublisher<URL, Never> { get }
    func decidePolicy(for navigationAction: WKNavigationAction) -> WebNavigationDecision
    func handleScriptMessage(_ message: WKScriptMessage)
}


