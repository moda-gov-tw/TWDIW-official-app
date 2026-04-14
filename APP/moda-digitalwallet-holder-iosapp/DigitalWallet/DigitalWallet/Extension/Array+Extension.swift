//
//  Array+Extension.swift
//  DigitalWallet
//

import Foundation

extension Array where Element: NameSortableProtocol {
    func sortedByName(localeIdentifier: String = "en") -> [Element] {
            let locale = Locale(identifier: localeIdentifier)
            return self
                .filter { $0.name != nil }
                .sorted {
                    ($0.name ?? "").compare($1.name ?? "", locale: locale) == .orderedAscending
                }
        }
    
    func sortedByENThenZHTW() -> [Element] {
        var enList = self.filter({$0.name?.isFirstCharChinese == false})
        var zhtwList = self.filter({$0.name?.isFirstCharChinese == true})
        
        enList = enList.compactMap({$0}).sortedByName()
        zhtwList = zhtwList.compactMap({$0}).sortedByName(localeIdentifier: "zh_TW")
        
        return enList + zhtwList
    }
}

extension Array where Element: Equatable {
    func groupConsecutive() -> [[Element]] {
        return self.reduce(into: []) { result, element in
            // 如果結果是空的，或是當前元素不等於前一個 group 的最後一個元素
            if result.isEmpty || element != result.last?.last {
                // 就新增一個 group
                result.append([element])
            } else {
                // 否則，加到最後一個 group 裡
                result.append(result.removeLast() + [element])
            }
        }
    }
}
