//
//  CreateWalletNormalLoginViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class CreateWalletWithPinCodeViewController: BaseViewController {
    /// 背景
    @IBOutlet weak var contentBackgroundView: UIView!
    /// 備註的點
    @IBOutlet weak var hintView: UIView!
    /// 密碼設定的點
    @IBOutlet weak var settingHintView: UIView!
    /// 密碼設定
    @IBOutlet weak var settingTextField: CustomTextField!
    /// 密碼設定眼睛按鈕
    @IBOutlet weak var settingEyeButton: UIButton!
    /// 密碼設定提示文字
    @IBOutlet weak var settingHintLabel: UILabel!
    /// 再次密碼設定的點
    @IBOutlet weak var settingAgainHintView: UIView!
    /// 再次輸入皮夾密碼
    @IBOutlet weak var settingAgainTextField: CustomTextField!
    /// 再次密碼設定眼睛按鈕
    @IBOutlet weak var settingAgainEyeButton: UIButton!
    /// 確認按鈕
    @IBOutlet weak var confirmButton: UIButton!
    /// 取消按鈕
    @IBOutlet weak var cancelButton: UIButton!
    /// 上方的content
    @IBOutlet weak var contentLabel: UILabel!
    
    @IBOutlet weak var subTitleLabel: UILabel!
    
    @IBOutlet weak var textFieldTitleLabel: UILabel!
    
    @IBOutlet weak var confirmTextFieldTitleLabel: UILabel!
    
    let viewModel: SetWalletPinCodeProtocol
    var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: SetWalletPinCodeProtocol) {
        self.viewModel = viewModel
        super.init(nibName: "CreateWalletWithPinCodeViewController", bundle: nil)
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
        title = viewModel.title
        contentLabel.text = viewModel.content
        
        // 點
        hintView.layer.cornerRadius = hintView.frame.height * 0.5
        settingHintView.layer.cornerRadius = settingHintView.frame.height * 0.5
        settingAgainHintView.layer.cornerRadius = settingAgainHintView.frame.height * 0.5
        
        // 背景匡
        contentBackgroundView.layer.cornerRadius = 12
        
        // 輸入框
        settingTextField.delegate = self
        settingTextField.tag = 0
        settingAgainTextField.delegate = self
        settingAgainTextField.tag = 1
        
        // 眼睛按鈕設定
        let openEyesImage = UIImage(named: ImageName.CreateWallet.OpenEyes.rawValue)
        let closeEyesImage = UIImage(named: ImageName.CreateWallet.CloseEyes.rawValue)
        
        settingEyeButton.setImage(openEyesImage, for: .normal)
        settingEyeButton.setImage(closeEyesImage, for: .selected)
        settingAgainEyeButton.setImage(openEyesImage, for: .normal)
        settingAgainEyeButton.setImage(closeEyesImage, for: .selected)
        
        // 確認按鈕
        confirmButton.layer.cornerRadius = confirmButton.frame.height * 0.5
        updateConfirmButton(isEnabled: false)
        
        subTitleLabel.text = viewModel.subTitle
        textFieldTitleLabel.text = viewModel.textFieldTitle
        confirmTextFieldTitleLabel.text = viewModel.confirmTextFieldTitle
    }
    
    /// 畫面綁定
    private func initBinding() {
        viewModel.isLoadingSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] isShowLoading in
                guard let self = self else { return }
                
                if isShowLoading {
                    self.showLoading()
                } else {
                    self.dismissLoading()
                }
            }.store(in: &cancelSet)
        
        viewModel.inputErrorSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in
                guard let self = self else {
                    return
                }
                
                switch result{
                    case .pwdLengthError:
                        self.settingTextField.layer.borderColor = UIColor.EF_2_A_1_C.cgColor
                        self.settingHintLabel.textColor = .EF_2_A_1_C
                    case .pwdNotSameError:
                        self.settingTextField.layer.borderColor = UIColor.D_6_D_6_D_6.cgColor
                        self.settingHintLabel.textColor = ._62676_D
                        
                        showAlert(title: NSLocalizedString("PromptMessage", comment: ""), content: NSLocalizedString("PinCodeNotSame", comment: ""))
                }
            }.store(in: &cancelSet)
        
        viewModel.finishSetupPinCodeSubject
            .receive(on: DispatchQueue.main)
            .sink { _ in
                let loginRepository = AppDelegate.shared.container.resolve(LoginRepository.self)!
                let loginViewModel = LoginViewModel(loginRepository: loginRepository, loginAction: .normal)
                let loginVC = LoginViewController(viewModel: loginViewModel)
                let navi = CustomNavigationController(rootViewController: loginVC)
                
                UIApplication.shared.windows.first?.rootViewController = navi
                UIApplication.shared.windows.first?.makeKeyAndVisible()
            }.store(in: &cancelSet)
        
        viewModel.failSetupPinCodeSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] message in
                guard let self = self else { return }
                self.showAlert(content: message)
            }.store(in: &cancelSet)
    }
    
    /// 更新確認按鈕狀態
    /// - Parameter isEnabled: 可以使用
    private func updateConfirmButton(isEnabled: Bool) {
        confirmButton.backgroundColor = isEnabled ? ._4_E_61_A_7 : .DEDEDE
        confirmButton.setTitleColor(isEnabled ? .white : .AAAAAA, for: .normal)
        confirmButton.isEnabled = isEnabled
    }
    
    @IBAction func onClickSetting(_ sender: UIButton) {
        sender.isSelected.toggle()
        settingTextField.isSecureTextEntry = sender.isSelected
    }
    
    @IBAction func onClickSettingAgain(_ sender: UIButton) {
        sender.isSelected.toggle()
        settingAgainTextField.isSecureTextEntry = sender.isSelected
    }
    
    /**確認*/
    @IBAction func onClickConfirm(_ sender: Any) {
        viewModel.setupWalletPinCode(pinCode: settingTextField.text ?? "", confirmPinCode: settingAgainTextField.text ?? "")
    }
    
    @IBAction func onClickCancel(_ sender: Any) {
        switch viewModel.type {
            case .create:
                navigationController?.popToRootViewController(animated: true)
            case .update:
                navigationController?.popViewController(animated: true)
        }
    }
}

extension CreateWalletWithPinCodeViewController: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        updateConfirmButton(isEnabled: true)
        let currentText = textField.text ?? ""
        let updatedText = (currentText as NSString).replacingCharacters(in: range, with: string)
        
        if (textField.tag == settingTextField.tag && updatedText.isEmpty && settingAgainTextField.text?.isEmpty ?? true) ||
            (textField.tag == settingAgainTextField.tag && updatedText.isEmpty && settingTextField.text?.isEmpty ?? true) {
            updateConfirmButton(isEnabled: false)
        }
        
        return updatedText.count <= 8
    }
}
