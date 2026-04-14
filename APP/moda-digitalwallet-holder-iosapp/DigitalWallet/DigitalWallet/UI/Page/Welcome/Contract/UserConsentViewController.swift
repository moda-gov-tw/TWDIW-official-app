//
//  ContractViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class UserConsentViewController: BaseViewController {
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var agreeLicenseButton: UIButton!
    @IBOutlet weak var agreeLicenseLabel: UILabel!
    @IBOutlet weak var checkoutLicenseLabel: UILabel!
    @IBOutlet weak var checkoutLicenseView: UIStackView!
    @IBOutlet weak var agreePrivacyButton: UIButton!
    @IBOutlet weak var agreePrivacyLabel: UILabel!
    @IBOutlet weak var checkoutPrivacyLabel: UILabel!
    @IBOutlet weak var checkoutPrivacyView: UIStackView!
    @IBOutlet weak var confirmButton: UIButton!
    
    private let viewModel: UserConsentViewModel
    
    private lazy var tapCheckoutLicense = UITapGestureRecognizer()
    private lazy var tapCheckoutPrivacy = UITapGestureRecognizer()
    
    private var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: UserConsentViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "UserConsentViewController", bundle: nil)
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
        title = NSLocalizedString("DigitalIdentityWalletContract", comment: "")
        imageView.layer.contentsRect = CGRect(x: 0, y: 0.2, width: 1, height: 0.8)
        confirmButton.layer.cornerRadius = confirmButton.frame.height * 0.5
        titleLabel.font = .boldNotoSansTCFontOfSize(fontSize: 20)
        titleLabel.text = NSLocalizedString("BeforeUseWallet", comment: "")
        
        agreeLicenseLabel.text = NSLocalizedString("AgreeLicense", comment: "")
        agreeLicenseLabel.font = .regularNotoSansTCFontOfSize(fontSize: 16)
        checkoutLicenseLabel.text = NSLocalizedString("DigitalIdentityWalletLicense", comment: "")
        checkoutLicenseLabel.font = .boldNotoSansTCFontOfSize(fontSize: 14)
        checkoutLicenseView.addGestureRecognizer(tapCheckoutLicense)
        
        agreePrivacyLabel.text = NSLocalizedString("AgreePrivacy", comment: "")
        agreePrivacyLabel.font = .regularNotoSansTCFontOfSize(fontSize: 16)
        checkoutPrivacyLabel.text = NSLocalizedString("PersonalPrivacyPolicy", comment: "")
        checkoutPrivacyLabel.font = .boldNotoSansTCFontOfSize(fontSize: 14)
        checkoutPrivacyView.addGestureRecognizer(tapCheckoutPrivacy)
    }
    
    func initBinding() {
        viewModel.terms.forEach({ terms in
            switch terms.content {
            case .License:
                terms.$isAgree
                    .receive(on: DispatchQueue.main)
                    .sink { [weak self] isAgree in
                        guard let self else { return }
                        agreeLicenseButton.isSelected = isAgree
                        if viewModel.terms.allSatisfy({$0.isAgree}) {
                            confirmButton.isEnabled = true
                            confirmButton.backgroundColor = UIColor._4_E_61_A_7
                        } else {
                            confirmButton.isEnabled = false
                            confirmButton.backgroundColor = UIColor.DEDEDE
                        }
                    }.store(in: &cancelSet)
                
            case .Privacy:
                terms.$isAgree
                    .receive(on: DispatchQueue.main)
                    .sink { [weak self] isAgree in
                        guard let self else { return }
                        agreePrivacyButton.isSelected = isAgree
                        if viewModel.terms.allSatisfy({$0.isAgree}) {
                            confirmButton.isEnabled = true
                            confirmButton.backgroundColor = UIColor._4_E_61_A_7
                        } else {
                            confirmButton.isEnabled = false
                            confirmButton.backgroundColor = UIColor.DEDEDE
                        }
                    }.store(in: &cancelSet)
            case .VPAgreement:
                break
            }
        })
        
        viewModel.showTermsSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] terms in
                let termsViewModel = TermsViewModel(terms: terms)
                let termsVC = TermsViewController(viewModel: termsViewModel)
                self?.navigationController?.pushViewController(termsVC, animated: true)
            }.store(in: &cancelSet)
        
        tapCheckoutLicense.gesturePublisher
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                guard let self = self else { return }
                viewModel.checkoutTerms(type: .License)
            }.store(in: &cancelSet)
        
        tapCheckoutPrivacy.gesturePublisher
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                guard let self = self else { return }
                viewModel.checkoutTerms(type: .Privacy)
            }.store(in: &cancelSet)
    }
    
    @IBAction func onClickCheck(_ sender: UIButton) {
        sender.isSelected.toggle()
        if sender == agreeLicenseButton {
            viewModel.checkTerms(type: .License, isAgree: sender.isSelected)
        } else {
            viewModel.checkTerms(type: .Privacy, isAgree: sender.isSelected)
        }
    }
    
    @IBAction func onClickConfirm(_ sender: Any) {
        let container = AppDelegate.shared.container
        let repository = CreateWalletRepository(databaseManager: container.resolve(SQLiteManager.self)!, verifiableManager: container.resolve(VerifiableManager.self)!)
        let settingViewModel = LoginSettingViewModel(repository: repository, biometricVerifyManager: AppDelegate.shared.container.resolve(BiometricVerifyManager.self)!)
        let settingVC = LoginSettingViewController(viewModel: settingViewModel)
        self.navigationController?.pushViewController(settingVC, animated: true)
    }
    
    
    /// 使用者點擊取消回歡迎頁
    /// - Parameter sender: 取消按鈕
    @IBAction func onClickCancel(_ sender: Any) {
        if let welcomeVC = navigationController?.viewControllers.first(where: { vc in
            vc.isKind(of: WelcomeViewController.self)
        }) {
            navigationController?.popToViewController(welcomeVC, animated: true)
        }
    }
}
