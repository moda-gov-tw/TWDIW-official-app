//
//  CardRecordViewModel.swift
//  DigitalWallet
//

import UIKit
import Combine

class CardRecordViewModel: NSObject {
    let recordSubject = PassthroughSubject<Array<CardRecord>, Never>()
    let repository: UserRepositoryProtocol
    let userVerifiableCredentialData: UserVerifiableCredentailData
    
    var sortCardNewToOld = true
    var record: [CardRecord] = []
    
    init(repository: UserRepositoryProtocol, verifiableCredential: UserVerifiableCredentailData){
        self.repository = repository
        self.userVerifiableCredentialData = verifiableCredential
    }
    
    func getCardRecord() {
        record.removeAll()
        let datas = repository.databaseManager.fetchCardRecords(vcId: userVerifiableCredentialData.cardId ?? UUID())
        record.append(contentsOf: datas)
        sortCards(isNewToOld: sortCardNewToOld)
    }
    
    /**Log紀錄排序*/
    func sortCards(isNewToOld: Bool){
        sortCardNewToOld = isNewToOld
        
        record = record.sorted(by: { firstCard, secondCard in
            guard let firstDate = firstCard.createDate,
                  let secondDate = secondCard.createDate else {
                return false
            }
            
            if sortCardNewToOld {
                return firstDate > secondDate
            } else {
                return firstDate < secondDate
            }
        })
        
        recordSubject.send(record)
    }
    
    func updateCardRecord(indexPath: IndexPath) {
        let updateRecord = record.enumerated().map { (row, record) -> CardRecord in
            var resultRecord = record
            if row == indexPath.row {
                resultRecord.isExpand = !resultRecord.isExpand
            }
            return resultRecord
        }
        record = updateRecord
        recordSubject.send(record)
    }
}
