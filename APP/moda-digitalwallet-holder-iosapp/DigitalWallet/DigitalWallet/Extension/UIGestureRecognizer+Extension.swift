//
//  UIGestureRecognizer+Extension.swift
//  DigitalWallet
//

import Combine
import UIKit

extension UIGestureRecognizer {
    var gesturePublisher: AnyPublisher<Void, Never> {
        self.publisher(for: \.state)
            .filter({ $0 == .recognized })
            .map({ _ in () })
            .eraseToAnyPublisher()
    }
}
