//
//  NSMutableAttributeString+Extension.swift
//  DigitalWallet
//

import UIKit

extension NSMutableAttributedString {
    convenience init(format: String,
                     parts: [String],
                     font: UIFont,
                     customFont: UIFont? = nil,
                     color: UIColor,
                     customColor: UIColor? = nil) {
        self.init(string: format, attributes: [.font: font, .foregroundColor: color])
        parts.forEach { part in
            if let range = self.string.range(of: part) {
                let nsRange = NSRange(range, in: self.string)
                self.addAttributes([.foregroundColor: customColor ?? color, .font: customFont ?? font], range: nsRange)
            }
        }
    }
}
