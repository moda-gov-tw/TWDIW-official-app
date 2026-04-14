//
//  BaseWebViewViewModel.swift
//  DigitalWallet
//


import Combine
import WebKit

class AddCertificateWebViewViewModel: WebViewViewModelProtocol {
    private let openURLSubject = PassthroughSubject<URL, Never>()
    var openURLPublisher: AnyPublisher<URL, Never> {
        openURLSubject.eraseToAnyPublisher()
    }
    
    let parseLinkManager: ParseLinkProtocol
    
    init(parseLinkManager: ParseLinkProtocol) {
        self.parseLinkManager = parseLinkManager
    }
    
    func decidePolicy(for navigationAction: WKNavigationAction) -> WebNavigationDecision {
        return .allow
    }
    
    func handleScriptMessage(_ message: WKScriptMessage) {
        // 判斷是不是定好的Name
        if message.name != WebScriptMessage.mobile.rawValue {
            return
        }
        // 解析body物件，取出deeplink
        guard let body = message.body as? [String: Any],
              let jsonData = try? JSONSerialization.data(withJSONObject: body),
              let decoded = try? JSONDecoder().decode(ScriptMessage.self, from: jsonData),
              let url = decoded.data?.deeplink,
              let type = decoded.data?.type
        else {
            return
        }
        if type == "webview" {
            parseLinkManager.crossAppCurrentValueSubject.send(.crossAppActive(url: url, isShowFullScreen: true))
        } else {
            parseLinkManager.crossAppCurrentValueSubject.send(.crossAppActive(url: url, isShowFullScreen: false))
        }
    }
}
