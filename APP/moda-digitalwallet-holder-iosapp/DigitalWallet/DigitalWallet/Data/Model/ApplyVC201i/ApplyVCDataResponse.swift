//
//  ApplyVCDataResponse.swift
//  DigitalWallet
//

import Foundation

struct ApplyVCDataResponse: Codable {
    var credential: String
    var credentialDefinition: CredentialDefinition
    
    enum CodingKeys: String, CodingKey {
        case credential = "credential"
        case credentialDefinition = "credentialDefinition"
    }
}

struct CredentialDefinition: Codable {
    var format: String
    var scope: String
    var cryptographicBindingMethodsSupported: [String]
    var credentialSigningAlgValuesSupported: [String]
    var credentialDefinition: CredentialDefinitionDetail
    var proofTypesSupported: ProofTypesSupported
    var display: [Display]
    
    enum CodingKeys: String, CodingKey {
        case format = "format"
        case scope = "scope"
        case cryptographicBindingMethodsSupported = "cryptographic_binding_methods_supported"
        case credentialSigningAlgValuesSupported = "credential_signing_alg_values_supported"
        case credentialDefinition = "credential_definition"
        case proofTypesSupported = "proof_types_supported"
        case display = "display"
    }
}

struct CredentialDefinitionDetail: Codable{
    var type: [String]
    var credentialSubject: Dictionary<String, CredentialSubjectDetail>
    
    enum CodingKeys: String, CodingKey {
        case type = "type"
        case credentialSubject = "credentialSubject"
    }
}

struct ProofTypesSupported: Codable{
    var jwt: ProofSigningAlgValuesSupported
    
    enum CodingKeys: String, CodingKey {
        case jwt = "jwt"
    }
}

struct ProofSigningAlgValuesSupported: Codable{
    var proofSigningAlgValuesSupported: [String]
    
    enum CodingKeys: String, CodingKey {
        case proofSigningAlgValuesSupported = "proof_signing_alg_values_supported"
    }
}

struct CredentialSubjectDetail: Codable {
    var mandatory: Bool
    var valueType: String
    var display: [Display]
    
    enum CodingKeys: String, CodingKey {
        case mandatory = "mandatory"
        case valueType = "value_type"
        case display = "display"
    }
    
    struct Display: Codable {
        var name: String
        var locale: String
        
        enum CodingKeys: CodingKey {
            case name
            case locale
        }
    }
}

struct Display: Codable {
    var name: String
    var locale: String
    var description: Description?
    var backgroundImage: BackgroundImage?
    
    enum CodingKeys: String, CodingKey {
        case name = "name"
        case locale = "locale"
        case description = "description"
        case backgroundImage = "background_image"
    }
    
    init(from decoder: any Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.name = try container.decode(String.self, forKey: .name)
        self.locale = try container.decode(String.self, forKey: .locale)
        let descriptionJsonString = try? container.decode(String.self, forKey: .description)
        if let descriptionData = descriptionJsonString?.data(using: .utf8) {
            self.description = try JSONDecoder().decode(Description.self, from: descriptionData)
        } else {
            self.description = nil
        }
        self.backgroundImage = try container.decodeIfPresent(BackgroundImage.self, forKey: .backgroundImage)
    }
    
    func encode(to encoder: any Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(self.name, forKey: .name)
        try container.encode(self.locale, forKey: .locale)
        if let description = description {
            let jsonData = try JSONEncoder().encode(description)
            if let jsonString = String(data: jsonData, encoding: .utf8) {
                try container.encode(jsonString, forKey: .description)
            }
        }
        try container.encodeIfPresent(self.backgroundImage, forKey: .backgroundImage)
    }
}

struct BackgroundImage: Codable {
    var uri: String
    
    enum CodingKeys: String, CodingKey {
        case uri = "uri"
    }
}

struct Description: Codable {
    var issuerUrl: String?
    var ial: String?
    
    enum CodingKeys: String, CodingKey {
        case issuerUrl = "issuer_url"
        case ial = "ial"
    }
}
