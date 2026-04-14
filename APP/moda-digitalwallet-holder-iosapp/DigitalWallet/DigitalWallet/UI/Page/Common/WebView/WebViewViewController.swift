//
//  WebViewViewController.swift
//  DigitalWallet
//

import UIKit
import WebKit
import Combine

class WebViewViewController: BaseViewController, WKUIDelegate, WKNavigationDelegate {
    @IBOutlet weak var wkWebView: WKWebView!
    
    private let url: String
    private let naviTitle: String
    private let isNeedNavigationBar: Bool
    private let isShowUrlOnTitle: Bool
    
    private let viewModel: WebViewViewModelProtocol
    private var cancelSet = Set<AnyCancellable>()
    
    /// 初始化帶入參數
    /// - Parameters:
    ///   - naviTitle: navi功能名稱
    ///   - url: 內嵌網址
    init(naviTitle: String, url: String, viewModel: WebViewViewModelProtocol, isNeedNavigationBar: Bool, isShowUrlOnTitle: Bool) {
        self.url = url
        self.naviTitle = naviTitle
        self.isNeedNavigationBar = isNeedNavigationBar
        self.isShowUrlOnTitle = isShowUrlOnTitle
        self.viewModel = viewModel
        super.init(nibName: "WebViewViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initView()
        setUpWebScript()
        initBinding()
        setNavigation()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }
    
    private func initView() {
        
        wkWebView.uiDelegate = self
        wkWebView.navigationDelegate = self
        if let url = URL(string: url) {
            wkWebView.load(URLRequest(url: url))
        }
        if #available(iOS 16.4,*) {
            self.wkWebView.isInspectable = true
        }
        wkWebView.isUserInteractionEnabled = true
        
        if isNeedNavigationBar {
            navigationController?.navigationBar.isHidden = false
        }
    }
    
    /// 設定`JavaScript`
    private func setUpWebScript() {
        WebScriptMessage.allCases.forEach({ [weak self] scriptName in
            guard let self else { return }
            wkWebView.configuration.userContentController.add(self, name: scriptName.rawValue)
        })
        wkWebView.addUserAgent()
    }
    
    private func initBinding() {
        viewModel.openURLPublisher
            .receive(on: DispatchQueue.main)
            .sink { url in
                UIApplication.shared.open(url)
            }.store(in: &cancelSet)
    }
    
    private func setNavigation() {
        navigationController?.navigationBar.standardAppearance = TransparentNavigationBarAppearance()
        navigationController?.navigationBar.scrollEdgeAppearance = TransparentNavigationBarAppearance()
        navigationController?.navigationBar.compactAppearance = TransparentNavigationBarAppearance()
        navigationItem.hidesBackButton = true
        // 判斷邏輯
        navigationItem.leftBarButtonItem = isShowUrlOnTitle ? UIBarButtonItem(image: UIImage(named: "DownArrow"), style: .plain, target: self, action: #selector(goBack)) : UIBarButtonItem(image: UIImage(named: "ArrowBackFiled"), style: .plain, target: self, action: #selector(goBack))
        navigationItem.rightBarButtonItem = isShowUrlOnTitle ? UIBarButtonItem(image: UIImage(named: "Cancel"), style: .plain, target: self, action: #selector(close)) : nil
        navigationItem.leftBarButtonItem?.tintColor = ._4_E_61_A_7
        navigationItem.rightBarButtonItem?.tintColor = ._4_E_61_A_7
        
        let titleLabel = UILabel()
        
        titleLabel.attributedText = setupNavitationTitle()
        titleLabel.numberOfLines = 2
        titleLabel.textAlignment = .center
        navigationItem.titleView = titleLabel
    }
    
    /// 設定NavigationTitle
    /// - Returns: NSMutableAttributedString
    private func setupNavitationTitle() -> NSMutableAttributedString {
        let titleText = NSMutableAttributedString()
        /// 1. 名稱
        let title = NSAttributedString(string: naviTitle,
                                       attributes:
                                        [.font : FontName.NotoSansTC_Bold.font.withSize(16.0),
                                         .foregroundColor: UIColor._0_E_0_E_0_E])
        titleText.append(title)
        
        /// 2. 連結
        if isShowUrlOnTitle {
            titleText.append(NSAttributedString(string: "\n"))
            let urlString = NSAttributedString(string: url,
                                               attributes:
                                                [.font : FontName.NotoSansTC_Regular.font.withSize(10.0),
                                                 .foregroundColor: UIColor.AFAFAF])
            titleText.append(urlString)
        }
        return titleText
    }
    
    /// 關閉
    @objc private func close() {
        navigationController?.popViewController(animated: true)
    }
    
    /// 返回
    @objc private func webGoBack() {
        if wkWebView.canGoBack {
            wkWebView.goBack()
        }else{
            navigationController?.popViewController(animated: true)
        }
    }
    
    /// 連線失敗的情境
    private func connectionFailed() {
        showAlert(title: NSLocalizedString("ConnectionFailedTitle", comment: "連線失敗"), content: NSLocalizedString("ConnectionFailedContent", comment: "原因可能是目前使用裝置或該機關網路異常,請注意裝置連線情況，稍後再嘗試使用。")) { [weak self] in
            guard let self else { return }
            navigationController?.popViewController(animated: true)
        }
    }
}


extension WebViewViewController: WKScriptMessageHandler {
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        viewModel.handleScriptMessage(message)
    }
}

extension WebViewViewController {
    /**webView收到新的事件時，進行處理*/
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        let decision = viewModel.decidePolicy(for: navigationAction)
        switch decision {
        case .allow:
            decisionHandler(.allow)
        case .cancel:
            decisionHandler(.cancel)
        }
    }
    
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        showLoading(style: .white)
    }
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        dismissLoading()
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: any Error) {
        dismissLoading()
        connectionFailed()
    }
    
    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: any Error) {
        dismissLoading()
        connectionFailed()
    }
}
