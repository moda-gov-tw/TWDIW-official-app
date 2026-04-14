//
//  CustomTextField.swift
//  DigitalWallet
//

import UIKit

class SearchTextField: UITextField {
    
    var leftIconSize: CGFloat = 24
    var rightIconSize: CGFloat = 20
    var textPadding: UIEdgeInsets = .zero {
        didSet {
            setNeedsDisplay()
        }
    }
    
    private let padding = UIEdgeInsets(top: 12,
                                       left: 16,
                                       bottom: 12,
                                       right: 16)

    override init(frame: CGRect) {
        super.init(frame: frame)
        initView()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        initView()
    }
    
    override func textRect(forBounds bounds: CGRect) -> CGRect {
        super.textRect(forBounds: bounds)
        return bounds.inset(by: textPadding)
    }
    
    override func placeholderRect(forBounds bounds: CGRect) -> CGRect {
        super.placeholderRect(forBounds: bounds)
        return bounds.inset(by: textPadding)
    }
    
    override func editingRect(forBounds bounds: CGRect) -> CGRect {
        super.editingRect(forBounds: bounds)
        return bounds.inset(by: textPadding)
    }
    
    override func leftViewRect(forBounds bounds: CGRect) -> CGRect {
        super.leftViewRect(forBounds: bounds)
        return CGRect(x: padding.left, y: padding.top, width: leftIconSize, height: leftIconSize)
    }
    
    override func rightViewRect(forBounds bounds: CGRect) -> CGRect {
        super.rightViewRect(forBounds: bounds)
        let x = bounds.width - rightIconSize - padding.right
        let y = (bounds.height - rightIconSize) / 2
        return CGRect(x: x, y: y, width: rightIconSize, height: rightIconSize)
    }
    
    func setBorderColor(borderColor: CGColor) {
        layer.borderColor = borderColor
    }
    
    private func initView() {
        layer.masksToBounds = true
        layer.borderWidth = 1.0
        layer.cornerRadius = 16.0
    }
    
}

extension SearchTextField {
    /// - Parameter image: 設定的圖片
    func setLeftImage(_ image: UIImage) {
        let leftImageView = UIImageView(image: image)
        leftImageView.contentMode = .scaleAspectFit
        leftView = leftImageView
        leftViewMode = .always
    }
    
    /// - Parameter image: 設定的圖片
    func setRightImage(_ image: UIImage) {
        let rightImageView = UIImageView(image: image)
        rightImageView.contentMode = .scaleAspectFit
        rightView = rightImageView
        rightViewMode = .always
    }
    
    /// 設定PlaceHolder
    /// - Parameters:
    ///   - placeHolder: 文字內容
    ///   - font: 字型
    ///   - color: 顏色
    func setAttributedPlaceHolder(_ placeHolder: String, font: UIFont, color: UIColor) {
        attributedPlaceholder = NSAttributedString(string: placeHolder, attributes: [
            .font : font,
            .foregroundColor : color
        ])
    }
}
