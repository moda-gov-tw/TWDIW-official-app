//
//  BaseWebViewViewModel.swift
//  DigitalWallet
//

import Combine
import WebKit

class BaseWebViewViewModel: WebViewViewModelProtocol {
    private let openURLSubject = PassthroughSubject<URL, Never>()
    var openURLPublisher: AnyPublisher<URL, Never> {
        openURLSubject.eraseToAnyPublisher()
    }
    
    func decidePolicy(for navigationAction: WKNavigationAction) -> WebNavigationDecision {
        guard let url = navigationAction.request.url else {
            return .allow
        }
        
        switch url.scheme {
        case "mailto", "modadigitalwallet":
            openURLSubject.send(url)
            return .cancel
        default:
            return .allow
        }
    }
    
    func handleScriptMessage(_ message: WKScriptMessage) {
        
    }
}
