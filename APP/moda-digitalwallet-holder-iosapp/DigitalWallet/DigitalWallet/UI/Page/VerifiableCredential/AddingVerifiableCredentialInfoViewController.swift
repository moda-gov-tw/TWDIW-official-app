//
//  AddingVerifiableCredentialInfoViewController.swift
//  DigitalWallet
//

import UIKit

class AddingVerifiableCredentialInfoViewController: BotttomSheetViewController {
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var directionsLabel: UILabel!
    @IBOutlet weak var confirmButton: UIButton!

    @IBOutlet weak var contentBackgroundView: UIView!
    @IBOutlet weak var cardBackgroundView: UIView!
    
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var organLabel: UILabel!
    @IBOutlet weak var backgroundImageView: UIImageView!
    @IBOutlet weak var cardVerifiedImageView: UIImageView!
    /// 漸層底層
    @IBOutlet weak var gradientView: UIView!
    /// 自定義文字
    @IBOutlet weak var customLabel: UILabel!
    
    let verifiableCredentailResult: VerifiableCredentailResult?
    let applyVCData: ApplyVCDataResponse
    let orgName: String
    let cardImageData: Data?
    let displayName: String
    
    init(applyVCData: ApplyVCDataResponse, orgName: String, cardImageData: Data?, displayName: String, verifiableCredentailResult: VerifiableCredentailResult?) {
        self.applyVCData = applyVCData
        self.orgName = orgName
        self.cardImageData = cardImageData
        self.displayName = displayName
        self.verifiableCredentailResult = verifiableCredentailResult
        super.init(nibName: "AddingVerifiableCredentialInfoViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupData()
    }
    
    override func viewDidLayoutSubviews() {
        initView()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }
    
    private func initView() {
        confirmButton.layer.cornerRadius = confirmButton.frame.height / 2
        
        // 設置圓角
        contentBackgroundView.layer.cornerRadius = 20
        contentBackgroundView.layer.masksToBounds = false // 讓陰影可以顯示在圓角外部
        
        // 設置陰影
        contentBackgroundView.layer.shadowColor = UIColor._929292.cgColor
        contentBackgroundView.layer.shadowOpacity = 0.2
        contentBackgroundView.layer.shadowOffset = CGSize(width: 0, height: 0)
        contentBackgroundView.layer.shadowRadius = 5
        
        // 設置 shadowPath 為圓角矩形
        contentBackgroundView.layer.shadowPath = UIBezierPath(roundedRect: contentBackgroundView.bounds, cornerRadius: 20).cgPath
        
        // 卡片背景圓角
        cardBackgroundView.layer.cornerRadius = 20
        cardBackgroundView.clipsToBounds = true
        
        // 文字字體
        organLabel.font = .regularNotoSansTCFontOfSize(fontSize: 12)
        nameLabel.font = .boldNotoSansTCFontOfSize(fontSize: 14)
        customLabel.font = .regularNotoSansTCFontOfSize(fontSize: 12)
        
        titleLabel.font = .boldNotoSansTCFontOfSize(fontSize: 18)
        
        // 底部漸層色
        if let sublayers = gradientView.layer.sublayers, sublayers.contains(where: { $0 is CAGradientLayer }) {
            sublayers.first?.frame = gradientView.bounds
            return
        }
        
        let gredientLayer = CAGradientLayer()
        
        gredientLayer.colors = [
            UIColor.CFCFCF.cgColor,
            UIColor.white.withAlphaComponent(0.0).cgColor
        ]
        gredientLayer.startPoint = CGPoint(x:0, y: 0.5)
        gredientLayer.endPoint = CGPoint(x: 1, y: 0.5)
        gredientLayer.frame = gradientView.bounds
        
        gradientView.layer.insertSublayer(gredientLayer, at: 0)
    }
    
    private func setupData() {
        let cardName = applyVCData.credentialDefinition.display.first?.name ?? ""
        
        titleLabel.text = NSLocalizedString("FinishAdd", comment: "")
        nameLabel.text = cardName
        organLabel.text = orgName
        customLabel.text = displayName
        directionsLabel.text = NSLocalizedString("canUseInCardOverView", comment: "") + "「\(cardName)」。"
        
        if let cardImageData {
            backgroundImageView.image = UIImage(data: cardImageData)
        } else {
            backgroundImageView.image = .normalCardTop
        }
        
        cardVerifiedImageView.isHidden = !(verifiableCredentailResult?.trustBadge ?? false)
    }

    @IBAction func onClickCancel(_ sender: Any) {
        dismiss(animated: true) {
            CustomTabBarController.selectIndexSubject.send(0)
        }
    }
    
    @IBAction func onClickConfirm(_ sender: Any) {
        dismiss(animated: true) {
            CustomTabBarController.selectIndexSubject.send(0)
        }
    }
}
