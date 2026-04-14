//
//  PinCodeViewController.swift
//  DigitalWallet
//  手機驗證
//

import UIKit
import Combine

class EnterPinCodeViewController: BaseViewController {
    /// 背景
    @IBOutlet weak var contentBackgroundView: UIView!
    /// 密碼
    @IBOutlet weak var enterTextField: CustomTextField!
    /// 密碼眼睛按鈕
    @IBOutlet weak var enterEyeButton: UIButton!
    /// 登入按鈕
    @IBOutlet weak var loginButton: UIButton!
    /// PinCode提示
    @IBOutlet weak var pinCodeHintLabel: UILabel!
    
    private let pinCodeErrorLimit: Int = 5//密碼錯誤上限
    private let pinCodeEnterLimit: Int = 8//密碼輸入上限
    
    private var pinCodeErrorCount: Int = 0//密碼錯誤次數
    let viewModel: EnterPinCodeViewModel
    
    var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: EnterPinCodeViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "EnterPinCodeViewController", bundle: nil)
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
        contentBackgroundView.layer.cornerRadius = 24.0
        contentBackgroundView.layer.maskedCorners = [.layerMinXMinYCorner, .layerMaxXMinYCorner]
        contentBackgroundView.clipsToBounds = true
        
        pinCodeHintLabel.text = NSLocalizedString("pinCodeErrorHint", comment: "皮夾密碼錯誤，重新輸入皮夾密碼")
        
        // 眼睛按鈕設定
        let openEyesImage = UIImage(named: ImageName.CreateWallet.OpenEyes.rawValue)
        let closeEyesImage = UIImage(named: ImageName.CreateWallet.CloseEyes.rawValue)
        enterEyeButton.setImage(closeEyesImage, for: .normal)
        enterEyeButton.setImage(openEyesImage, for: .selected)
        
        // 登入按鈕
        loginButton.layer.cornerRadius = loginButton.frame.height * 0.5
        
        isHideNavigationBar = false
        title = viewModel.title ?? NSLocalizedString("WalletSetting", comment: "皮夾設定")
        backButtonType = .normal
    }
    
    /// 畫面綁定
    private func initBinding() {
        viewModel.pinCodeErrorSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                pinCodeErrorCount += 1
                if pinCodeErrorCount >= pinCodeErrorLimit{
                    self.showAlert(title: NSLocalizedString("PinCodeError", comment: ""),
                                   content: NSLocalizedString("PinCodeErrorOverFive", comment: ""))
                }
                updateErrorHint(isHidden: false)
            }.store(in: &cancelSet)
        
        viewModel.veriflySuccessSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                
                /*將本頁移除*/
                if var vcs = navigationController?.viewControllers {
                    vcs.removeAll { $0 is EnterPinCodeViewController }
                    navigationController?.viewControllers = vcs
                }
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
        viewModel.veriflyUser(pinCode: enterTextField.text ?? "")
    }
    
    @IBAction func onClickCancel(_ sender: Any) {
        navigationController?.popViewController(animated: true)
    }
}
