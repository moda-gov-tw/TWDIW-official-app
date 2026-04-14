//
//  TimeSelectEnum.swift
//  DigitalWallet
//

import Foundation

enum TimeSelectEnum: Int, CaseIterable{
    case Minute3 = 3
    case Minute5 = 5
    case Minute10 = 10
    case Minute15 = 15
    case Permanent = 0
    
    var itemName: String{
        switch self {
            case .Minute3: NSLocalizedString("Minute3", comment: "")
            case .Minute5: NSLocalizedString("Minute5", comment: "")
            case .Minute10: NSLocalizedString("Minute10", comment: "")
            case .Minute15: NSLocalizedString("Minute15", comment: "")
            case .Permanent: NSLocalizedString("Permanent", comment: "")
        }
    }
}
