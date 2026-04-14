//
//  CardRequirementView.swift
//  DigitalWallet
//

import UIKit

class CardRequirementView: UIView {
    
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var contentLabel: UILabel!
    
    var info: CardRequirement?

    override init(frame: CGRect) {
        super.init(frame: frame)
        loadNib(CardRequirementView.self)
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        loadNib(CardRequirementView.self)
    }

    func setupView(_ info: CardRequirement?, isEnable: Bool, eyeOpen: Bool) {
        self.info = info
        
        let nameColor: UIColor = isEnable ? ._464646 : .AAAAAA
        nameLabel.textColor = nameColor
        nameLabel.text = info?.name
        
        let contentColor: UIColor = isEnable ? ._767676 : .AAAAAA
        contentLabel.textColor = contentColor
        contentLabel.text = eyeOpen ? info?.value : Config.hiddenText
    }
    
}
