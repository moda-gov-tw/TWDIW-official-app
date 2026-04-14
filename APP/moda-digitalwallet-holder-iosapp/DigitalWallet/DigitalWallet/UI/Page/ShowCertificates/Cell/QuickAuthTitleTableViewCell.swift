//
//  StoreListTitleTableViewCell.swift
//  DigitalWallet
//

import UIKit

class QuickAuthTitleTableViewCell: UITableViewCell {
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var expandButton: UIButton!
    /// 點擊展開收縮
    var onClickExpand: (() -> Void)?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        setupView()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    private func setupView() {
        selectionStyle = .none
        expandButton.isHidden = true
        titleLabel.font = FontName.NotoSansTC_Bold.font.withSize(14)
    }
    
    @IBAction func clickExpand(_ sender: Any) {
        expandButton.isSelected.toggle()
        onClickExpand?()
    }
}

extension QuickAuthTitleTableViewCell {
    func setupCell(title: String, arrow: Bool) {
        titleLabel.text = title
        expandButton.isHidden = arrow
    }
}
