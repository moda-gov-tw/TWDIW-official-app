//
//  UserVerifiableCredentailData.swift
//  DigitalWallet
//

import UIKit

class UserVerifiableCredentailData {
    let vc: ApplyVCDataResponse
    let cardId: UUID?
    let cardName: String?
    let iss: String?
    let orgName: String?
    var verificationStatus: CardStatus
    let createDate: Date?
    var cardImage: Data?
    /// 到期通知類型
    var remind: Int64?
    /// 到期時間
    var exp: Int64?
    var displayName: String?
    var updateDate: Date?
    var trustBadge: Bool
    
    init(vc: ApplyVCDataResponse,
         cardId: UUID?,
         cardName: String?,
         iss: String?,
         orgName: String?,
         verificationStatus: CardStatus,
         createDate: Date?,
         cardImage: Data? = nil,
         remind: Int64?,
         exp: Int64?,
         displayName: String? = nil,
         updateDate: Date? = nil,
         trustBadge: Bool = false) {
        self.vc = vc
        self.cardId = cardId
        self.cardName = cardName
        self.iss = iss
        self.orgName = orgName
        self.verificationStatus = verificationStatus
        self.createDate = createDate
        self.cardImage = cardImage
        self.remind = remind
        self.exp = exp
        self.displayName = displayName
        self.updateDate = updateDate
        self.trustBadge = trustBadge
    }
}

extension UserVerifiableCredentailData {
    func checkRemind() -> VerifyCardRemind {
        let calendar = Calendar.current
        guard let exp = self.exp,
              let remind = self.remind,
              let status = VerifyCardRemind(rawValue: remind) else {
            return .effective
        }
        let timeInterval = TimeInterval(exp)
        let date = Date(timeIntervalSince1970: timeInterval)
        let componentsDate = calendar.dateComponents([.day], from: Date(), to: date)
        let componentsDay = componentsDate.day ?? 0
        /// 判斷卡片是否已失效與判斷有沒有提醒過
        if self.verificationStatus == .invalidation && remind != VerifyCardRemind.invalid.rawValue {
            return .invalid
        }
        /// 判斷到期狀態
        if componentsDay < 0 && remind != VerifyCardRemind.invalid.rawValue {
            return .invalid
        } else if componentsDay < 1 && remind != VerifyCardRemind.invalid.rawValue && remind != VerifyCardRemind.oneDay.rawValue {
            return .oneDay
        } else if componentsDay < 7 && remind != VerifyCardRemind.invalid.rawValue && remind != VerifyCardRemind.oneDay.rawValue && remind != VerifyCardRemind.sevenDays.rawValue {
            return .sevenDays
        }
        return status
    }
}
