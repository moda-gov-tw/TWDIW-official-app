//
//  VerifiableData.swift
//  DigitalWallet
//

import Foundation

/// 401i 解析後組合的資料
class VerifiableData {
    /// 是否在信任清單中
    var inTrustedList: Bool
    /// 驗證卡片群組列表
    let groupList: [VerifyGroupData]
    
    init(inTrustedList: Bool, groupList: [VerifyGroupData]) {
        self.inTrustedList = inTrustedList
        self.groupList = groupList
    }
}

/// 驗證卡片群組資料
class VerifyGroupData {
    /// 群組名稱
    let groupName: String?
    /// 規則 pick 跟 all
    let rule: VerifyCardRule?
    /// `rule = pick` 時選幾張卡片
    let count: Int?
    /// 卡片列表
    var cards: [CardInfoData]
    /// 需求的卡片列表
    let requestCards: [VirtualCard]
    /// 最大數量
    let limitCount: Int
    /// 是否展開
    var isExpand: Bool
    /// 群組狀態
    var status: VerifiableCardStatus {
        let wrongPick = (rule == .pick) && limitCount < count ?? 0
        let wrongAll = (rule == .all) && requestCards.count > cards.filter({$0.isSelect}).count
        let wrongRules = wrongAll || wrongPick
        if cards.isEmpty || wrongRules {
            return .noAuthorized
        } else if cards.filter({ $0.isSelect }).isEmpty {
            return .unselect
        } else {
            return .authorized
        }
    }
    /// 卡片資料是否都授權
    var isAuthorizeAllCards: Bool {
        return !cards.contains(where: {!$0.isAuthorizeAllInfo})
    }
    
    /// 全選
    var isAllCardSelect: Bool {
        return cards.filter({$0.isSelect}).count == requestCards.count
    }
    
    init(groupName: String? = nil,
         isExpand: Bool = true,
         cards: [CardInfoData],
         requestCards: [VirtualCard],
         limitCount: Int,
         rule: VerifyCardRule? = nil,
         count: Int? = nil) {
        self.groupName = groupName
        self.isExpand = isExpand
        self.cards = cards
        self.requestCards = requestCards
        self.limitCount = limitCount
        self.rule = rule
        self.count = count
    }
}

extension VerifyGroupData {
    /// 驗證卡片群組資料副本
    func copy() -> VerifyGroupData {
        let copy = VerifyGroupData(groupName:self.groupName,
                                     isExpand:self.isExpand,
                                   cards:self.cards.map({CardInfoData(cardUUID: $0.cardUUID,
                                                                      cardId: $0.cardId,
                                                                      cardName: $0.cardName,
                                                                      card: $0.card,
                                                                      infos: $0.infos,
                                                                      isSelect: $0.isSelect)}),
                                     requestCards:self.requestCards,
                                     limitCount:self.limitCount,
                                     rule:self.rule,
                                     count:self.count)
        copy.isExpand = self.isExpand
        return copy
    }
}

class CardInfoData {
    /// 卡片UUID
    let cardUUID: UUID?
    /// 卡片Id
    let cardId: String?
    /// 卡片名稱
    let cardName: String?
    /// 卡片資訊
    let card: UserVerifiableCredentailData
    /// 授權內容項目
    let infos: [CardRequirement]
    /// 是否展開
    var isExpand: Bool
    /// 是否選擇
    var isSelect: Bool
    /// 是否授權全部內容項目
    var isAuthorizeAllInfo: Bool {
        return !infos.contains(where: { !$0.isSelect })
    }
    
    /// 卡片資訊
    var infoMessage: String? {
        infos.filter({ $0.isSelect })
            .compactMap({ $0.name })
            .joined(separator: "、")
    }
    
    /// 選項內容資訊
    var infoContent: String? {
        infos
            .compactMap({ $0.value })
            .filter({ !$0.isEmpty })
            .joined(separator: "、")
    }
    
    init(cardUUID: UUID?,
         cardId: String?,
         cardName: String?,
         card: UserVerifiableCredentailData,
         infos: [CardRequirement],
         isExpand: Bool = false,
         isSelect: Bool = true) {
        self.cardUUID = cardUUID
        self.cardId = cardId
        self.cardName = cardName
        self.card = card
        self.infos = infos
        self.isExpand = isExpand
        self.isSelect = isSelect
    }
}

class CardRequirement {
    let id: String?
    /// 授權項目名稱
    let name: String?
    /// 授權項目內容
    let value: String?
    /// 是否選擇
    var isSelect: Bool
    
    init(id: String?, name: String?, value: String?, isSelect: Bool = true) {
        self.id = id
        self.name = name
        self.value = value
        self.isSelect = isSelect
    }
}
