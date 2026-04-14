//
//  CustomNavigationBar.swift
//  DigitalWallet
//

import UIKit
import Combine

class CustomNavigationBar: UINavigationBar {
    @IBOutlet weak var walletBackgroundView: UIView!
    @IBOutlet weak var logoImageView: UIImageView!
    @IBOutlet weak var refreshButton: UIButton!
    @IBOutlet weak var OperationLogButton: UIButton!
    @IBOutlet weak var walletNameLabel: UILabel!
    
    let selectWalletSubject = PassthroughSubject<Void, Never>()
    let refreshButtonClickSubject = PassthroughSubject<Void, Never>()
    let operationLogButtonClickSubject = PassthroughSubject<Void, Never>()
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        commonInit()
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    
    private func commonInit() {
        guard let view = Bundle.main.loadNibNamed("CustomNavigationBar", owner: self, options: nil)?.first as? UIView else { return }
        view.frame = self.bounds
        view.clipsToBounds = false
        view.layer.shadowOpacity = 0.2
        view.layer.shadowOffset = CGSize(width: 0, height: 10)
        view.layer.shadowColor = UIColor.A_8_A_8_A_8.cgColor
        addSubview(view)
    }
    
    @IBAction func onClickRefreshButton(_ sender: Any) {
        refreshButtonClickSubject.send()
    }
    
    @IBAction func onClickOperationButton(_ sender: Any) {
        operationLogButtonClickSubject.send()
    }
}
