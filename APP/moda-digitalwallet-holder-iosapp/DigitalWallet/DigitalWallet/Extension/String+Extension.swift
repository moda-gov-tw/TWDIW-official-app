//
//  String+Extension.swift
//  DigitalWallet
//

import Foundation
import UIKit

extension String {
    /// 解析 JWT 字串並回傳 payload 的 base64-decoded 資料
    func decodeJWT() -> Data? {
        let segments = self.split(separator: ".")
        guard segments.count == 3 else {
            return nil
        }
        
        let payloadSegment = segments[1]
        var base64 = payloadSegment
            .replacingOccurrences(of: "-", with: "+")
            .replacingOccurrences(of: "_", with: "/")
        
        while base64.count % 4 != 0 {
            base64.append("=")
        }
        
        return Data(base64Encoded: base64)
    }
    
    /// 檢查第一個字是否為中文
    var isFirstCharChinese: Bool {
        guard let scalar = self.first?.unicodeScalars.first else { return false }
        return (0x4E00...0x9FFF).contains(scalar.value)
    }
    
    /// 透過文字字體及畫面寬度計算高度
    /// - Parameters:
    ///   - font: 字體
    ///   - width: 元件寬度
    func calculateTextHeight(font: UIFont, width: CGFloat) -> CGFloat {
        let attributes: [NSAttributedString.Key: Any] = [
            .font: font
        ]
        let size = CGSize(width: width, height: .greatestFiniteMagnitude)
        let rect = self.boundingRect(
            with: size,
            options: .usesLineFragmentOrigin,
            attributes: attributes,
            context: nil
        )
        return ceil(rect.height)
    }
}
