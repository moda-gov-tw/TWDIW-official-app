//
//  UIFont+Extension.swift
//  DigitalWallet
//

import UIKit

extension UIFont {
    static func boldNotoSansTCFontOfSize(fontSize: CGFloat) -> UIFont {
        return UIFont(name: FontName.NotoSansTC_Bold.rawValue, size: fontSize)!
    }
    
    static func regularNotoSansTCFontOfSize(fontSize: CGFloat) -> UIFont {
        return UIFont(name: FontName.NotoSansTC_Regular.rawValue, size: fontSize)!
    }
}
