//
//  WKWebView+Extension.swift
//  DigitalWallet
//

import WebKit

extension WKWebView {
    
    /// 檢查HTML是否有ViewPortTag，沒有就自動補上
    func ensureViewportMetaTag() {
        let checkMetaScript = """
            (function() {
                var metas = document.getElementsByTagName('meta');
                for (var i = 0; i < metas.length; i++) {
                    if (metas[i].name === 'viewport') {
                        return true;
                    }
                }
                return false;
            })();
        """

        self.evaluateJavaScript(checkMetaScript) { result, error in
            if let hasViewport = result as? Bool, !hasViewport {
                let insertMetaScript = """
                    (function() {
                            var meta = document.createElement('meta');
                            meta.name = 'viewport';
                            meta.content = 'width=device-width, initial-scale=1.0';
                            document.getElementsByTagName('head')[0].appendChild(meta);
                            return true;
                        })();
                """
                self.evaluateJavaScript(insertMetaScript) { _, error in
                    
                }
            }
        }
    }
    
    func addUserAgent() {
        self.evaluateJavaScript(Config.userAgent) { [weak self] oldAgent, error in
            guard let self,
                  let strOldAgent = oldAgent as? String else {
                return
            }
            let iOS = "iOS"
            let version = Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as! String
            let appendingAgent = String(format: Config.webViewFormate, version, iOS)
            let newAgent = strOldAgent.appending(appendingAgent)
            customUserAgent = newAgent
        }
    }
}
