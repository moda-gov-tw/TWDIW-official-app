//
//  BaseViewController.swift
//  DigitalWallet
//

import UIKit
import Network

class BaseViewController: UIViewController {
    enum BackButtonType {
        case normal
        case fill
        
        var image: UIImage {
            switch self {
            case .normal:
                    .arrowBack
            case .fill:
                    .arrowBackFiled
            }
        }
    }
    
    enum LoadingViewType {
        case safeArea
        case fill
    }
    
    let loadingVC = LoadingViewController(nibName: "LoadingViewController", bundle: nil)
    let verifyingVC = VerifyingViewController(nibName: "VerifyingViewController", bundle: nil)
    var isHideNavigationBar: Bool = false
    var backButtonType: BackButtonType = .fill {
        didSet {
            setupNavigationBar(backButtonType)
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        NotificationCenter.default.addObserver(self, selector: #selector(handleUnsubscribeVCNotification(_:)), name: Notification.Name(Config.Notification.unsubscribeVC), object: nil)
        setupNavigationBar(backButtonType)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        //自動登出重新計時
        AppDelegate.shared.countdownTimer?.startCountdown()
        navigationController?.navigationBar.isHidden = isHideNavigationBar
    }
    
    func setupNavigationBar(_ type: BackButtonType) {
        navigationItem.hidesBackButton = true
        navigationItem.leftBarButtonItem = UIBarButtonItem(image: type.image, style: .plain, target: self, action: #selector(goBack))
    }
    
    @objc private func handleUnsubscribeVCNotification( _ notification: Notification) {
        if let userInfo =  notification.userInfo {
            if let title = userInfo["title"] as? String,
               let message = userInfo["message"] as? String {
                showAlert(title: title, content: message)
            }
        }
    }
    
    func showLoading(style: LoadingViewController.LoadingStyle = .normal) {
        loadingVC.modalPresentationStyle = .overFullScreen
        loadingVC.loadingStyle = style
        present(loadingVC, animated: false)
    }
    
    func dismissLoading() {
        loadingVC.dismiss(animated: false)
    }
    
    func showVerifying() {
        verifyingVC.modalPresentationStyle = .overFullScreen
        present(verifyingVC, animated: false)
    }
    
    func dismissVerifying() {
        verifyingVC.dismiss(animated: true)
    }

    func showAlert(title: String = NSLocalizedString("MessageTitle", comment: ""), content: String, complete: (()-> Void)? = nil) {
        let alert = CustomAlertViewController(title: title, content: content, leftButtonTitle: nil, leftButtonCompletion: nil,
                                              rightButtonTitle: NSLocalizedString("Confirm", comment: ""),
                                              rightButtonCompletion: {
            complete?()
        })
        
        present(alert, animated: true)
    }
    
    /// 顯示警告視窗
    /// - Parameters:
    ///   - title: 標題
    ///   - content: 內容
    ///   - complete: 點擊確認後回調
    func showWarningAlert(title: String = NSLocalizedString("MessageTitle", comment: ""), content: String, complete: (()-> Void)? = nil){
        let alert = CustomWarningViewViewController(title: title, content: content, buttonCompletion: {
            complete?()
        })
        
        present(alert, animated: true)
    }
    
    /// 二選一詢問視窗
    /// - Parameters:
    ///   - title: 標題
    ///   - content: 詢問內容
    ///   - complete: 點擊結果
    func askAlert(title: String, content: String,
                  leftButtonTitle: String = NSLocalizedString("Cancel", comment: "取消"),
                  rightButtonTitle: String = NSLocalizedString("Confirm", comment: "確認"),
                  complete: @escaping ((Bool) -> Void)){
        let alert = CustomAlertViewController(title: title, content: content,
        
        leftButtonTitle: leftButtonTitle, leftButtonCompletion: {
            complete(false)
        }, rightButtonTitle: rightButtonTitle, rightButtonCompletion: {
            complete(true)
        })
        
        present(alert, animated: true)
    }
    
    @objc func goBack() {
        navigationController?.popViewController(animated: true)
    }
    
    /// - Parameter completion: 回傳是否有網路
    func checkNetworkStatus(completion: @escaping (Bool) -> Void) {
        let monitor = NWPathMonitor()
        let queue = DispatchQueue.main

        monitor.pathUpdateHandler = { path in
            if path.status == .satisfied {
                completion(true)  // 有網路
            } else {
                completion(false) // 無網路
            }
            monitor.cancel() // 只檢查一次就取消
        }

        monitor.start(queue: queue)
    }
    
    /// 前往內坎網站
    func checkNetworkAndToWebView(_ urlString: String, title: String, viewModel: WebViewViewModelProtocol =  BaseWebViewViewModel(), isNeedNavigationBar: Bool = false, isShowUrlOnTitle: Bool) {
        
        checkNetworkStatus { [weak self] hasNetwork in
            guard let self else { return }
            if hasNetwork {
                let vc = WebViewViewController(naviTitle: title,
                                               url: urlString,
                                               viewModel: viewModel,
                                               isNeedNavigationBar: isNeedNavigationBar,
                                               isShowUrlOnTitle: isShowUrlOnTitle)
                navigationController?.pushViewController(vc, animated: true)
            } else {
                showAlert(title: NSLocalizedString("ConnectionFailedTitle", comment: "連線失敗"),
                          content: NSLocalizedString("ConnectionFailedContent", comment: "原因可能是目前使用裝置或該機關網路異常,請注意裝置連線情況，稍後再嘗試使用。"))
            }
        }
    }
}
