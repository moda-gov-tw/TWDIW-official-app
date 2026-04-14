//
//  Int+Extension.swift
//  DigitalWallet
//

import Foundation

extension Int {
    func toDateString() -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(self))
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy/MM/dd"
        formatter.timeZone = .current
        return formatter.string(from: date)
    }
}
