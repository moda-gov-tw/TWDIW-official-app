//
//  VerifiablePresentationRepositoryProtocol.swift
//  DigitalWallet
//

import Foundation
import Combine

protocol VerifiablePresentationRepositoryProtocol {
    var userId: UUID { get }
    var verifiableManager: VerifiableManagerProtocol { get }
    var verifyResp: VerifiableData { get }
    var parseVPData: ParseVPDataResponse { get }
    var termsURI: String { get }
    /// 授權單位
    var client: String { get }
    /// 授權名稱
    var scenario: String? { get }
    /// 授權目的
    var purpose: String { get }
    var finishSelectedFieldSubject: PassthroughSubject<Array<(id: String, name: String)>?, Never> { get }
    
    func verifyVP(customData: VPCustomData?) async throws -> Bool
    func saveVerifySuccessRecord(customData: VPCustomData?)
}

struct MatchRequireData {
    let vpRequirement: RequestData
    let matchVCData: UserVerifiableCredentailData
    let matchDecodeVCData: DecodeVCDataResponse
}
