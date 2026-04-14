//
//  SetWalletNameViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class SetWalletNameViewController: BaseViewController {
    /// 提示狀態
    enum HintType {
        /// 一般
        case `default`
        /// 錯誤
        case error
        /// 同名
        case sameName
    }
    
    @IBOutlet weak var originWalletNameLabel: UILabel!
    @IBOutlet weak var newWalletNameTextField: CustomTextField!
    @IBOutlet weak var walletNameHintLabel: UILabel!
    @IBOutlet weak var confirmButton: UIButton!
    
    let viewModel: SetWalletNameViewModel
    private let maxLength = 8 //設定最大輸入字數
    
    private var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: SetWalletNameViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "SetWalletNameViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initView()
        initBinding()
        
        newWalletNameTextField.addTarget(self, action: #selector(textFieldDidChange), for: .editingChanged)
    }
    
    private func initView() {
        title = NSLocalizedString("ChangeWalletName", comment: "")
        originWalletNameLabel.text = viewModel.repository.wallet.name
        walletNameHintLabel.text = NSLocalizedString("WalletNameErrorHint", comment: "")
    }
    
    private func initBinding() {
        viewModel.walletNameErrorSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                self.updateHintUI(.error)
            }.store(in: &cancelSet)
        
        viewModel.walletNameIllegalSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                /// 皮夾名稱有誤
                self.showAlert(title: NSLocalizedString("WalletNameIllegalTitle", comment: ""),
                               /// 您輸入的皮夾名稱出現特殊符號，請確認後再次嘗試。
                               content: NSLocalizedString("WalletNameIllegalPleaseTryAgain", comment: "")) {
                    self.updateHintUI(.error)
                }
            }.store(in: &cancelSet)
        
        viewModel.hasSameWalletNameSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                self.showAlert(title: NSLocalizedString("WalletNameReuseTitle", comment: ""),
                               content: NSLocalizedString("WalletNameAlreadyReusePleaseChangeTheName", comment: "")) {
                    self.updateHintUI(.sameName)
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
        
        viewModel.updateWalletNameResultSubject
            .receive(on: DispatchQueue.main)
            .sink { isSuccess in
                
                if isSuccess {
                    self.viewModel.repository.refreshWalletData()//更新資料
                    CustomTabBarController.selectIndexSubject.send(0)//開啟卡片總覽頁
                    
                    self.navigationController?.popToRootViewController(animated: true)
                }
            }.store(in: &cancelSet)
    }
    
    private func updateHintUI(_ hintType: HintType) {
        
        var borderColor: UIColor = .DEDEDE
        var hintColor: UIColor = ._62676_D
        var hintText: String? = NSLocalizedString("WalletNameErrorHint", comment: "")
        switch hintType {
        case .default: break
        case .error:
            borderColor = .EF_2_A_1_C
            hintColor = .EF_2_A_1_C
        case .sameName:
            borderColor = .EF_2_A_1_C
            hintColor = .EF_2_A_1_C
            hintText = NSLocalizedString("WalletNameAlreadyUseHint", comment: "")
        }
        
        newWalletNameTextField.layer.borderColor = borderColor.cgColor
        walletNameHintLabel.textColor = hintColor
        walletNameHintLabel.text = hintText
    }
    
    /**確認*/
    @IBAction func onClickConfirm(_ sender: UIButton) {
        viewModel.updateWalletName(walletName: newWalletNameTextField.text)
    }
    
    /**取消*/
    @IBAction func onClickCancel(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
}

extension SetWalletNameViewController: UITextFieldDelegate {
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
        updateHintUI(.default)
    }
}
