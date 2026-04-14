//
//  VerifiablePresentationTableFooterView.swift
//  DigitalWallet
//

import UIKit

class VerifiablePresentationTableFooterView: UIView {

    override init(frame: CGRect) {
        super.init(frame: frame)
        loadNib(VerifiablePresentationTableFooterView.self)
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        loadNib(VerifiablePresentationTableFooterView.self)
    }

}
