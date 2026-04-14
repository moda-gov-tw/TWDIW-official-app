//
//  PinButton.swift
//  DigitalWallet
//

import Foundation
import UIKit

class PinButton: UIButton {
    
    enum ClickValue: String {
        case zero = "0"
        case one = "1"
        case two = "2"
        case three = "3"
        case four = "4"
        case five = "5"
        case six = "6"
        case seven = "7"
        case eight = "8"
        case nine = "9"
        case backSpace = "999"
    }
    
    /// 點擊值
    var clickValue: ClickValue {
        ClickValue(rawValue: "\(self.tag)") ?? .backSpace
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        initView()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        initView()
    }
    
    private func initView() {
        layer.cornerRadius = self.frame.height * 0.5
    }
}
