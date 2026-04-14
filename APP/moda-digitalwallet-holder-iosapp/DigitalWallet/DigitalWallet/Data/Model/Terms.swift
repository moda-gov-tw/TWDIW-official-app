//
//  Terms.swift
//  DigitalWallet
//

import Foundation
import Combine

class Terms {
    let title: String
    let content: TermsType
    @Published var isAgree: Bool
    
    init(title: String, content: TermsType, isAgree: Bool) {
        self.title = title
        self.content = content
        self.isAgree = isAgree
    }
}
