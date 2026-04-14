//
//  VP05QRCodeViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class VP05QRCodeViewController: BaseViewController {
    @IBOutlet weak var showQRCodeLabel: UILabel!
    @IBOutlet weak var certificateTitleLabel: UILabel!
    @IBOutlet weak var contentView: UIView!
    @IBOutlet weak var certificateQRCodeLabel: UILabel!
    @IBOutlet weak var showQRCodeStackView: UIStackView!
    @IBOutlet weak var qrCodeImageView: UIImageView!
    @IBOutlet weak var countDownLabel: UILabel!
    @IBOutlet weak var qrCodeExpiredStackView: UIStackView!
    @IBOutlet weak var qrCodeExpiredLabel: UILabel!
    @IBOutlet weak var reGenerateQRCodeLabel: UILabel!
    @IBOutlet weak var finishButton: UIButton!
    @IBOutlet weak var regenerateQRCodeButton: UIButton!
    
    var enlargedQRCodeView: EnlargedQRCodeView?
    let viewModel: VP05QRCodeViewModel
    private var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: VP05QRCodeViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "VP05QRCodeViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupView()
        setupButton()
        dataBinding()
    }

    private func setupView() {
        isHideNavigationBar = true
        showQRCodeLabel.text = NSLocalizedString("PleaseShowQRCode", comment: "")
        showQRCodeLabel.font = FontName.NotoSansTC_Bold.font.withSize(20)
        certificateQRCodeLabel.text = NSLocalizedString("EnlargeQRCode", comment: "")
        certificateQRCodeLabel.font = FontName.NotoSansTC_Regular.font
        qrCodeExpiredLabel.text = NSLocalizedString("QRCodeAlreadyExpired", comment: "")
        qrCodeExpiredLabel.font = FontName.NotoSansTC_Bold.font.withSize(18)
        reGenerateQRCodeLabel.text = NSLocalizedString("PleaseRegenerateQRCode", comment: "")
        reGenerateQRCodeLabel.font = FontName.NotoSansTC_Regular.font.withSize(16)
        certificateTitleLabel.text = viewModel.repository.vp05Info?.certificateInfo.name
        certificateTitleLabel.font = FontName.NotoSansTC_Bold.font
        contentView.layer.masksToBounds = true
        contentView.layer.cornerRadius = 12
        let clickQRGestureRecongnizer = UITapGestureRecognizer(target: self, action: #selector(onClickQRCode))
        qrCodeImageView.addGestureRecognizer(clickQRGestureRecongnizer)
    }
    
    private func setupButton() {
        finishButton.layer.masksToBounds = true
        regenerateQRCodeButton.layer.masksToBounds = true
        finishButton.setTitle(NSLocalizedString("FinishScan", comment: ""), for: .normal)
        finishButton.titleLabel?.font = FontName.NotoSansTC_Medium.font.withSize(16)
        finishButton.layer.cornerRadius = 20
        regenerateQRCodeButton.setTitle(NSLocalizedString("RegenerateQRCode", comment: ""), for: .normal)
        regenerateQRCodeButton.titleLabel?.font = FontName.NotoSansTC_Medium.font.withSize(16)
        regenerateQRCodeButton.layer.cornerRadius = 20
        regenerateQRCodeButton.layer.borderWidth = 1
        regenerateQRCodeButton.layer.borderColor = UIColor._4_E_61_A_7.cgColor
    }
    
    private func dataBinding() {
        NotificationCenter.default.publisher(for: UIScene.didEnterBackgroundNotification)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                viewModel.cancelTimer()
            }.store(in: &cancelSet)
        
        NotificationCenter.default.publisher(for: UIScene.willEnterForegroundNotification)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                viewModel.comeBackToTimer()
            }.store(in: &cancelSet)
        
        viewModel.$qrCodeData
            .receive(on: DispatchQueue.main)
            .compactMap{ $0 }
            .sink { [weak self] data in
                guard let self = self else { return }
                viewModel.startCountdown()
                qrCodeImageView.image = UIImage(data: data)
            }.store(in: &cancelSet)
        
        viewModel.$qrCodeStatus
            .receive(on: DispatchQueue.main)
            .dropFirst()
            .sink { [weak self] status in
                guard let self = self else { return }
                showQRCodeStackView.isHidden = (status == .expired)
                qrCodeExpiredStackView.isHidden = (status == .active)
                if status == .expired {
                    enlargedQRCodeView?.removeFromSuperview()
                    if let birginBrightness = UserDefaultManager.shared.getObject(key: .Brightness, type: CGFloat.self),
                       birginBrightness != -1 {
                        UIScreen.main.brightness = UserDefaultManager.shared.getObject(key: .Brightness, type: CGFloat.self) ?? UIScreen.main.brightness
                    }
                    UserDefaultManager.shared.setObject(value: -1, key: .Brightness)
                }
            }.store(in: &cancelSet)
        
        
        viewModel.$countDownString
            .receive(on: DispatchQueue.main)
            .sink { [weak self] str in
                guard let self = self else { return }
                countDownLabel.attributedText = str
            }.store(in: &cancelSet)
        
        
    }
    
    @objc private func onClickQRCode() {
        UserDefaultManager.shared.setObject(value: UIScreen.main.brightness, key: .Brightness)
        enlargedQRCodeView = EnlargedQRCodeView(frame: view.frame)
        enlargedQRCodeView?.titleLabel.text = showQRCodeLabel.text
        enlargedQRCodeView?.subTitleLabel.text = certificateTitleLabel.text
        enlargedQRCodeView?.qrImageView.image = qrCodeImageView.image
        enlargedQRCodeView?.onClickClose = {
            UIScreen.main.brightness = UserDefaultManager.shared.getObject(key: .Brightness, type: CGFloat.self) ?? UIScreen.main.brightness
            UserDefaultManager.shared.setObject(value: -1, key: .Brightness)
        }
        if let enlargedQRCodeView {
            UIScreen.main.brightness = 1.0
            self.view.addSubview(enlargedQRCodeView)
        }
    }
   
    @IBAction func clickFinish(_ sender: Any) {
        CustomTabBarController.selectIndexSubject.send(2)
    }
    
    @IBAction func clickRegenerate(_ sender: Any) {
        viewModel.callTabbarProtocol.regenerateVP05()
        navigationController?.popToRootViewController(animated: true)
    }
}
