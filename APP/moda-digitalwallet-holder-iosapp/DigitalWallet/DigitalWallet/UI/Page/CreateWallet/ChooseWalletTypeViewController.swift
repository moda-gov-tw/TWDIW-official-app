//
//  ChooseWalletTypeViewController.swift
//  DigitalWallet
//

import UIKit

class ChooseWalletTypeViewController: BaseViewController {
    
    enum NavigationTransactionType: Equatable {
        case push
        case present
    }
    
    /// 一般皮夾背景
    @IBOutlet weak var normalWalletBackgroundView: UIView!
    
    /// 確認按鈕
    @IBOutlet weak var confirmButton: UIButton!
    /// 取消按鈕
    @IBOutlet weak var cancelButton: UIButton!
    
    let transactionType: NavigationTransactionType
    
    init(transactionType: NavigationTransactionType) {
        self.transactionType = transactionType
        super.init(nibName: "ChooseWalletTypeViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        initView()
    }

    /// 畫面設定
    private func initView() {
        title = "建立皮夾"
        
        // 一般皮夾背景
        normalWalletBackgroundView.layer.borderWidth = 3.0
        normalWalletBackgroundView.layer.borderColor = UIColor._6_F_7_FB_7.cgColor
        normalWalletBackgroundView.layer.cornerRadius = 12.0
        
        // 確認按鈕
        confirmButton.layer.cornerRadius = confirmButton.frame.height * 0.5
        
        isHideNavigationBar = false
        
        if transactionType == .present {
            navigationItem.leftBarButtonItem = UIBarButtonItem(image: .arrowBackFiled, style: .plain, target: self, action: #selector(onClickBackBarItem))
        }
        
    }
    
    @IBAction func onClickConfirm(_ sender: Any) {
        let container = AppDelegate.shared.container
        let repository = CreateWalletRepository(databaseManager: container.resolve(SQLiteManager.self)!, verifiableManager: container.resolve(VerifiableManager.self)!)
        let viewModel = WalletNamingViewModel(databaseManager: container.resolve(SQLiteManager.self)!, repository: repository, biometricVerifyManager: container.resolve(BiometricVerifyManager.self)!, namingType: .others)
        let createWalletVC = WalletNamingViewController(viewModel: viewModel)
        navigationController?.pushViewController(createWalletVC, animated: true)
    }
    
    @objc private func onClickBackBarItem() {
        navigationController?.dismiss(animated: true)
    }
    
    @IBAction func onClickCancel(_ sender: Any) {
        switch transactionType {
        case .push:
            navigationController?.popToRootViewController(animated: true)
            break
        case .present:
            onClickBackBarItem()
            break
        }
    }

}
