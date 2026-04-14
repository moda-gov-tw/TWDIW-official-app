//
//  RemindCardView.swift
//  DigitalWallet
//

import UIKit

class RemindCardView: UIView {

    @IBOutlet weak var cardNameLabel: UILabel!
    @IBOutlet weak var dotLabel: UILabel!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        loadNib(RemindCardView.self)
        setupView()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        loadNib(RemindCardView.self)
        setupView()
    }
    
    private func setupView() {
        dotLabel.font = FontName.NotoSansTC_Bold.font.withSize(16)
        cardNameLabel.font = FontName.NotoSansTC_Bold.font.withSize(16)
    }
    
}
