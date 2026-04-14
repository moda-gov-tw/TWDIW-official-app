//
//  PinCodeViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class PinCodeLoginController: BaseViewController {
    /// 背景
    @IBOutlet weak var keyboardBackgroundView: UIView!
    /// 密碼
    @IBOutlet weak var enterTextField: CustomTextField!
    /// 密碼眼睛按鈕
    @IBOutlet weak var enterEyeButton: UIButton!
    /// 登入按鈕
    @IBOutlet weak var loginButton: UIButton!
    /// PinCode提示
    @IBOutlet weak var pinCodeHintLabel: UILabel!
    
    let viewModel: PinCodeLoginViewModel
    private let pinCodeErrorLimit: Int = 5//密碼錯誤上限
    private let pinCodeEnterLimit: Int = 8//密碼輸入長度上限
    
    private var pinCodeErrorCount: Int = 0//密碼錯誤次數
    var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: PinCodeLoginViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "PinCodeLoginController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initView()
        initBinding()
    }
    
    /// 畫面設定
    private func initView() {
        // 背景
        keyboardBackgroundView.layer.cornerRadius = 24.0
        keyboardBackgroundView.layer.maskedCorners = [.layerMinXMinYCorner, .layerMaxXMinYCorner]
        keyboardBackgroundView.clipsToBounds = true
        
        pinCodeHintLabel.text = NSLocalizedString("pinCodeErrorHint", comment: "皮夾密碼錯誤，重新輸入皮夾密碼")
        
        // 眼睛按鈕設定
        let openEyesImage = UIImage(named: ImageName.CreateWallet.OpenEyes.rawValue)
        let closeEyesImage = UIImage(named: ImageName.CreateWallet.CloseEyes.rawValue)
        enterEyeButton.setImage(closeEyesImage, for: .normal)
        enterEyeButton.setImage(openEyesImage, for: .selected)
        
        // 登入按鈕
        loginButton.layer.cornerRadius = loginButton.frame.height * 0.5
        
        isHideNavigationBar = true
    }
    
    /// 畫面綁定
    private func initBinding() {
        viewModel.loginFailSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] errorMessage in
                guard let self = self else { return }
                pinCodeErrorCount += 1
                
                if let errorMessage {
                    self.showAlert(content: errorMessage)
                } else if pinCodeErrorCount >= pinCodeErrorLimit{
                    self.showAlert(title: NSLocalizedString("PinCodeError", comment: ""),
                                   content: NSLocalizedString("PinCodeErrorOverFive", comment: ""))
                }
                updateErrorHint(isHidden: false)
            }.store(in: &cancelSet)
        
        viewModel.loginSuccessSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] wallet in
                guard let self else { return }
                pinCodeErrorCount = 0//錯誤次數歸0
                
                /*紀錄本次登入的皮夾*/
                UserDefaultManager.shared.setObject(value: wallet.uuid, key: .DefaultLoginWallet)
                
                let container = AppDelegate.shared.container
                let userRepository = container.resolve(UserRepository.self, argument: wallet)!
                let parseLinkManager = container.resolve(ParseLinkManager.self)!
                let imageLoadManager = container.resolve(ImageLoadManager.self)!
                
                /*自動登出計時*/
                AppDelegate.shared.countdownTimer = CountdownTimer(repository: userRepository)
                AppDelegate.shared.countdownTimer?.startCountdown()
                AppDelegate.shared.autoLogoutTimer()
                
                let tabBarViewModel = CustomTabBarViewModel(respository: userRepository, parseLinkManager: parseLinkManager)
                let tabBarVC = CustomTabBarController(viewModel: tabBarViewModel)
                let naviTabBarVC = CustomNavigationController(rootViewController: tabBarVC)
                
                /// 我的憑證
                let cardOverViewVC = CardOverviewViewController(viewModel: CardOverViewViewModel(repository: userRepository, callTabbarProtocol: tabBarViewModel))
                let cardOverViewNavigationViewController = CustomNavigationController(rootViewController: cardOverViewVC, isTransparent: true)
                cardOverViewVC.tabBarItem = UITabBarItem(title: "管理憑證", image: UIImage.tabBarIconCardOverview, tag: 0)
                
                /// 加入憑證
                let addCertificateVC = AddCertificateViewController(viewModel: AddCertificateViewModel(repository: userRepository, callTabbarProtocol: tabBarViewModel, parseLinkManager: parseLinkManager, imageLoadManager: imageLoadManager))
                let addCertificateNavigationViewController = CustomNavigationController(rootViewController: addCertificateVC)
                addCertificateVC.tabBarItem = UITabBarItem(title: "加入憑證", image: UIImage.addCertificate, tag: 1)
                
                /// 出示憑證
                let showCertificatesVC = ShowCertificatesViewController(viewModel: ShowCertificatesViewModel(repository: userRepository, parseLinkManager: parseLinkManager))
                let showCertificatesNavigationViewController = CustomNavigationController(rootViewController: showCertificatesVC)
                showCertificatesVC.tabBarItem = UITabBarItem(title: "出示憑證", image: UIImage.showCertificates, tag: 2)
                
                /// 掃描
                let dummyVC = DummyViewController()
                let dummyNavigationViewController = CustomNavigationController(rootViewController: dummyVC)
                dummyVC.tabBarItem = UITabBarItem(title: "掃描", image: UIImage.tabBarIconScan, tag: 3)
                
                /// 個人
                let settingVC = SettingViewController(viewModel: SettingViewModel(repository: userRepository))
                let settingNavigationViewController = CustomNavigationController(rootViewController: settingVC)
                settingVC.tabBarItem = UITabBarItem(title: NSLocalizedString("Personal", comment: ""), image: UIImage.tabBarIconSetting, tag: 4)
                settingNavigationViewController.setupNavigationBarAppearance(isTransparent: true)
                tabBarVC.viewControllers = [cardOverViewNavigationViewController,
                                            addCertificateNavigationViewController,
                                            showCertificatesNavigationViewController,
                                            dummyNavigationViewController,
                                            settingNavigationViewController]

                let window = self.view.window
                window?.rootViewController = naviTabBarVC
                window?.makeKeyAndVisible()
            }.store(in: &cancelSet)
    }
    
    /// 更新錯誤提示
    /// - Parameter isHidden: 隱藏錯誤提示
    private func updateErrorHint(isHidden: Bool) {
        let cgColor = isHidden ? UIColor.DEDEDE.cgColor : UIColor.EF_2_A_1_C.cgColor
        enterTextField.layer.borderColor = cgColor
        pinCodeHintLabel.isHidden = isHidden
    }
    
    private func updateLoginButton(_ enable: Bool) {
        loginButton.isEnabled = enable
        loginButton.backgroundColor = enable ? ._4_E_61_A_7 : .DEDEDE
    }
    
    @IBAction func onClickPinButton(_ sender: PinButton) {
        updateErrorHint(isHidden: true)
        let bBackSpace = sender.clickValue == .backSpace
        
        guard let strWalletCode = enterTextField.text, !strWalletCode.isEmpty else {
            if !bBackSpace {
                enterTextField.text = sender.clickValue.rawValue
                updateLoginButton(true)
            }
            
            return
        }
        
        /*刪除最後一個字串*/
        if bBackSpace{
            enterTextField.text = String(strWalletCode.dropLast())
        }
        /*新增字串，若長度未超過限制，則新增*/
        else if enterTextField.text?.count ?? 0 < pinCodeEnterLimit{
            enterTextField.text = (strWalletCode + sender.clickValue.rawValue)
        }
        
        updateLoginButton(!(enterTextField.text?.isEmpty ?? true))
    }
    
    @IBAction func onClickEyeButton(_ sender: UIButton) {
        sender.isSelected.toggle()
        enterTextField.isSecureTextEntry = !sender.isSelected
    }
    
    @IBAction func onClickLogin(_ sender: Any) {
        viewModel.doLogin(pinCode: enterTextField.text ?? "")
    }
    
    @IBAction func onClickCancel(_ sender: Any) {
        navigationController?.popToRootViewController(animated: true)
    }
}
