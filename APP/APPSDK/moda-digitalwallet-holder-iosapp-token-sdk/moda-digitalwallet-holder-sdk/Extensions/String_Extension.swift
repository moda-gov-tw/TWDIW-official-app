import Foundation

extension String {
    
    func toBase64() -> String? {
        guard let data = self.data(using: .utf8) else { return nil }
        return data.base64EncodedString()
    }
    
    func fromBase64() -> String? {
        guard let data = Data(base64Encoded: self) else { return nil }
        return String(data: data, encoding: .utf8)
    }
    
    func hexToData() -> Data? {
        let hexStr = self.replacingOccurrences(of: " ", with: "")
        var data = Data()
        
        var index = hexStr.startIndex
        while index < hexStr.endIndex {
            let nextIndex = hexStr.index(index, offsetBy: 2, limitedBy: hexStr.endIndex) ?? hexStr.endIndex
            let byte = String(hexStr[index..<nextIndex])
            
            if let num = UInt8(byte, radix: 16) {
                data.append(num)
            } else {
                return nil
            }
            
            index = nextIndex
        }
        return data
    }
    
    func hexToBase64() -> String? {
        guard let data = self.hexToData() else {
            return nil
        }
        return data.base64EncodedString()
    }
    
    func toBase64URL() -> String? {
        guard let data = self.data(using: .utf8) else { return nil }
        return data.base64EncodedString()
            .replacingOccurrences(of: "+", with: "-")
            .replacingOccurrences(of: "/", with: "_")
            .replacingOccurrences(of: "=", with: "")
    }
    
    func base64UrlToBase64() -> String {
        var base64 = self
            .replacingOccurrences(of: "-", with: "+")
            .replacingOccurrences(of: "_", with: "/")
        while base64.count % 4 != 0 {
            base64 += "="
        }
        return base64
    }
}
