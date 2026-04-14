//
//  LoginSettingViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class LoginSettingViewController: BaseViewController {

    @IBOutlet weak var startSettingButton: UIButton!
    
    let viewModel: LoginSettingViewModel
    var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: LoginSettingViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "LoginSettingViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        initView()
        initBinding()
    }

    func initView() {
        title = NSLocalizedString("LoginSetting", comment: "")
        startSettingButton.layer.cornerRadius = startSettingButton.frame.height / 2
    }
    
    func initBinding() {
        viewModel.finishCreateWalletSubject
            .receive(on: DispatchQueue.main)
            .sink { _ in
                let loginRepository = AppDelegate.shared.container.resolve(LoginRepository.self)!
                let loginViewModel = LoginViewModel(loginRepository: loginRepository, loginAction: .normal)
                let loginVC = LoginViewController(viewModel: loginViewModel)
                let navi = CustomNavigationController(rootViewController: loginVC)
                UIApplication.shared.windows.first?.rootViewController = navi
                UIApplication.shared.windows.first?.makeKeyAndVisible()
            }.store(in: &cancelSet)
        
        viewModel.createWalletWithPinCodeSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                let createWalletWithPinCodeViewModel = CreateWalletWithPinCodeViewModel(title: NSLocalizedString("LoginSetting", comment: ""),
                                                                                        content: NSLocalizedString("CreateWalletMessage", comment: ""),
                                                                                        subTitle: NSLocalizedString("WalletPWSetting", comment: ""),
                                                                                        textFieldTitle: NSLocalizedString("WalletPW", comment: ""),
                                                                                        confirmTextFieldTitle: NSLocalizedString("WalletPWAgain", comment: ""),
                                                                                        createWalletRepository: viewModel.createWalletRepository)
                let createWalletWithPinCodeVC = CreateWalletWithPinCodeViewController(viewModel: createWalletWithPinCodeViewModel)
                self.navigationController?.pushViewController(createWalletWithPinCodeVC, animated: true)
            }.store(in: &cancelSet)
        
        viewModel.$isLoading
            .receive(on: DispatchQueue.main)
            .sink { [weak self] isShowLoading in
                guard let self = self else { return }
                if isShowLoading {
                    self.showLoading()
                } else {
                    self.dismissLoading()
                }
            }.store(in: &cancelSet)
    }
    
    /**開始設定*/
    @IBAction func onClickStratSetting(_ sender: Any) {
        viewModel.authenticateUser()
    }
    
    @IBAction func onClickCancel(_ sender: Any) {
        if let navigationController = self.navigationController {
            for vc in navigationController.viewControllers {
                if vc is WelcomeViewController {
                    navigationController.popToViewController(vc, animated: true)
                }
            }
        }
    }
}
