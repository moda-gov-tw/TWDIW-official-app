//
//  FontName.swift
//  DigitalWallet
//

import Foundation
import UIKit

enum FontName: String {
    /// `weight` `900`
    case NotoSansTC_Black = "NotoSansTC-Black"
    /// `weight` `800`
    case NotoSansTC_ExtraBold = "NotoSansTC-ExtraBold"
    /// `weight` `700`
    case NotoSansTC_Bold = "NotoSansTC-Bold"
    /// `weight` `600`
    case NotoSansTC_SemiBold = "NotoSansTC-SemiBold"
    /// `weight` `500`
    case NotoSansTC_Medium = "NotoSansTC-Medium"
    /// `weight` `400`
    case NotoSansTC_Regular = "NotoSansTC-Regular"
    /// `weight` `300`
    case NotoSansTC_Light = "NotoSansTC-Light"
    /// `weight` `200`
    case NotoSansTC_ExtraLight = "NotoSansTC-ExtraLight"
    /// `weight` `100`
    case NotoSansTC_Thin = "NotoSansTC_Thin"
    
    /// 預設 `14.0`
    var font: UIFont {
        UIFont(name: self.rawValue, size: 14.0)!
    }
}
