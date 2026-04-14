//
//  DeleteWalletSuccessViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class DeleteWalletSuccessViewController: UIViewController {

    @IBOutlet weak var contentBackgroundView: UIView!
    @IBOutlet weak var doneButton: UIButton!
    @IBOutlet weak var deleteSuccessLabel: UILabel!
    
    private let viewModel: DeleteWalletSuccessViewModel
    var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: DeleteWalletSuccessViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "DeleteWalletSuccessViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        initView()
        initBinding()
    }
    
    private func initView() {
        contentBackgroundView.layer.cornerRadius = 24.0
        contentBackgroundView.layer.maskedCorners = [.layerMinXMinYCorner, .layerMaxXMinYCorner]
        doneButton.layer.cornerRadius = doneButton.frame.height / 2
        deleteSuccessLabel.text = String(format: NSLocalizedString("DeleteWalletSuccess", comment: "成功刪除 "), viewModel.repository.wallet.name)
    }
    
    private func initBinding() {
        viewModel.actionFinishSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] actionType in
                guard let self else { return }
                switch actionType {
                case .toLogin:
                    let loginRepository = AppDelegate.shared.container.resolve(LoginRepository.self)!
                    let loginViewModel = LoginViewModel(loginRepository: loginRepository, loginAction:
                            .normal)
                    let loginVC = LoginViewController(viewModel: loginViewModel)
                    let navi = CustomNavigationController(rootViewController: loginVC)
                    UIApplication.shared.windows.first?.rootViewController = navi
                    UIApplication.shared.windows.first?.makeKeyAndVisible()
                    break
                case .toCreateWalllet:
                    let welcomeVC =  WelcomeViewController(nibName: "WelcomeViewController", bundle: nil)
                    let navi = CustomNavigationController(rootViewController: welcomeVC)
                    UIApplication.shared.windows.first?.rootViewController = navi
                    UIApplication.shared.windows.first?.makeKeyAndVisible()
                    break
                }
            }.store(in: &cancelSet)
    }

    @IBAction func onClickDone(_ sender: Any) {
        viewModel.done()
    }
}
