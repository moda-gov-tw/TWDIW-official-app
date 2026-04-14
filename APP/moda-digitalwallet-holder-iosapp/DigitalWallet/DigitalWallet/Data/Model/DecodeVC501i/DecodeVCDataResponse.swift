//
//  DecodeVCDataResponse.swift
//  DigitalWallet
//

import Foundation

struct DecodeVCDataResponse: Codable {
    /***/
    var sub: String?
    /***/
    var nbf: Int64?
    /***/
    var iss: String?
    /***/
    var cnf: Cnf?
    /***/
    var exp: Int64?
    /***/
    var vc: VC?
    /***/
    var nonce: String?
    /***/
    var jti: String?
    
    enum CodingKeys: String, CodingKey {
        case sub = "sub"
        case nbf = "nbf"
        case iss = "iss"
        case cnf = "cnf"
        case exp = "exp"
        case vc = "vc"
        case nonce = "nonce"
        case jti = "jti"
    }
    
    init(dictionary: NSDictionary?, orgJsonString: String) {
        if let dictionary = dictionary {
            self.sub = dictionary["sub"] as? String
            self.nbf = dictionary["nbf"] as? Int64
            self.iss = dictionary["iss"] as? String
            let cnfDic = dictionary["cnf"] as? NSDictionary
            self.cnf = Cnf(dictionary: cnfDic)
            self.exp = dictionary["exp"] as? Int64
            
            let vcDic = dictionary["vc"] as? NSDictionary
            self.vc = VC(dictionary: vcDic, orgJsonString: orgJsonString)
            
            self.nonce = dictionary["nonce"] as? String
            self.jti = dictionary["jti"] as? String
        }
    }
}

struct Cnf: Codable {
    /***/
    var jwk: JWK?
    
    enum CodingKeys: String, CodingKey {
        case jwk = "jwk"
    }
    
    init(dictionary: NSDictionary?) {
        if let dictionary = dictionary {
            let jwkDic = dictionary["jwk"] as? NSDictionary
            self.jwk = JWK(dictionary: jwkDic)
        }
    }
}

struct JWK: Codable{
    /***/
    var crv: String?
    /***/
    var kty: String?
    /***/
    var x: String?
    /***/
    var y: String?
    
    enum CodingKeys: String, CodingKey {
        case crv = "crv"
        case kty = "kty"
        case x = "x"
        case y = "y"
    }
    
    init(dictionary: NSDictionary?) {
        if let dictionary = dictionary {
            self.crv = dictionary["crv"] as? String
            self.kty = dictionary["kty"] as? String
            self.x = dictionary["x"] as? String
            self.y = dictionary["y"] as? String
        }
    }
}

struct VC: Codable {
    /***/
    var context: [String]?
    /***/
    var type: [String]?
    /***/
    var credentialSchema: CredentialSchema?
    /***/
    var credentialSubject: CredentialSubject?
    
    enum CodingKeys: String, CodingKey {
        case context = "@context"
        case type = "type"
        case credentialSchema = "credentialSchema"
        case credentialSubject = "credentialSubject"
    }
    
    init(dictionary: NSDictionary?, orgJsonString: String) {
        if let dictionary = dictionary {
            self.context = dictionary["@context"] as? [String]
            self.type = dictionary["type"] as? [String]
            let credentialSchemaDic = dictionary["credentialSchema"] as? NSDictionary
            self.credentialSchema = CredentialSchema(dictionary: credentialSchemaDic)
            let credentialSubjectDic = dictionary["credentialSubject"] as? NSDictionary
            self.credentialSubject = CredentialSubject(dictionary: credentialSubjectDic, orgJsonString: orgJsonString)
        }
    }
}

struct CredentialSubject: Codable {
    /***/
    var sd: [String]?
    /***/
    var sdAlg: String?
    /***/
    var field: SubjectField?
    
    enum CodingKeys: String, CodingKey {
        case sd = "_sd"
        case sdAlg = "_sd_alg"
        case field = "field"
    }
    
    init(dictionary: NSDictionary?, orgJsonString: String) {
        if let dictionary = dictionary {
            self.sd = dictionary["_sd"] as? [String]
            self.sdAlg = dictionary["_sd_alg"] as? String
            let fieldDic = dictionary["field"] as? NSDictionary
            self.field = SubjectField(dictionary: fieldDic, orgJsonString: orgJsonString)
        }
    }
}

struct SubjectField: Codable {
    var code: Int?
    var data: [Dictionary<String, String>] = []
    
    enum CodingKeys: String, CodingKey {
        case code = "code"
    }
    
    init(dictionary: NSDictionary?, orgJsonString: String) {
        if let dictionary = dictionary,
           let fields = getDecodeVCDatasFromString(response: orgJsonString) {
            self.code = dictionary["code"] as? Int
            let dataArray = dictionary["data"] as? NSDictionary
            
            fields.enumerated().forEach { (index, dic) in
                dic.keys.forEach { key in
                    if let dic = dataArray?.first(where: {$0.key as? String == key}),
                       let dicKey = dic.key as? String,
                       let dicValue = dic.value as? String{
                        self.data.append([dicKey : dicValue])
                    }
                }
            }
        }
    }
    
    /*解析response String 取得排序過後的jsonObject*/
    func getDecodeVCDatasFromString(response: String) -> [[String: String]]? {
        let cleanResponse = response.replacingOccurrences(of: "\"", with: "")
        
        guard let startRange = cleanResponse.range(of: "data:{", options: .backwards) else {
            return nil
        }
        
        let substring = cleanResponse[startRange.upperBound...]
        
        guard let endRange = substring.range(of: "}") else {
            return nil
        }
        
        let finalString = substring[..<endRange.lowerBound]
        let datas = finalString.split(separator: ",")
        var dicSet: [[String: String]] = []
        
        for data in datas {
            let keyAndValue = data.split(separator: ":")
            
            
            guard let key = keyAndValue.first else {
                return nil
            }
            let value = keyAndValue.count >= 2 ? keyAndValue[1] : ""
            dicSet.append([String(key): String(value)])
        }
        
        return dicSet
    }

}

struct CredentialSchema: Codable {
    /***/
    var id: String?
    /***/
    var type: String?
    
    enum CodingKeys: String, CodingKey {
        case id = "id"
        case type = "type"
    }
    
    init(dictionary: NSDictionary?) {
        if let dictionary = dictionary {
            self.id = dictionary["id"] as? String
            self.type = dictionary["type"] as? String
        }
    }
}
