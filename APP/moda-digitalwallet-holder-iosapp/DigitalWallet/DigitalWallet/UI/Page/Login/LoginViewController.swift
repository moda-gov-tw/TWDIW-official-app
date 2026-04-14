//
//  SelectWalletViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class LoginViewController: BaseViewController {
    /// 背景
    @IBOutlet weak var contentBackgroundView: UIView!
    /// 下拉選單背景
    @IBOutlet weak var pickerBackgroundView: UIView!
    /// 皮夾名稱
    @IBOutlet weak var walletNameLabel: UILabel!
    /// 下一步按鈕
    @IBOutlet weak var nextStepButton: UIButton!
    
    let pickerView = CustomPickerViewController()
    
    let viewModel: LoginViewModel
    
    var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: LoginViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "LoginViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initView()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        initBinding()
        viewModel.getWallets()
        AppDelegate.shared.countdownTimer?.stopTimer()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        if viewModel.loginAction == .reLogin {
            onClickNextStep(UIButton())
        }
        
        viewModel.loginAction = .normal
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        
        /*登入後解除訂閱，避免登出後Cross App觸發時生命週期出錯*/
        cancelSet.removeAll()
    }
    
    /// 畫面設定
    private func initView() {
        // 背景
        contentBackgroundView.layer.cornerRadius = 24.0
        
        // 選擇皮夾按鈕背景
        pickerBackgroundView.layer.borderWidth = 1.0
        pickerBackgroundView.layer.borderColor = UIColor.DEDEDE.cgColor
        pickerBackgroundView.layer.cornerRadius = 12.0
        
        // 下一步按鈕
        nextStepButton.layer.cornerRadius = nextStepButton.frame.height * 0.5
        
        isHideNavigationBar = true
        
        // pickerView設定
        pickerView.modalPresentationStyle = .overFullScreen
        pickerView.modalTransitionStyle = .crossDissolve
    }

    /// 畫面綁定
    private func initBinding() {
        viewModel.$pickerItems
            .receive(on: DispatchQueue.main)
            .sink { [weak self] items in
                guard let self = self else {
                    return
                }
                
                /*預設第一筆*/
                var item: CustomPickerItem? = items.first
                
                /*有上次登入的皮夾，預設上次*/
                if !items.isEmpty{
                    let wallets: Array<MyWallet> = viewModel.loginRepository.getWallets()
                    let uuid: UUID? = UserDefaultManager.shared.getObject(key: .DefaultLoginWallet, type: UUID.self)
                    
                    if let wallet = wallets.first(where: { $0.uuid == uuid }) {
                        if let selectPickerItem = items.first(where: { $0.name == wallet.name }) {
                            item = selectPickerItem
                        }
                    }
                }
                
                pickerView.setupPicker(list: items, selectName: item?.name, targetView: pickerBackgroundView)
                viewModel.selectedItem = item
            }.store(in: &cancelSet)
        
        viewModel.$selectedItem
            .dropFirst()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] item in
                guard let self = self else {
                    return
                }
                
                if let item {
                    self.walletNameLabel.text = item.name
                    self.pickerView.setupSelecedName(selectName: item.name)
                } else {
                    self.walletNameLabel.text = ""
                }
            }.store(in: &cancelSet)
        
        viewModel.pinCodeLoginSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] wallet in
                guard let self = self else {
                    return
                }
                
                let pinCodeLoginViewModel = PinCodeLoginViewModel(loginRepository: viewModel.loginRepository, wallet: wallet)
                let pinCodeLoginVC = PinCodeLoginController(viewModel: pinCodeLoginViewModel)
                
                self.navigationController?.pushViewController(pinCodeLoginVC, animated: true)
            }.store(in: &cancelSet)
        
        /*登入皮夾時，只有用生物辨識註冊，沒有pinCode卻用pinCode登入*/
        viewModel.noPinCodeSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] wallet in
                guard let self = self else {
                    return
                }
                
                self.showWarningAlert(title: NSLocalizedString("UnableToLogin", comment: ""),
                                      content: NSLocalizedString("WalletNoPinCodeMessage", comment: "")){
                    
                }
        }.store(in: &cancelSet)
        
        viewModel.loginSuccesSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] wallet in
                guard let self else {
                    return
                }
                
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
                addCertificateNavigationViewController.setupNavigationBarAppearance(isTransparent: true)
                addCertificateVC.tabBarItem = UITabBarItem(title: "加入憑證", image: UIImage.addCertificate, tag: 1)
                
                /// 出示憑證
                let showCertificatesVC = ShowCertificatesViewController(viewModel: ShowCertificatesViewModel(repository: userRepository, parseLinkManager: parseLinkManager))
                let showCertificatesNavigationViewController = CustomNavigationController(rootViewController: showCertificatesVC)
                showCertificatesNavigationViewController.setupNavigationBarAppearance(isTransparent: true)
                showCertificatesVC.tabBarItem = UITabBarItem(title: NSLocalizedString("ShowCertificates", comment: ""), image: UIImage.showCertificates, tag: 2)
                
                /// 掃描
                let dummyVC = DummyViewController()
                let dummyNavigationViewController = CustomNavigationController(rootViewController: dummyVC)
                dummyVC.tabBarItem = UITabBarItem(title: "掃描", image: UIImage.tabBarIconScan, tag: 3)
                
                /// 個人
                let settingVC = SettingViewController(viewModel: SettingViewModel(repository: userRepository))
                let settingNavigationViewController = CustomNavigationController(rootViewController: settingVC)
                settingNavigationViewController.setupNavigationBarAppearance(isTransparent: true)
                settingVC.tabBarItem = UITabBarItem(title: NSLocalizedString("Personal", comment: ""), image: UIImage.tabBarIconSetting, tag: 4)

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
    
    /// 點擊下一步
    @IBAction func onClickNextStep(_ sender: UIButton) {
        // 先停用按鈕，避免連點
        sender.isEnabled = false
        viewModel.doLogin()
        // 1.5 秒後再啟用
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
            sender.isEnabled = true
        }
    }
}
