//
//  VerifivationResultsViewModel.swift
//  DigitalWallet
//

import Foundation

class VerifivationResultsViewModel: BaseViewModel {
    enum VerifiablePresentationResultCellType {
        case verifyData(VerifyGroupData)
        case customFields(DwModa201iCustomField)
    }
    
    let repository: VerifiablePresentationRepositoryProtocol
    let resultType: VerifiablePresentationResult
    var customFields: [DwModa201iCustomField] = []
    
    lazy var title: String = {
        repository.client
    }()
    
    var resultList: [VerifiablePresentationResultCellType] {
        var result: [VerifiablePresentationResultCellType] = []
        
        let verifyDataList = repository.verifyResp.groupList.filter({
            $0.cards.contains { card in
                card.isSelect
            }
        })
        result.append(contentsOf: verifyDataList.map({.verifyData($0)}))
        result.append(contentsOf: customFields.map({.customFields($0)}))
        return result
    }
    
    init(repository: VerifiablePresentationRepositoryProtocol,
         resultType: VerifiablePresentationResult,
         customFields: [DwModa201iCustomField]) {
        self.repository = repository
        self.resultType = resultType
        self.customFields = customFields
    }
}
