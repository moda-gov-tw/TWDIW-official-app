//
//  VerifiablePresentationInfoTableViewCell.swift
//  DigitalWallet
//

import UIKit

class VerifiablePresentationInfoTableViewCell: UITableViewCell {
    
    @IBOutlet weak var titleLabel: UILabel!

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    func setupCell(authorizingAgency: String?) {
        let authorizingAgencyTitle: String = NSLocalizedString("AuthorizingInfoTitle", comment: "提供資料的對象：")
        let combineAuthorizingAgencyAttributedString = NSMutableAttributedString()
        let authorizingAgencyTitleAttributedString = NSMutableAttributedString(string: authorizingAgencyTitle, attributes: [
            .font: FontName.NotoSansTC_Bold.font.withSize(16.0)
        ])
        let authorizingAgencyContentAttributedString = NSMutableAttributedString(string: authorizingAgency ?? "", attributes: [
            .font: FontName.NotoSansTC_Regular.font.withSize(16.0)
        ])
        combineAuthorizingAgencyAttributedString.append(authorizingAgencyTitleAttributedString)
        combineAuthorizingAgencyAttributedString.append(authorizingAgencyContentAttributedString)
        titleLabel.attributedText = combineAuthorizingAgencyAttributedString
    }
}
