//
//  UserConsentViewModel.swift
//  DigitalWallet
//

import Foundation
import Combine

class UserConsentViewModel {
    
    let terms: [Terms]
    let showTermsSubject = PassthroughSubject<Terms, Never>()
    var cancelSet = Set<AnyCancellable>()
    
    init(terms: [Terms]) {
        self.terms = terms
    }
    
    func checkoutTerms(type: TermsType) {
        if let licenseTerms = terms.first(where: { terms in
            terms.content == type
        }) {
            showTermsSubject.send(licenseTerms)
        }
    }
    
    func checkTerms(type: TermsType, isAgree: Bool) {
        terms.forEach { terms in
            if terms.content == type {
                terms.isAgree = isAgree
            }
        }
    }
}
