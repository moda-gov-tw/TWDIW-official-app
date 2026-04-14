//
//  VerifyResultTableViewCell.swift
//  DigitalWallet
//

import UIKit

class VerifyResultTableViewCell: UITableViewCell {

    @IBOutlet weak var groupNameLabel: UILabel!
    @IBOutlet weak var cardsStackView: UIStackView!
    
    let padding: CGFloat = 16.0
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    override func prepareForReuse() {
        super.prepareForReuse()
        cardsStackView.arrangedSubviews.forEach({ $0.removeFromSuperview() })
    }
    
    func setupCell(_ data: VerifyGroupData, vpName: String, corners: CACornerMask) {
        contentView.layer.addMaskedCorners(radius: 12.0, corners: corners)
        
        let isLast = corners.contains(.layerMaxXMaxYCorner)
        self.separatorInset = UIEdgeInsets(top: 0, left: isLast ? self.bounds.width + padding*2 : padding, bottom: 0, right: isLast ? 0 : padding)
        let groupName = data.groupName ?? vpName
        groupNameLabel.text = groupName
        
        data.cards
            .filter({$0.isSelect && $0.infos.contains(where: {$0.isSelect})})
            .forEach({
            let cardLabel = UILabel()
            let cardMessage = "\($0.card.cardName ?? "")：\($0.infoMessage ?? "")"
            cardLabel.numberOfLines = 0
            cardLabel.font = FontName.NotoSansTC_Medium.font.withSize(12.0)
            cardLabel.textColor = ._767676
            cardLabel.text = cardMessage
            cardsStackView.addArrangedSubview(cardLabel)
        })
    }
    
    func setupCell(_ data: DwModa201iCustomField, vpName: String, corners: CACornerMask) {
        contentView.layer.addMaskedCorners(radius: 12.0, corners: corners)
        
        let isLast = corners.contains(.layerMaxXMaxYCorner)
        self.separatorInset = UIEdgeInsets(top: 0, left: isLast ? self.bounds.width + padding*2 : padding, bottom: 0, right: isLast ? 0 : padding)
        let groupName = data.cname ?? vpName
        groupNameLabel.text = groupName
        let cardLabel = UILabel()
        cardLabel.numberOfLines = 0
        cardLabel.font = FontName.NotoSansTC_Medium.font.withSize(12.0)
        cardLabel.textColor = ._767676
        cardLabel.text = data.value
        cardsStackView.addArrangedSubview(cardLabel)
    }
}
