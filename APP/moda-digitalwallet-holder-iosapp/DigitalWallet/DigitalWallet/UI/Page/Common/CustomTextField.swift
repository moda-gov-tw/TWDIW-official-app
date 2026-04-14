//
//  CustomTextField.swift
//  DigitalWallet
//

import UIKit

class CustomTextField: UITextField {
    
    private let padding = UIEdgeInsets(top: 12,
                                       left: 16,
                                       bottom: 12,
                                       right: 40)
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        initView()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        initView()
    }
    
    private func initView() {
        layer.borderWidth = 1
        layer.masksToBounds = true
        layer.cornerRadius = 12
        layer.borderColor = UIColor.DEDEDE.cgColor
        
        textContentType = .oneTimeCode//不要自動填寫
    }
    
    override func textRect(forBounds bounds: CGRect) -> CGRect {
        super.textRect(forBounds: bounds)
        return bounds.inset(by: padding)
    }
    
    override func placeholderRect(forBounds bounds: CGRect) -> CGRect {
        super.placeholderRect(forBounds: bounds)
        return bounds.inset(by: padding)
    }
    
    override func editingRect(forBounds bounds: CGRect) -> CGRect {
        super.editingRect(forBounds: bounds)
        return bounds.inset(by: padding)
    }
    
    override func rightViewRect(forBounds bounds: CGRect) -> CGRect {
        var rightViewRect = super.rightViewRect(forBounds: bounds)
        rightViewRect.origin.x -= 16
        return rightViewRect
    }
}
