//
//  CustomPickerTableViewCell.swift
//  DigitalWallet
//

import UIKit

class CustomPickerTableViewCell: UITableViewCell {

    @IBOutlet weak var nameLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        initView()
    }
    
    private func initView() {
        contentView.layer.cornerRadius = 8
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    /// 設定`Cell`
    /// - Parameters:
    ///   - strName: 名稱
    ///   - isSelect: 是否有被選擇
    func setupCell(pickerItem: CustomPickerItem, isSelect: Bool) {
        contentView.backgroundColor = isSelect ? .D_4_D_9_E_9 : .white
        nameLabel.text = pickerItem.name
        /// 設定文字顏色
        var textColor: UIColor
        switch pickerItem.status {
        case .normal:
            textColor = ._4_E_61_A_7
            break
        case .delete:
            textColor = .C_94_D_70
            break
        case .certificateLog:
            textColor = ._464646
            break
        }
        nameLabel.textColor = textColor
    }
}
