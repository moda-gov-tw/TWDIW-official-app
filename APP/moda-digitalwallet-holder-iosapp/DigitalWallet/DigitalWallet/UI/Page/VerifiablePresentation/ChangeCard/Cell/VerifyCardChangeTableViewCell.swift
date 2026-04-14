//
//  VerifyCardChangeTableViewCell.swift
//  DigitalWallet
//

import UIKit

class VerifyCardChangeTableViewCell: UITableViewCell {

    @IBOutlet weak var backGroundView: UIView!
    @IBOutlet weak var cardNameLabel: UILabel!
    @IBOutlet weak var orgNameLabel: UILabel!
    @IBOutlet weak var expandButton: UIButton!
    @IBOutlet weak var radioButtonImageView: UIImageView!
    @IBOutlet weak var selectCardButton: UIButton!
    @IBOutlet weak var cardsStackView: UIStackView!
    
    /// 刷新UI
    var refreshUI: (()->Void)?
    
    /// 點擊卡片
    var selectCard: ((CardInfoData)->Void)?
    
    var groupData: [CardInfoData] = []
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        setupUI()
    }
    
    override func prepareForReuse() {
        super.prepareForReuse()
        cardsStackView.arrangedSubviews.forEach({ $0.removeFromSuperview() })
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    private func setupUI() {
        backGroundView.layer.borderWidth = 1.0
        backGroundView.layer.cornerRadius = 12.0
        backGroundView.layer.borderColor = UIColor.D_4_D_9_E_9.cgColor
        backGroundView.clipsToBounds = true
    }
    
    func setupCell(_ data: [CardInfoData], isEnable: Bool, eyeOpen: Bool) {
        groupData = data
        let isMultiple = data.count > 1
        let isExpand = data.first?.isExpand ?? false
        let isSelect = data.first?.isSelect ?? false
        
        let disableColor = UIColor.AAAAAA
        var backgroundColor: UIColor?
        var cardNameColor: UIColor?
        var orgNameColor: UIColor?
        
        var radioButtonImage: UIImage?
        
        
        expandButton.isSelected = isExpand
        expandButton.setTitle("", for: .normal)
        expandButton.setImage(isExpand ? .arrowUp20.withRenderingMode(.alwaysTemplate) : .arrowDown20.withRenderingMode(.alwaysTemplate), for: .normal)
        expandButton.tintColor = isEnable ? ._4_E_61_A_7 : .D_1_D_1_D_6
        expandButton.setTitleColor(isEnable ? ._4_E_61_A_7 : .D_1_D_1_D_6, for: .normal)
        expandButton.isEnabled = isEnable
        expandButton.semanticContentAttribute = .forceRightToLeft
        
        radioButtonImageView.alpha = isMultiple ? 0 : 1
        
        cardNameLabel.text = data.first?.card.cardName
        orgNameLabel.text = data.first?.card.orgName
        
        if !isEnable {
            backgroundColor = .F_4_F_4_F_4
            cardNameColor = disableColor
            orgNameColor = disableColor
            radioButtonImage = isSelect ? .radioButtonDisableFill : .radioButtonDisable
        } else {
            backgroundColor = .white
            cardNameColor = ._464646
            orgNameColor = ._767676
            radioButtonImage = isSelect ? .radioButtonEnableFill : .radioButtonEnable
        }
        
        backGroundView.backgroundColor = backgroundColor
        cardNameLabel.textColor = cardNameColor
        orgNameLabel.textColor = orgNameColor
        radioButtonImageView.image = radioButtonImage
        
        data.forEach { card in
            cardsStackView.addArrangedSubview(DividerView())
            let cardView = ChangeCardView()
            cardView.setupView(card, isEnable: isEnable, eyeOpen: eyeOpen, isMultiple: isMultiple)
            cardView.selectCard = { [weak self] selectCard in
                guard let self else { return }
                self.selectCard?(selectCard)
            }
            cardsStackView.addArrangedSubview(cardView)
        }
    }
    
    @IBAction func onClickExpandButton(_ sender: UIButton) {
        sender.isSelected.toggle()
        groupData.forEach({$0.isExpand = sender.isSelected})
        refreshUI?()
    }
    
    @IBAction func onClickSelectCardButton(_ sender: Any) {
        guard let card = groupData.first,
              groupData.count <= 1 else { return }
        selectCard?(card)
    }
}
