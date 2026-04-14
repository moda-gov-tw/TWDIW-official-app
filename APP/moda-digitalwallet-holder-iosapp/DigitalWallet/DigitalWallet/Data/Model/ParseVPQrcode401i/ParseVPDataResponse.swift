//
//  ParseVPDataResponse.swift
//  DigitalWallet
//

import Foundation

struct ParseVPDataResponse: Codable {
    
    var reqT: String
    
    var requestDatas: [RequestData]?
    
    var requestJon: RequestToken?
    
    var inTrustedList: Bool = true
    
    enum CodingKeys: String, CodingKey {
        case reqT = "request_token"
        case requestDatas = "request_data"
    }
    
    init(from decoder: any Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.reqT = try container.decode(String.self, forKey: .reqT)
        self.requestDatas = try container.decode([RequestData].self, forKey: .requestDatas)
        if let jwtData = try container.decode(String.self, forKey: .reqT).decodeJWT() {
            self.requestJon = try JSONDecoder().decode(RequestToken.self, from: jwtData)
        }
    }
}

struct RequestToken: Codable {
    let responseURI: String?
    let aud: String?
    let iss: String?
    let presentationDefinition: PresentationDefinition?
    let responseType: String?
    let state: String?
    let nonce: String?
    let clientMetadata: ClientMetadata?
    let clientID: String?
    let responseMode: String?
    
    enum CodingKeys: String, CodingKey {
        case responseURI = "response_uri"
        case aud
        case iss
        case presentationDefinition = "presentation_definition"
        case responseType = "response_type"
        case state
        case nonce
        case clientMetadata = "client_metadata"
        case clientID = "client_id"
        case responseMode = "response_mode"
    }
}

struct PresentationDefinition: Codable {
    
    struct Purpose: Codable {
        let client: String?
        let termsURI: String?
        let scenario: String?
        let purpose: String?
        
        enum CodingKeys: String, CodingKey {
            case client
            case termsURI = "terms_uri"
            case scenario
            case purpose
        }
    }
    
    let id: String?
    let purpose: Purpose?
    let inputDescriptors: [InputDescriptor]?
    
    enum CodingKeys: String, CodingKey {
        case id
        case purpose
        case inputDescriptors = "input_descriptors"
    }
    
    init(from decoder: Decoder) throws {
            let container = try decoder.container(keyedBy: CodingKeys.self)
            id = try container.decode(String.self, forKey: .id)
            inputDescriptors = try container.decode([InputDescriptor].self, forKey: .inputDescriptors)

            // 將 purpose 欄位從 JSON 字串轉換成 Purpose 結構
            let purposeJSONString = try container.decode(String.self, forKey: .purpose)
            if let data = purposeJSONString.data(using: .utf8) {
                purpose = try? JSONDecoder().decode(Purpose.self, from: data)
            } else {
                purpose = nil
            }
        }
}

struct InputDescriptor: Codable {
    let id: String?
    let name: String?
    let constraints: Constraints?
}

struct Constraints: Codable {
    let fields: [Field]?
    let limitDisclosure: String?

    enum CodingKeys: String, CodingKey {
        case fields
        case limitDisclosure = "limit_disclosure"
    }
}

struct Field: Codable {
    let path: [String]?
    let filter: Filter?

    struct Filter: Codable {
        let contains: Contains?
        let type: String?

        struct Contains: Codable {
            let `const`: String
        }
    }
}

struct ClientMetadata: Codable {
    let jwksURI: String?
    let vpFormats: VPFormats?
    let responseTypes: [String]?

    enum CodingKeys: String, CodingKey {
        case jwksURI = "jwks_uri"
        case vpFormats = "vp_formats"
        case responseTypes = "response_types"
    }
}

struct VPFormats: Codable {
    let jwtVC: Algorithm?
    let jwtVP: Algorithm?

    enum CodingKeys: String, CodingKey {
        case jwtVC = "jwt_vc"
        case jwtVP = "jwt_vp"
    }
}

struct Algorithm: Codable {
    let alg: [String]?
}

struct RequestData: Codable {
    /// 群組名稱
    var name: String?
    /// 群組分類
    var group: String?
    /// pick 跟 all
    var rule: String?
    /// `rule = pick` 時選幾張卡片
    var count: Int?
    /// `rule = pick` 時選幾張卡片
    var max: Int?
    /// 卡片列表
    var cards: [VirtualCard]?
    
    enum CodingKeys: CodingKey {
        case name
        case group
        case rule
        case count
        case max
        case cards
    }
}


struct VirtualCard: Codable {
    /// 卡片?
    var card: String?
    /// 卡片ID
    var cardId: String?
    /// 卡片資料
    var info: String?
    /// 驗證欄位
    var fields: [String]
    
    enum CodingKeys: String, CodingKey {
        case card = "card"
        case cardId = "card_id"
        case info = "name"
        case fields
    }
    
    /// 卡片資料結構
    var infoItem: VirtualCardInfo? {
        if let info = info,
           let jsonData = info.data(using: .utf8) {
            return try? JSONDecoder().decode(VirtualCardInfo.self, from: jsonData)
        }
        return nil
    }
}

/// `VirtualCard` 如果為結構時的物件
struct VirtualCardInfo: Codable {
    /// 組織名稱
    var orgName: String?
    /// VC名稱
    var vcName: String?
    
    enum CodingKeys: String, CodingKey {
        case orgName = "org_tw_name"
        case vcName = "vc_name"
    }
}
