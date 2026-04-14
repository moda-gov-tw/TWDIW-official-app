//
//  TermsType.swift
//  DigitalWallet
//

import Foundation

enum TermsType: Equatable {
    case License
    case Privacy
    case VPAgreement(urlString: String?)
    
    var url: URL? {
        switch self {
        case .License:
            guard let path = Bundle.main.path(forResource: Config.FileResource.use_agreement_contract.rawValue, ofType: Config.FileType.html.rawValue) else { return nil }
            return URL(fileURLWithPath: path)
        case .Privacy:
            guard let path = Bundle.main.path(forResource: Config.FileResource.privacy_contract.rawValue, ofType: Config.FileType.html.rawValue) else { return nil }
            return URL(fileURLWithPath: path)
        case .VPAgreement(let urlString):
            guard let path = urlString else { return nil }
            return URL(string: path)
        }
    }
}
