//
//  ContactCustomerServiceTableViewCell.swift
//  DigitalWallet
//

import UIKit

class ContactCustomerServiceTableViewCell: UITableViewCell {
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var messageLabel: UILabel!
    @IBOutlet weak var mailLabel: UILabel!
    @IBOutlet weak var contentBackgroundView: UIView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        titleLabel.sizeToFit()
        
        self.selectionStyle = .none//點選不變色
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
    }
    
    func setItemStyle(){
        contentBackgroundView.layer.cornerRadius = 12
        contentBackgroundView.layer.shadowColor = UIColor._929292.cgColor
        contentBackgroundView.layer.shadowOpacity = 0.2
        contentBackgroundView.layer.shadowOffset = CGSize(width: 0, height: 0)
        contentBackgroundView.layer.shadowRadius = 5
    }
}
