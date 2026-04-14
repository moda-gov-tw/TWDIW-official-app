//
//  CustomNavigationBarAppearance.swift
//  DigitalWallet
//

import UIKit

class CustomNavigationBarAppearance: UINavigationBarAppearance {
    
    required init(coder: NSCoder) {
        super.init(coder: coder)
        setupBarAppearance()
    }
    
    override init(idiom: UIUserInterfaceIdiom) {
        super.init(idiom: idiom)
        setupBarAppearance()
    }
    
    override init(barAppearance: UIBarAppearance) {
        super.init(barAppearance: barAppearance)
        setupBarAppearance()
    }
    
    /// 設定Bar的樣式
    private func setupBarAppearance() {
        // 設定背景顏色不透明,不模糊
        configureWithOpaqueBackground()
        backgroundColor = .white
        shadowColor = .clear
        titleTextAttributes = [
            .foregroundColor: UIColor._2_E_3_A_64,
            .font: UIFont.boldNotoSansTCFontOfSize(fontSize: 18)
        ]
        
        setBackIndicatorImage(UIImage(), transitionMaskImage: UIImage())
    }
}
