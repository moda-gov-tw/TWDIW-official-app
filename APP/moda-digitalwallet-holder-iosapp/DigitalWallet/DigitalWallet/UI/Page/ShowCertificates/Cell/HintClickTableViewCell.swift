//
//  HintClickTableViewCell.swift
//  DigitalWallet
//

import UIKit

class HintClickTableViewCell: UITableViewCell {
    @IBOutlet weak var hintLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        setupView()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    private func setupView() {
        selectionStyle = .none
        hintLabel.font = FontName.NotoSansTC_Regular.font.withSize(16)
        hintLabel.text = NSLocalizedString("HintClickStar", comment: "")
    }
}

extension HintClickTableViewCell {
    func setupCell(content: String) {
        hintLabel.text = content
    }
}
