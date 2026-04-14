//
//  CALayer+Extension.swift
//  DigitalWallet
//

import UIKit

extension CALayer {
    func addMaskedCorners(radius: CGFloat, corners: CACornerMask) {
        self.cornerRadius = radius
        self.maskedCorners = corners
    }
}
