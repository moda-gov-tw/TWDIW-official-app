//
//  AuthorizedCardListTableViewCell.swift
//  DigitalWallet
//

import UIKit

class AuthorizedCardListTableViewCell: UITableViewCell {
    
    @IBOutlet weak var cardNameLabel: UILabel!
    @IBOutlet weak var orgNameLabel: UILabel!
    @IBOutlet weak var applicationButton: UIButton!
    

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    func setupCell(_ data: VirtualCard) {
        cardNameLabel.text = data.infoItem?.vcName ?? data.card
        orgNameLabel.text = data.infoItem?.orgName ?? " "
    }
}
