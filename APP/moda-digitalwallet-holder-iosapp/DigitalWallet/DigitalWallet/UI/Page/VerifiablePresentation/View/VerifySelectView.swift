//
//  VerifySelectView.swift
//  DigitalWallet
//

import UIKit

class VerifySelectView: UIView {
    
    /// 選擇框圖片
    enum CheckBoxImageType: String {
        /// 未勾選
        case Uncheck
        /// 已勾選
        case Check
        /// 移除
        case Remove
        
        var image: UIImage? {
            return UIImage(named: self.rawValue)
        }
    }

    @IBOutlet weak var checkBoxButton: UIButton!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var contentLabel: UILabel!
    
    /// 點擊選擇
    var clickCheckBox: ((Bool)->Void)?
    
    var info: CardRequirement?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        loadNib(VerifySelectView.self)
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        loadNib(VerifySelectView.self)
    }
    
    @IBAction func onClickCheckBox(_ sender: UIButton) {
        sender.isSelected.toggle()
        info?.isSelect = sender.isSelected
        clickCheckBox?(sender.isSelected)
    }
    
    func setupView(info: CardRequirement, eyeOpen: Bool, rule: VerifyCardRule?) {
        self.info = info
        titleLabel.text = self.info?.name
        contentLabel.text = eyeOpen ? self.info?.value : Config.hiddenText
        checkBoxButton.isSelected = self.info?.isSelect ?? false
        
        if rule == .all {
            checkBoxButton.isEnabled = false
            checkBoxButton.isSelected = false
        }
        
    }
    
    /// 更新選框按鈕選取狀態
    func updateCheckBoxButtonSelectedImage(_ imageType: CheckBoxImageType) {
        checkBoxButton.setImage(imageType.image, for: .selected)
    }
}
