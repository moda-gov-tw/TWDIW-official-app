//
//  VerifiablePresentationListInfoTableViewCell.swift
//  DigitalWallet
//

import UIKit

class VerifiablePresentationListInfoTableViewCell: UITableViewCell {
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var eyeButton: UIButton!
    
    /// 點擊眼睛狀態
    var changeEyeStatus: ((Bool) -> Void)?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    override func layoutSubviews() {
        initView()
    }
    
    private func initView() {
        titleLabel.font = .boldNotoSansTCFontOfSize(fontSize: 16)
        
        // 眼睛按鈕設定
        let openEyesImage = UIImage(named: ImageName.CreateWallet.OpenEyes.rawValue)
        let closeEyesImage = UIImage(named: ImageName.CreateWallet.CloseEyes.rawValue)
        eyeButton.setImage(closeEyesImage, for: .normal)
        eyeButton.setImage(openEyesImage, for: .selected)
    }
    
    @IBAction func clickOnEyeButton(_ sender: UIButton) {
        changeEyeStatus?(!sender.isSelected)
    }
    
    func setupCell(eyeOpen: Bool) {
        eyeButton.isSelected = eyeOpen
        titleLabel.text = NSLocalizedString("AuthorizingCardListTitle", comment: "提供的資料")
    }
}
