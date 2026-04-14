//
//  CardLogTableViewCell.swift
//  DigitalWallet
//

import UIKit

class CardLogTableViewCell: UITableViewCell {
    @IBOutlet weak var statusBackgroundView: UIView!
    @IBOutlet weak var statusImageView: UIImageView!
    @IBOutlet weak var statusTitleLabel: UILabel!
    @IBOutlet weak var logDateLabel: UILabel!
    @IBOutlet weak var scenarioLabel: UILabel!
    @IBOutlet weak var expandButton: UIButton!
    
    @IBOutlet weak var infoView: UIView!
    
    @IBOutlet weak var authorizationCredentialsTitleLabel: UILabel!
    @IBOutlet weak var authorizationCredentialsLabel: UILabel!
    @IBOutlet weak var clientTitleLabel: UILabel!
    @IBOutlet weak var clientLabel: UILabel!
    @IBOutlet weak var purposeTitleLabel: UILabel!
    @IBOutlet weak var purposeLabel: UILabel!
    @IBOutlet weak var applyInfosTitleLabel: UILabel!
    @IBOutlet weak var applyInfosLabel: UILabel!
    
    @IBOutlet weak var authoriztionCredentialsStackView: UIStackView!
    /// 點擊展開閉包
    var onClickExpand: (() -> Void)?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        initUI()
        backgroundColor = .clear
        selectionStyle = .none//點選無特效
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    private func initUI() {
        statusBackgroundView.layer.cornerRadius = 14.0
        statusBackgroundView.clipsToBounds = true
        authorizationCredentialsTitleLabel.text = NSLocalizedString("AuthorizationCredentials", comment: "")
        clientTitleLabel.text = NSLocalizedString("clientTitle", comment: "")
        purposeTitleLabel.text = NSLocalizedString("purposeTitle", comment: "")
        applyInfosTitleLabel.text = NSLocalizedString("applyInfosTitle", comment: "")
    }
    
    func setupCell(_ data: CardRecord) {
        statusBackgroundView.backgroundColor = data.type?.backgroundColor
        
        statusImageView.image = data.type?.icon
        
        statusTitleLabel.text = data.type?.title
        statusTitleLabel.textColor = data.type?.titleColor
        
        logDateLabel.text = data.getCreateDate()
        
        scenarioLabel.text = data.recordMessage
        
        /// 授權項目顯示展開關閉按鈕
        let hasExpandButton = data.type == .verify
        expandButton.isHidden = !hasExpandButton
        
        /// 開合按鈕設定
        let imageName: String = data.isExpand ? "ArrowUp20" : "ArrowDown20"
        expandButton.setBackgroundImage(UIImage(named: imageName), for: .normal)
        infoView.isHidden = !data.isExpand
        
        /// 授權資料設定
        if let vcNames = data.vcNames {
            authoriztionCredentialsStackView.isHidden = false
            authorizationCredentialsLabel.text = vcNames
        } else {
            authoriztionCredentialsStackView.isHidden = true
        }
        clientLabel.text = data.client
        purposeLabel.text = data.purpose
        applyInfosLabel.text = data.applyInfos
    }

    /// 卡片擴展按鈕點擊
    @IBAction func onClickExpandButton(_ sender: UIButton) {
        onClickExpand?()
    }
}
