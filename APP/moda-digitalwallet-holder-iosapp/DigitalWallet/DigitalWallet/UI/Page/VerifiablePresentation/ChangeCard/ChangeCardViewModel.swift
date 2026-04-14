//
//  ChangeCardViewModel.swift
//  DigitalWallet
//

import Foundation
import Combine

class ChangeCardViewModel {
    let repository: VerifiablePresentationRepositoryProtocol
    /// 最大選擇
    var limitCount: Int = 1
    /// 原始群組資料
    var verifyGroup: VerifyGroupData
    /// 顯示及操作用資料
    var verifyGroupCopy: VerifyGroupData
    /// 是否開啟眼睛
    var eyeOpen: Bool = true
    /// 是否展開
    var expand: Bool = false
    /// 卡片列表
    var changeCardItems: [ChangeCardCellType] = []
    
    /// 刷新畫面
    var refreshUISubject = PassthroughSubject<Void, Never>()
    
    var cancelSet = Set<AnyCancellable>()
    
    /// 1.  `Rule` 是否為 `Pick`
    /// 2.  `count` 是否小於卡片已選擇的數量
    var canSelect: Bool {
        return verifyGroupCopy.cards.filter({$0.isSelect}).count < limitCount
    }
    
    init(repository: VerifiablePresentationRepositoryProtocol,
         verifyGroup: VerifyGroupData,
         verifyGroupCopy: VerifyGroupData) {
        self.repository = repository
        self.verifyGroup = verifyGroup
        self.verifyGroupCopy = verifyGroupCopy
        updateLimitCount()
        updateChangeCardItems()
    }
    
    /// 更新最大數量
    private func updateLimitCount() {
        if verifyGroup.rule == .all {
            limitCount = verifyGroup.requestCards.count
        } else {
            limitCount = verifyGroup.count ?? verifyGroup.requestCards.count
        }
    }
    
    /// 更新列表
    private func updateChangeCardItems() {
        setExpand(false)
        /// 提示文字
        let totalCount = verifyGroupCopy.rule == .pick ? verifyGroupCopy.count ?? 0 : verifyGroupCopy.requestCards.count
        let message = String(format: NSLocalizedString("SelectCardMessage", comment: ""), "\(totalCount)")
        changeCardItems.append(.hintMessage(message: message))
        
        /// 分組內容
        let groups = verifyGroupCopy.cards.reduce(into: [(String?, [CardInfoData])]()) { result, infoData in
            if let index = result.firstIndex(where: {$0.0 == infoData.cardId}) {
                result[index].1.append(infoData)
            } else {
                result.append((infoData.cardId, [infoData]))
            }
        }
        
        groups.forEach({
            changeCardItems.append(.cardGroup(cards: $0.1))
        })
    }
    
    /// 取得選擇文字
    func getCount() -> String {
        /// Group內卡片選擇筆數
        let count = verifyGroupCopy.cards.filter { $0.isSelect }.count

        return String(format: NSLocalizedString("VerifyCardSelected", comment: "已選擇 %d 張"),count)
    }
    
    /// 檢查當下資料是否在禁止點擊清單中
    /// - Parameter cards: 卡片資料
    /// - Returns: 是否可以點擊
    func checkEnable(_ cards: [CardInfoData]) -> Bool {
        /// 已選擇的列表
        let selectList = verifyGroupCopy.cards.filter({$0.isSelect})
        /// 是以選中的列表
        let isSelectItem = selectList.contains(where: {$0.cardId == cards.first?.cardId})
        /// 還可以選擇
        let canSelect = selectList.count < limitCount
        return isSelectItem || canSelect
    }
    
    /// 檢查選擇內容
    /// - Parameter cardInfo: 選中的卡片資料
    func checkSelect(cardInfo: CardInfoData) {
        /// 已選擇的列表
        let selectList = verifyGroupCopy.cards.filter({$0.isSelect})
        /// 是編輯卡片
        let isEdit = selectList.contains(where: {$0.cardId == cardInfo.cardId})
        
        let changeGroup = verifyGroupCopy.cards.filter({$0.cardId == cardInfo.cardId})
        if isEdit {
            /// 1. 找出要調整的卡片群組
            changeGroup.forEach { cardInfoData in
                if cardInfoData.card.cardId == cardInfo.card.cardId {
                    cardInfoData.isSelect.toggle()
                } else {
                    cardInfoData.isSelect = false
                }
            }
        } else {
            if selectList.count < limitCount {
                changeGroup.forEach { cardInfoData in
                    if cardInfoData.card.cardId == cardInfo.card.cardId {
                        cardInfoData.isSelect.toggle()
                    }
                }
            } else {
                return
            }
        }
        
        /// 把已選擇的卡片所有欄位預設勾選
        verifyGroupCopy.cards
            .filter({$0.isSelect})
            .forEach({$0.infos.forEach({$0.isSelect = true})})
        
        refreshUISubject.send()
    }
    
    /// 完成更新卡片
    func finishChangeCardUpdate() {
        setExpand(false)
        verifyGroup.cards = verifyGroupCopy.cards
    }
    
    /// 設定展開收合
    /// - Parameter expand: 是否展開
    func setExpand(_ expand: Bool) {
        verifyGroupCopy.isExpand = expand
        verifyGroupCopy.cards.forEach({ $0.isExpand = expand })
    }
    
    /// 更新展開按鈕的選擇狀態
    func updateExpandButtonSelect() -> Bool {
        let isPick = verifyGroup.rule == .pick
        let selectCardsCount = verifyGroupCopy.cards.filter({$0.isSelect}).count
        var isMaxSelect: Bool = isPick ? !(verifyGroup.count ?? 0 > selectCardsCount) : !(verifyGroup.cards.count > selectCardsCount)
        let result = isMaxSelect ? !verifyGroupCopy.cards
            .filter({$0.isSelect})
            .contains(where: {!$0.isExpand}) : !verifyGroupCopy.cards
            .contains(where: {!$0.isExpand})
         
        return result
    }
}
