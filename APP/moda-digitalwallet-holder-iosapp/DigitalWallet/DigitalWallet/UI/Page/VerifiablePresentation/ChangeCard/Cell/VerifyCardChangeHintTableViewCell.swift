//
//  VerifyCardChangeHintTableViewCell.swift
//  DigitalWallet
//

import UIKit

class VerifyCardChangeHintTableViewCell: UITableViewCell {
    
    
    @IBOutlet weak var hintLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func setupCell(_ message: String?) {
        hintLabel.text = message
    }
}
