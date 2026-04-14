//
//  NormalCardTableViewCell.swift
//  DigitalWallet
//

import UIKit

class NormalCardTableViewCell: UITableViewCell {
    
    @IBOutlet weak var contentBackgroundView: UIView!
    @IBOutlet weak var cardBackgroundView: UIView!
    
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var organLabel: UILabel!
    @IBOutlet weak var statusLabel: UILabel!
    @IBOutlet weak var backgroundImageView: UIImageView!
    @IBOutlet weak var cardVerifiedImageView: UIImageView!
    @IBOutlet weak var maskImageView: UIImageView!
    /// 漸層底層
    @IBOutlet weak var gradientView: UIView!
    /// 自定義文字
    @IBOutlet weak var customLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    override func layoutSubviews() {
        contentView.layer.masksToBounds = false
        
        // 設置圓角
        contentBackgroundView.layer.cornerRadius = 20
        // 讓陰影可以顯示在圓角外部
        contentBackgroundView.layer.masksToBounds = false
        
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
        statusLabel.font = .boldNotoSansTCFontOfSize(fontSize: 14)
        customLabel.font = .regularNotoSansTCFontOfSize(fontSize: 12)
        
        statusLabel.text = NSLocalizedString("invalidation", comment: "")
        
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
    
    func setupCell(_ data: UserVerifiableCredentailData) {
        nameLabel.text = data.cardName
        organLabel.text = data.orgName
        
        let bInvalidation = data.verificationStatus == .invalidation
//        let bVerified = data.verificationStatus == .verified
        if let cardImage = data.cardImage {
            backgroundImageView.image = UIImage(data: cardImage)
        } else {
            backgroundImageView.image = .normalCardTop
        }
        if let displayName = data.displayName,
           !displayName.isEmpty {
            gradientView.isHidden = false
            customLabel.text = displayName
        } else {
            gradientView.isHidden = true
            customLabel.text = ""
        }
        
        statusLabel.isHidden = !bInvalidation
        maskImageView.isHidden = !bInvalidation
        cardVerifiedImageView.isHidden = !data.trustBadge
    }
    
    func setupCellInSelectCredential(_ data: UserVerifiableCredentailData) {
        nameLabel.text = data.cardName
        organLabel.text = data.orgName
        customLabel.text = data.displayName
        if let cardImage = data.cardImage {
            backgroundImageView.image = UIImage(data: cardImage)
        } else {
            backgroundImageView.image = .normalCardTop
        }
        
        statusLabel.isHidden = true
        maskImageView.isHidden = true
        cardVerifiedImageView.isHidden = data.trustBadge
    }
}
