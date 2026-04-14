//
//  TermsViewController.swift
//  DigitalWallet
//

import UIKit
import WebKit
import Combine

class TermsViewController: BaseViewController {

    @IBOutlet weak var termsWebView: WKWebView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var agreeButton: UIButton!
    @IBOutlet weak var buttonsStackView: UIStackView!
    
    let viewModel: TermsViewModel
    var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: TermsViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "TermsViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        initView()
        initBinding()
        viewModel.loadLocalHTMLFile()
    }
    
    func initView() {
        termsWebView.navigationDelegate = self
        agreeButton.layer.cornerRadius = agreeButton.frame.height / 2
        titleLabel.text = viewModel.terms.title
        switch viewModel.terms.content {
        case .License:
            title = NSLocalizedString("LicenseTermsTitle", comment: "")
        case .Privacy:
            title = NSLocalizedString("PrivacyTermsTitle", comment: "")
        case .VPAgreement:
            title = viewModel.terms.title + " " + "知情同意書"
            titleLabel.text = viewModel.terms.title + " " + "知情同意書"
        }
        
        buttonsStackView.isHidden = !viewModel.showButton
    }
    
    func initBinding() {
        viewModel.loadHTMLSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] type in
                guard let self,
                      let url = type.url else { return }
                switch type {
                case .License, .Privacy:
                    termsWebView.loadFileURL(url, allowingReadAccessTo: url)
                    break
                case .VPAgreement:
                    let request = URLRequest(url: url, cachePolicy: .reloadIgnoringLocalAndRemoteCacheData)
                    termsWebView.load(request)
                    break
                }
            }.store(in: &cancelSet)
        
        viewModel.finishSubject
            .receive(on: DispatchQueue.main)
            .sink {
                self.navigationController?.popViewController(animated: true)
            }.store(in: &cancelSet)
    }
    
    @IBAction func onClickAgree(_ sender: Any) {
        viewModel.agreeTerms()
    }
    
    @IBAction func onClickDisagree(_ sender: Any) {
        viewModel.disagreeTerms()
    }
}

extension TermsViewController: WKNavigationDelegate {
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        webView.ensureViewportMetaTag()
    }
    
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        let prefix = "http"
        guard let url = navigationAction.request.url, (url.absoluteString.hasPrefix(prefix) || url.isFileURL) else {
            decisionHandler(.cancel)
            return
        }
        decisionHandler(.allow)
    }
}
