//
//  DigitalWallet
//

import Foundation

struct BaseModel<T: Codable>: Codable, DIDResponseProtocol {
    
    var code: String
    
    var message: String
    
    var data: T
}
