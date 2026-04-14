//
//  TimeSelectTableViewCell.swift
//  DigitalWallet
//

import UIKit

class TimeSelectTableViewCell: UITableViewCell {
    @IBOutlet weak var radioImageView: UIImageView!
    @IBOutlet weak var itemNameLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        itemNameLabel.sizeToFit()
        selectionStyle = .none//點選不變色
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
}
