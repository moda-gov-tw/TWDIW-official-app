//
//  SelectTableViewCell.swift
//  DigitalWallet
//

import UIKit
import Combine

class SelectTableViewCell: UITableViewCell {
    @IBOutlet weak var selectButton: UIButton!
    @IBOutlet weak var titleLabel: UILabel!
    
    let selectSubject = PassthroughSubject<(Bool, Int), Never>()
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    @IBAction func onClickSelect(_ sender: UIButton) {
        sender.isSelected.toggle()
        selectSubject.send((sender.isSelected, sender.tag))
    }
}
