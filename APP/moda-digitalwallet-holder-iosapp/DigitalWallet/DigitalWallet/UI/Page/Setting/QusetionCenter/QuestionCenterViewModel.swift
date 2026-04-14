//
//  QuestionCenterViewModel.swift
//  DigitalWallet
//

import UIKit

class QuestionCenterViewModel: NSObject {
    
    var questionCenterItems: [SettingItem] {
        get {
            var items: [SettingItem] = []
            
            items.append(SettingItem(itemName: NSLocalizedString("FrequentlyAskedQuestions", comment: "常見問題"), type: .commonQuestions, styleType: .normal))
            items.append(SettingItem(itemName: NSLocalizedString("ProblemReport", comment: "問題回報"), type: .problemReport, styleType: .normal))
            items.append(SettingItem(itemName: NSLocalizedString("ContactCustomerService", comment: "聯絡客服"), content: NSLocalizedString("ServiceMessage", comment: ""), content2: NSLocalizedString("ServiceEmail", comment: ""), type: .contactCustomerService, styleType: .normal))
            return items
        }
    }
}
