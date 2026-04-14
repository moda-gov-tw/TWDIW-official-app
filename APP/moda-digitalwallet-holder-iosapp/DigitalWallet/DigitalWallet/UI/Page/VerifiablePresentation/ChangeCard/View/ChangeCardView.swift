//
//  ChangeCardView.swift
//  DigitalWallet
//

import UIKit

class ChangeCardView: UIView {
    
    @IBOutlet weak var radioButtonImageView: UIImageView!
    @IBOutlet weak var cardRequirmentsStackView: UIStackView!
    @IBOutlet weak var selectCardButton: UIButton!
    
    var cardInfo: CardInfoData?
    
    /// 點擊卡片
    var selectCard: ((CardInfoData)->Void)?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        loadNib(ChangeCardView.self)
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        loadNib(ChangeCardView.self)
    }
    
    func setupView(_ data: CardInfoData, isEnable: Bool, eyeOpen: Bool, isMultiple: Bool) {
        cardInfo = data
        radioButtonImageView.isHidden = !isMultiple
        selectCardButton.isEnabled = isEnable
        let isSelect = data.isSelect
        var radioButtonImage: UIImage?
        if !isEnable {
            radioButtonImage = isSelect ? .radioButtonDisableFill : .radioButtonDisable
        } else {
            radioButtonImage = isSelect ? .radioButtonEnableFill : .radioButtonEnable
        }
        radioButtonImageView.image = radioButtonImage
        
        
        if data.isExpand {
            /// 展開情境
            data.infos.enumerated().forEach { (index, cardRequirement) in
                if index != 0 {
                    cardRequirmentsStackView.addArrangedSubview(DividerView())
                }
                let cardRequirementView = CardRequirementView()
                cardRequirementView.setupView(cardRequirement, isEnable: isEnable, eyeOpen: eyeOpen)
                cardRequirmentsStackView.addArrangedSubview(cardRequirementView)
            }
        } else {
            /// 收合情境
            let label = UILabel()
            label.font = FontName.NotoSansTC_Regular.font
            label.textColor = isEnable ? ._767676 : .D_1_D_1_D_6
            label.text = eyeOpen ? data.infoContent : Config.hiddenText
            cardRequirmentsStackView.addArrangedSubview(label)
        }
    }
    
    @IBAction func onClickSelectCardButton(_ sender: Any) {
        guard let cardInfo = cardInfo else { return }
        selectCard?(cardInfo)
    }
}
