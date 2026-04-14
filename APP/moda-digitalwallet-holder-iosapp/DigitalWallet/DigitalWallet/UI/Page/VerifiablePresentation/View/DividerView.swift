//
//  DividerView.swift
//  DigitalWallet
//

import UIKit

class DividerView: UIView {

    @IBOutlet weak var dividerBackgroundView: UIView!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        loadNib(DividerView.self)
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        loadNib(DividerView.self)
    }

    
}
