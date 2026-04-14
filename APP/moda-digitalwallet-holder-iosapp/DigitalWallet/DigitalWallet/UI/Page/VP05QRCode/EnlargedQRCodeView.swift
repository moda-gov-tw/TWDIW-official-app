//
//  EnlargedQRCodeView.swift
//  DigitalWallet
//

import UIKit

class EnlargedQRCodeView: UIView {
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var subTitleLabel: UILabel!
    @IBOutlet weak var qrImageView: UIImageView!
    @IBOutlet weak var closeButton: UIButton!
    
    /// 移除關閉
    var onClickClose: (() -> Void)?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        loadNib(EnlargedQRCodeView.self)
        setupView()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        loadNib(EnlargedQRCodeView.self)
        setupView()
    }
    
    private func setupView() {
        closeButton.layer.cornerRadius = closeButton.frame.height / 2
    }
    
    @IBAction func onClickClose(_ sender: Any) {
        onClickClose?()
        self.removeFromSuperview()
    }
    
}
