//
//  Localization.swift
//  DigitalWallet
//

import Foundation

enum Localization {
    
    enum Welcome: String {
        case WelcomeVC_Title
        case WelcomeVC_Subtitle
        case WelcomeVC_Start
        
        case PreviewFeatureVC_Step1Title
        case PreviewFeatureVC_Step2Title
        case PreviewFeatureVC_Step3Title
        case PreviewFeatureVC_Step1Content
        case PreviewFeatureVC_Step2Content
        case PreviewFeatureVC_Step3Content
        
        var localizedString: String {
            get {
                NSLocalizedString(self.rawValue, comment: "")
            }
        }
    }
}
