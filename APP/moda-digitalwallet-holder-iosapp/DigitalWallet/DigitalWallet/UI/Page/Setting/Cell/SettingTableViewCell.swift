//
//  SettingTableViewCell.swift
//  DigitalWallet
//

import UIKit
import Combine

class SettingTableViewCell: UITableViewCell {
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var contentLabel: UILabel!
    @IBOutlet weak var rightImageView: UIImageView!
    @IBOutlet weak var contentBackgroundView: UIView!
    @IBOutlet weak var switchButton: UIButton!
    
    var onClickSwitchButton: ((SettingItemType, Bool) -> Void)?
    var style: SettingItemType?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        titleLabel.sizeToFit()
        
        self.selectionStyle = .none//點選不變色
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    func setItemStyle(itemName: String, style: SettingItemType, content: String = "", cellStyle: SettingTableViewCellType = .normal){
        contentBackgroundView.layer.cornerRadius = 12
        contentBackgroundView.layer.shadowColor = UIColor._929292.cgColor
        contentBackgroundView.layer.shadowOpacity = 0.2
        contentBackgroundView.layer.shadowOffset = CGSize(width: 0, height: 0)
        contentBackgroundView.layer.shadowRadius = 5
        titleLabel.text = itemName
        self.style = style
        switch cellStyle {
        case .normal:
            switchButton.isHidden = true
            rightImageView.isHidden = false
            contentLabel.isHidden = true
            titleLabel.textColor = ._4_E_61_A_7
            break
        case .withSwitchButton(let isOn):
            switchButton.isHidden = false
            switchButton.setImage((isOn ? .switchOn : .switchOff), for: .normal)
            switchButton.isSelected = isOn
            rightImageView.isHidden = true
            contentLabel.isHidden = true
            titleLabel.textColor = ._4_E_61_A_7
            break
        case .warning:
            switchButton.isHidden = true
            rightImageView.isHidden = true
            contentLabel.isHidden = true
            titleLabel.textColor = .C_94_D_70
            break
        }
    }
    
    @IBAction func onClickSwitch(_ sender: UIButton) {
        guard let style else { return }
        onClickSwitchButton?(style, sender.image(for: .normal) == .switchOn)
    }
}
