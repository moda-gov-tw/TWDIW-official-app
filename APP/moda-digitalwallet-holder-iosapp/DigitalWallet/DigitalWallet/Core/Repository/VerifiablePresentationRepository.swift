//
//  VerifiablePresentation.swift
//  DigitalWallet
//

import Foundation
import Combine

class VerifiablePresentationRepository: VerifiablePresentationRepositoryProtocol {
    var userId: UUID
    let verifiableManager: VerifiableManagerProtocol
    let databaseManager: DatabaseModelProtocol
    let verifyResp: VerifiableData
    let parseVPData: ParseVPDataResponse
    var client: String
    var termsURI: String
    var scenario: String?
    var purpose: String
    let wallet: MyWallet
    
    let finishSelectedFieldSubject = PassthroughSubject<Array<(id: String, name: String)>?, Never>()
    
    init(userId: UUID, verifiableManager: VerifiableManagerProtocol, databaseManager: DatabaseModelProtocol, verifyResp: VerifiableData, parseVPData: ParseVPDataResponse, wallet: MyWallet) {

        self.userId = userId
        self.verifiableManager = verifiableManager
        self.databaseManager = databaseManager
        self.verifyResp = verifyResp
        self.parseVPData = parseVPData
        self.client = parseVPData.requestJon?.presentationDefinition?.purpose?.client ?? ""
        self.termsURI = parseVPData.requestJon?.presentationDefinition?.purpose?.termsURI ?? ""
        self.scenario = parseVPData.requestJon?.presentationDefinition?.purpose?.scenario
        self.purpose = parseVPData.requestJon?.presentationDefinition?.purpose?.purpose ?? ""
        self.wallet = wallet
    }
    
    func verifyVP(customData: VPCustomData?) async throws -> Bool {
        var selectVCs: [SelectVC] = []
        verifyResp.groupList.forEach { groupData in
            let groupSelectVCS = groupData.cards
                .filter({$0.isSelect})
                .compactMap { requirement -> SelectVC? in
                    let field = requirement.infos.compactMap { selected in
                        return selected.isSelect ? selected.id : nil
                    }
                    if field.isEmpty {
                        return nil
                    }
                    return SelectVC(vc: requirement.card.vc.credential, field: field, cardId: requirement.cardId)
                }
            selectVCs.append(contentsOf: groupSelectVCS)
        }
        
        
        guard let didDataString = wallet.didFile else {
            throw SDKError.LocalDataIsEmpty
        }
        return try await verifiableManager.verifyVerifiablePresentationWithKx(parseData: parseVPData, selectVCs: selectVCs, didDataString: didDataString, customData: customData)
    }
    
    func saveVerifySuccessRecord(customData: VPCustomData?) {
        verifyResp.groupList.forEach { group in
            group.cards.filter({ card in
                card.isSelect && card.infos.contains(where: {$0.isSelect})
            }).forEach { requirement in
                guard let vcUUID = requirement.card.cardId else { return }
                let applyInfos = requirement.infos.compactMap({ info in
                    return info.name
                }).joined(separator: "、")
                databaseManager.saveCredentialRecord(uid: UUID(),
                                                     walletId: wallet.uuid,
                                                     vcId: vcUUID,
                                                     text: scenario ?? "",
                                                     authorizationUnit: client,
                                                     authorizationPurpose: purpose,
                                                     authorizationField: applyInfos,
                                                     datetime: Date())
            }
        }
        
        var vcIdsSet: Set<String> = []
        var vcNamesSet: Set<String> = []
        var authorizationFieldsSet: Set<String> = []
        
        verifyResp.groupList.forEach { group in
            
            let vcIds = group.cards
                .filter({$0.isSelect})
                .compactMap({$0.cardUUID?.uuidString})
            vcIdsSet.formUnion(vcIds)
            
            let vcNames = group.cards
                .filter({$0.isSelect})
                .compactMap({$0.card.cardName})
            vcNamesSet.formUnion(vcNames)
            
            group.cards
                .filter({$0.isSelect})
                .forEach({
                    let infos = $0.infos.filter({$0.isSelect}).compactMap({$0.name})
                    authorizationFieldsSet.formUnion(infos)
                })
        }
        
        let vcIds = vcIdsSet.joined(separator: "、")
        let vcNames = vcNamesSet.joined(separator: "、")
        var authorizationFields = authorizationFieldsSet.joined(separator: "、")
        
        if let customData = customData?.customData {
            let customField = customData.compactMap({$0.cname})
            if !customField.isEmpty {
                authorizationFields.append("、")
                authorizationFields.append(contentsOf: customField.joined(separator: "、"))
            }
        }
        
        databaseManager.savePresentationRecord(uid: UUID(),
                                               walletId: wallet.uuid,
                                               text: scenario ?? "",
                                               vcIds: vcIds,
                                               vcNames: vcNames,
                                               authorizationUnit: client,
                                               authorizationPurpose: purpose,
                                               authorizationFields: authorizationFields,
                                               datetime: Date())
    }
}
