//
//  RemindAlertView.swift
//  DigitalWallet
//

import UIKit

class RemindAlertView: UIView {

    @IBOutlet weak var topContentStackView: UIStackView!
    @IBOutlet weak var mainHintLabel: UILabel!
    @IBOutlet weak var scrollViewHeightConstraint: NSLayoutConstraint!
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var mainView: UIView!
    @IBOutlet weak var confirmButton: UIButton!
    
    /// 點擊確認
    var onClickConfirm: (() -> Void)?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        loadNib(RemindAlertView.self)
        setupView()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        loadNib(RemindAlertView.self)
        setupView()
    }
    
    private func setupView() {
        mainHintLabel.font = FontName.NotoSansTC_Bold.font.withSize(18.0)
        mainView.layer.cornerRadius = 12.0
        confirmButton.layer.cornerRadius = confirmButton.frame.height * 0.5
    }
    
    func addView(remindData: ShowRemindListType) {
        topContentStackView.arrangedSubviews.forEach { $0.removeFromSuperview() }
        
        /// 1. 加入提示文字
        let hintLabel: UILabel = UILabel()
        hintLabel.font = FontName.NotoSansTC_Regular.font.withSize(16.0)
        hintLabel.numberOfLines = 0
        hintLabel.textColor = ._767676
        topContentStackView.addArrangedSubview(hintLabel)
        
        let paddingHeight: CGFloat = 12.0
        let paddingView: UIView = UIView()
        paddingView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            paddingView.heightAnchor.constraint(equalToConstant: paddingHeight)
        ])
        paddingView.backgroundColor = .clear
        topContentStackView.addArrangedSubview(paddingView)

        switch remindData {
        case .networkError:
            mainHintLabel.text = NSLocalizedString("CertificateUpdateError", comment: "")
            hintLabel.text = NSLocalizedString("CertificateUpdateNetworkError", comment: "")
            break
        case .expiringList:
            mainHintLabel.text = NSLocalizedString("CertificateExpiring", comment: "")
            hintLabel.text = NSLocalizedString("CertificateExpiringContent", comment: "")
            break
        case .expiredList:
            mainHintLabel.text = NSLocalizedString("CertificateExpired", comment: "")
            hintLabel.text = NSLocalizedString("CertificateExpiredContent", comment: "")
            break
        case .updateErrorList:
            mainHintLabel.text = NSLocalizedString("CertificateUpdateError", comment: "")
            hintLabel.text = NSLocalizedString("CertificateUpdateErrorContent", comment: "")
            break
        }
    
        let hintHeight = hintLabel.text?.calculateTextHeight(font: FontName.NotoSansTC_Medium.font.withSize(16.0), width: scrollView.frame.width) ?? 0.0
        refreshLayout(remindData: remindData, hintHeight: hintHeight + paddingHeight)
    }
    
    private func refreshLayout(remindData: ShowRemindListType, hintHeight: CGFloat) {
        var dataHeight: CGFloat = 0.0
        switch remindData {
        case    .updateErrorList(let data),
                .expiringList(let data),
                .expiredList(let data):
            data.forEach { remind in
                switch remind {
                case    .remind(let userVerifiableCredentailData),
                        .updateError(let userVerifiableCredentailData):
                    let certificateNameView = RemindCardView()
                    certificateNameView.translatesAutoresizingMaskIntoConstraints = false
                    certificateNameView.cardNameLabel.text = userVerifiableCredentailData.cardName
                    topContentStackView.addArrangedSubview(certificateNameView)
                    dataHeight += userVerifiableCredentailData.cardName?.calculateTextHeight(font: FontName.NotoSansTC_Bold.font.withSize(16.0), width: topContentStackView.frame.width - 20) ?? 0.0
                default: break
                }
            }
            break
        default: break
        }
        
        let contentHeight = hintHeight + dataHeight
        
        /// 內容高度計算
        scrollViewHeightConstraint.constant = min(contentHeight, 302)
        layoutIfNeeded()
        setNeedsDisplay()
    }
    
    @IBAction func clickConfrimButton(_ sender: Any) {
        onClickConfirm?()
    }
}
