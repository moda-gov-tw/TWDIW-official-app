//
//  TermsViewModel.swift
//  DigitalWallet
//

import Foundation
import Combine

class TermsViewModel {
    
    let terms: Terms
    let loadHTMLSubject = PassthroughSubject<TermsType, Never>()
    let finishSubject = PassthroughSubject<Void, Never>()
    var showButton: Bool = true
    
    init(terms: Terms, showButton: Bool = true) {
        self.terms = terms
        self.showButton = showButton
    }
    
    func loadLocalHTMLFile() {
        loadHTMLSubject.send(terms.content)
    }
    
    func agreeTerms() {
        terms.isAgree = true
        finishSubject.send()
    }
    
    func disagreeTerms() {
        terms.isAgree = false
        finishSubject.send()
    }
}
