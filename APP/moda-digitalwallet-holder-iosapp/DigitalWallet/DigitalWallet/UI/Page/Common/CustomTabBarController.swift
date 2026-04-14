//
//  CustomTabBarController.swift
//  DigitalWallet
//

import UIKit
import Combine

class CustomTabBarController: UITabBarController {
    
    static let selectIndexSubject = PassthroughSubject<Int, Never>()
    private let viewModel: CustomTabBarViewModel
    private var cancelSet = Set<AnyCancellable>()
    
    let loadingVC = LoadingViewController(nibName: "LoadingViewController", bundle: nil)
    let verifyingVC = VerifyingViewController(nibName: "VerifyingViewController", bundle: nil)
    
    init(viewModel: CustomTabBarViewModel) {
        self.viewModel = viewModel
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        initView()
        initBinding()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.navigationBar.isHidden = true
    }
    
    private func initView() {
        setupTabBar()
        delegate = self
    }

    private func initBinding() {
        CustomTabBarController.selectIndexSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] index in
                guard let self else { return }
                if index < self.children.count && index >= 0 {
                    navigationController?.popToRootViewController(animated: false)
                    self.selectedIndex = index
                }
            }.store(in: &cancelSet)
        
        viewModel.isLoadingSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] isLoading in
                if isLoading {
                    self?.showLoading()
                } else {
                    self?.dismissLoading()
                }
            }.store(in: &cancelSet)
        
        viewModel.showWebViewPassthroughSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] (title, url, isInside) in
                guard let self else { return }
                if isInside {
                    let vc = WebViewViewController(naviTitle: title,
                                                   url: url,
                                                   viewModel: AddCertificateWebViewViewModel(parseLinkManager: viewModel.parseLinkManager),
                                                   isNeedNavigationBar: true,
                                                   isShowUrlOnTitle: false)
                    navigationController?.pushViewController(vc, animated: true)
                } else {
                    let content = String(format: NSLocalizedString("OpenURLReminder", comment: ""), title)
                    askAlert(title: NSLocalizedString("ReminderMessage", comment: "提醒訊息"),
                             content: content) { isAgree in
                        if isAgree,
                           let openURL = URL(string: url) {
                            UIApplication.shared.open(openURL)
                        }
                    }
                }
            }.store(in: &cancelSet)
        
        viewModel.verifiableCredentialSuccessSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] (applyVC, orgName, cardImageData, displayName, verifyResult, isShowFullScreen) in
                guard let self else { return }
                if isShowFullScreen {
                    presentFullAddingVerifiableCredentialInfo(applyVCData: applyVC,
                                                              orgName: orgName,
                                                              cardImageData: cardImageData,
                                                              displayName: displayName,
                                                              verifiableCredentailResult: verifyResult)
                } else {
                    presentAddingVerifiableCredentialInfo(applyVCData: applyVC,
                                                          orgName: orgName,
                                                          cardImageData: cardImageData,
                                                          displayName: displayName,
                                                          verifiableCredentailResult: verifyResult)
                }
            }.store(in: &cancelSet)
        
        viewModel.verifiablePresentaionSuccessSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] (verifyResp, parseVPData, customFields, resultPageType) in
                guard let self else { return }
                pushToVerifiablePresentation(verifyResp: verifyResp,
                                             parseVPData: parseVPData,
                                             customFields: customFields,
                                             resultPageType: resultPageType)
            }.store(in: &cancelSet)
        
        viewModel.showScanVCPassthroughSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                guard let self else { return }
                pushToScan()
            }.store(in: &cancelSet)
        
        viewModel.showAppSettingAlertPassthroughSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                guard let self else { return }
                showAppSettingAlert()
            }.store(in: &cancelSet)
        
        viewModel.regeneratePassthroughSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                guard let self,
                      let currentNaviVC = self.selectedViewController as? CustomNavigationController,
                      let currentVC = currentNaviVC.viewControllers.first as? ShowCertificatesViewController else { return }
                currentVC.regenerateVP05()
            }.store(in: &cancelSet)
        
        // 呼叫 scanVC viewWillAppear
        viewModel.parseQRCodeErrorSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] message in
                guard let self else { return }
                showAlert(content: message) {
                    if let scanVC = self.navigationController?.topViewController as? ScanViewController {
                        scanVC.viewWillAppear(false)
                    }
                }
            }.store(in: &cancelSet)
        
        viewModel.verifiableCredentialErrorSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] (message, isNeedPresentFullView) in
                guard let self else { return }
                if isNeedPresentFullView {
                    // 開啟滿版失敗頁
                    let vc = AddCertificateFailViewController(nibName: "AddCertificateFailViewController", bundle: nil)
                    vc.modalPresentationStyle = .fullScreen
                    present(vc, animated: true)
                } else {
                    showAlert(content: message) {
                        if let scanVC = self.navigationController?.topViewController as? ScanViewController {
                            scanVC.viewWillAppear(false)
                        }
                    }
                }
            }.store(in: &cancelSet)
    }
    
    private func setupTabBar() {
        tabBar.backgroundColor = .white
        tabBar.layer.cornerRadius = 28
        tabBar.layer.maskedCorners = [.layerMinXMinYCorner, .layerMaxXMinYCorner]
        tabBar.layer.masksToBounds = false
        tabBar.layer.shadowColor = UIColor._868686.cgColor
        tabBar.layer.shadowOpacity = 0.25
        self.tabBar.layer.shadowPath = UIBezierPath(roundedRect: self.tabBar.bounds, cornerRadius: 20.0).cgPath
        tabBar.tintColor = UIColor._4_E_61_A_7
        tabBar.itemPositioning = .centered
    }
    
    func showLoading(style: LoadingViewController.LoadingStyle = .normal) {
        loadingVC.modalPresentationStyle = .overFullScreen
        loadingVC.loadingStyle = style
        present(loadingVC, animated: false)
    }
    
    func dismissLoading() {
        loadingVC.dismiss(animated: false)
    }
    
    func showAlert(title: String = NSLocalizedString("MessageTitle", comment: ""), content: String, complete: (()-> Void)? = nil) {
        let alert = CustomAlertViewController(title: title, content: content, leftButtonTitle: nil, leftButtonCompletion: nil,
                                              rightButtonTitle: NSLocalizedString("Confirm", comment: ""),
                                              rightButtonCompletion: {
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
    
    /// 開啟前往設定頁跳窗
    private func showAppSettingAlert() {
        let alert = CustomAlertViewController(title: NSLocalizedString("PromptMessage", comment: ""),
                                              content: NSLocalizedString("NoCameraPermission", comment: ""),
                                              leftButtonTitle: NSLocalizedString("Cancel", comment: "取消"),
                                              leftButtonCompletion: nil,
                                              rightButtonTitle: NSLocalizedString("Confirm", comment: "確認")) {
            self.openAppSettings()
        }
        present(alert, animated: true)
    }
    
    /// 前往設定頁
    private func openAppSettings() {
        if let appSettingsURL = URL(string: UIApplication.openSettingsURLString),
           UIApplication.shared.canOpenURL(appSettingsURL) {
            UIApplication.shared.open(appSettingsURL)
        }
    }
}

extension CustomTabBarController: UITabBarControllerDelegate {
    func tabBarController(_ tabBarController: UITabBarController, shouldSelect viewController: UIViewController) -> Bool {
        guard let nav = viewController as? CustomNavigationController, let rootVC = nav.viewControllers.first else {
            return true
        }
        nav.popToRootViewController(animated: false)
        switch rootVC {
        case is DummyViewController:
            viewModel.checkCameraPermissions()
            return false
        default:
            return true
        }
    }
}

// MARK: 頁面導轉
extension CustomTabBarController {
    /// 開啟掃描頁
    private func pushToScan() {
        let viewModel = ScanViewModel(respository: viewModel.repository, parseLinkManager: viewModel.parseLinkManager)
        let scanVC = ScanViewController(viewModel: viewModel)
        navigationController?.pushViewController(scanVC, animated: true)
    }
    
    /// 顯示VP自主揭露驗證頁面
    /// - Parameters:
    ///   - verifyResp: 驗證組成資料
    ///   - parseVPData: 原始VP資料
    private func pushToVerifiablePresentation(verifyResp:VerifiableData,
                                              parseVPData: ParseVPDataResponse,
                                              customFields: [DwModa201iCustomField],
                                              resultPageType: VerifiablePresentationResult) {
        let wallet = viewModel.repository.wallet
        let respository = AppDelegate.shared.container.resolve(VerifiablePresentationRepository.self,
                                                               arguments: viewModel.repository.wallet.uuid,
                                                               verifyResp,
                                                               parseVPData,
                                                               wallet)!
        
        let viewModel = VerifiablePresentationViewModel(repository: respository,
                                                        userRepository: viewModel.repository,
                                                        biometricVerifyManager: AppDelegate.shared.container.resolve(BiometricVerifyManager.self)!,
                                                        callTabbarProtocol: viewModel,
                                                        resultPageType: resultPageType,
                                                        customFields: customFields)
        let vc = VerifiablePresentationViewController(viewModel: viewModel)
        navigationController?.pushViewController(vc, animated: true)
    }
    
    /// 顯示加入VC頁面
    /// - Parameters:
    ///   - applyVCData: VC驗證資料
    ///   - orgName: 組織名稱
    ///   - cardImageData: 卡片背景
    ///   - displayName: 顯示姓名
    ///   - verifiableCredentailResult: 驗證結果
    private func presentAddingVerifiableCredentialInfo(applyVCData: ApplyVCDataResponse,
                                                       orgName: String,
                                                       cardImageData: Data?,
                                                       displayName: String,
                                                       verifiableCredentailResult: VerifiableCredentailResult?) {
        let vc = AddingVerifiableCredentialInfoViewController(applyVCData: applyVCData,
                                                              orgName: orgName,
                                                              cardImageData: cardImageData,
                                                              displayName: displayName,
                                                              verifiableCredentailResult: verifiableCredentailResult)
        present(vc, animated: true)
    }
    
    /// 顯示加入VC滿版頁面
    /// - Parameters:
    ///   - applyVCData: VC驗證資料
    ///   - orgName: 組織名稱
    ///   - cardImageData: 卡片背景
    ///   - displayName: 顯示姓名
    ///   - verifiableCredentailResult: 驗證結果
    private func presentFullAddingVerifiableCredentialInfo(applyVCData: ApplyVCDataResponse,
                                                           orgName: String,
                                                           cardImageData: Data?,
                                                           displayName: String,
                                                           verifiableCredentailResult: VerifiableCredentailResult?) {
        let vc = AddCertificateSuccessViewController(applyVCData: applyVCData,
                                                     orgName: orgName,
                                                     cardImageData: cardImageData,
                                                     displayName: displayName,
                                                     verifiableCredentailResult: verifiableCredentailResult)
        vc.modalPresentationStyle = .fullScreen
        present(vc, animated: true)
    }
}
