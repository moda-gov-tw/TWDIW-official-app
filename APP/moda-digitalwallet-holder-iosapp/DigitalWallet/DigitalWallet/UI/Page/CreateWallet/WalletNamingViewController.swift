//
//  CreateWalletViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class WalletNamingViewController: BaseViewController {
    @IBOutlet weak var walletNameTextField: CustomTextField!
    @IBOutlet weak var walletNameHintLabel: UILabel!
    @IBOutlet weak var createWalletButton: UIButton!
    @IBOutlet weak var contentBackgroundView: UIView!
    @IBOutlet weak var hintView: UIView!
    @IBOutlet weak var remarkHintView: UIView!
    
    let viewModel: WalletNamingViewModel
    private let maxLength = 8 //設定最大輸入字數
    
    private var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: WalletNamingViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "WalletNamingViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initView()
        initBinding()
        
        walletNameTextField.addTarget(self, action: #selector(textFieldDidChange), for: .editingChanged)
    }

    private func initView() {
        createWalletButton.layer.cornerRadius = createWalletButton.frame.height * 0.5
        contentBackgroundView.layer.cornerRadius = 12
        hintView.layer.cornerRadius = hintView.frame.height / 2
        remarkHintView.layer.cornerRadius = remarkHintView.frame.height / 2
        walletNameTextField.autocorrectionType = .no
        walletNameTextField.delegate = self
        
        title = viewModel.walletNamingTitle()
        walletNameTextField.text = viewModel.getDefaultWalletName()
        
        walletNameHintLabel.text = NSLocalizedString("WalletNameErrorHint", comment: "")
    }
    
    private func initBinding() {
        viewModel.pushToSettingLoginSubject
            .receive(on: DispatchQueue.main)
            .sink { repository in
                let settingViewModel = LoginSettingViewModel(repository: repository, biometricVerifyManager: AppDelegate.shared.container.resolve(BiometricVerifyManager.self)!)
                let settingVC = LoginSettingViewController(viewModel: settingViewModel)
                self.navigationController?.pushViewController(settingVC, animated: true)
            }.store(in: &cancelSet)
        
        viewModel.hasSameWalletNameSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                self.showAlert(title: NSLocalizedString("WalletnameAlreadyUse", comment: ""),
                               content: NSLocalizedString("WalletNameAlreadyUsePleaseChangeTheName", comment: ""))
            }.store(in: &cancelSet)
        
        viewModel.walletNameErrorSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                walletNameTextField.layer.borderColor = UIColor.EF_2_A_1_C.cgColor
                walletNameHintLabel.textColor = .EF_2_A_1_C
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
        
        viewModel.createWalletWithPinCodeSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                let createWalletWithPinCodeViewModel = CreateWalletWithPinCodeViewModel(title: NSLocalizedString("WalletSetting", comment: ""),
                                                                                        content: NSLocalizedString("CreateWalletMessage", comment: ""),
                                                                                        subTitle: NSLocalizedString("WalletPWSetting", comment: ""),
                                                                                        textFieldTitle: NSLocalizedString("WalletPW", comment: ""),
                                                                                        confirmTextFieldTitle: NSLocalizedString("WalletPWAgain", comment: ""),
                                                                                        createWalletRepository: viewModel.repository)
                let createWalletWithPinCodeVC = CreateWalletWithPinCodeViewController(viewModel: createWalletWithPinCodeViewModel)
                self.navigationController?.pushViewController(createWalletWithPinCodeVC, animated: true)
            }.store(in: &cancelSet)
        
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
    }
    
    @IBAction func onClickCreate(_ sender: Any) {
        viewModel.createWallet(walletName: walletNameTextField.text)
    }
    
    @IBAction func onClickCancel(_ sender: Any) {
        self.navigationController?.popToRootViewController(animated: true)
    }
}

extension WalletNamingViewController: UITextFieldDelegate {
    @objc func textFieldDidChange(_ textField: UITextField) {
        /*如果正在組字（如注音輸入中），則不執行長度限制*/
        if let markedTextRange = textField.markedTextRange, textField.position(from: markedTextRange.start, offset: 0) != nil {
            return
        }

        /*取得已輸入的文字*/
        if let text = textField.text, text.count > maxLength {
            /*截斷字串，避免超過最大長度*/
            textField.text = String(text.prefix(maxLength))
        }
    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        walletNameTextField.layer.borderColor = UIColor.DEDEDE.cgColor
        walletNameHintLabel.textColor = ._62676_D
    }
}
