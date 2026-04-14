//
//  LaunchViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class LaunchViewController: BaseViewController {
    
    let viewModel: LaunchViewModel
    private var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: LaunchViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "LaunchViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initView()
        initBinding()
        viewModel.checkUserDevice()
    }
    
    private func initView() {
        isHideNavigationBar = true
    }

    private func initBinding() {
        viewModel.unsuportedPassthroughSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] isHaveSoc in
                guard let self else { return }
                if isHaveSoc {
                    viewModel.setupDatabaseAndRootViewController()
                } else {
                    let alert = CustomAlertViewController(title: "安全警告", content: "基於安全考量，數位憑證皮夾需要使用裝置的硬體晶片進行安全加解密，您的裝置因不支持所以無法使用。", leftButtonTitle: nil, leftButtonCompletion: nil, rightButtonTitle: "確認") {
                        exit(0)
                    }
                    present(alert, animated: true)
                }
            }.store(in: &cancelSet)
        
        viewModel.setupRootViewControllerPassthroughSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] type in
                guard let self else { return }
                switch type {
                case .Login:
                    let loginRepository = AppDelegate.shared.container.resolve(LoginRepository.self)!
                    let loginViewModel = LoginViewModel(loginRepository: loginRepository,
                                                        loginAction: .normal)
                    let loginVC = LoginViewController(viewModel: loginViewModel)
                    let navi = CustomNavigationController(rootViewController: loginVC)
                    UIApplication.shared.windows.first?.rootViewController = navi
                    UIApplication.shared.windows.first?.makeKeyAndVisible()
                    break
                case .Register:
                    let welcomeVC =  WelcomeViewController(nibName: "WelcomeViewController", bundle: nil)
                    let navi = CustomNavigationController(rootViewController: welcomeVC)
                    UIApplication.shared.windows.first?.rootViewController = navi
                    UIApplication.shared.windows.first?.makeKeyAndVisible()
                    break
                }
            }.store(in: &cancelSet)
        
        viewModel.databaseConfigureErrorPassthroughSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self else { return }
                let message: String = "[\(SDKResultCode.AppError.rawValue)]：\(NSLocalizedString("AppError", comment: ""))"
                showWarningAlert(content: message) {
                    exit(0)
                }
            }.store(in: &cancelSet)
    }
}
