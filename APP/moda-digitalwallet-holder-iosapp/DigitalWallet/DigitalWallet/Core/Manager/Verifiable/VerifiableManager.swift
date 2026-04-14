//
//  VerifiableManager.swift
//  DigitalWallet
//

import Foundation
import Flutter
import moda_digitalwallet_holder_sdk

class VerifiableManager: VerifiableManagerProtocol {
    public static let shared = VerifiableManager()
    
    private var flutterEngine = FlutterEngine(name: "my flutter engine")
    private var basicChannel: FlutterBasicMessageChannel?
    private var methodChannel: FlutterMethodChannel?
    private let tokenSDKManager: TokenSDKManager
    
    private init(){
        flutterEngine.run()
        
        methodChannel = FlutterMethodChannel(
            name: Config.DID.CHANNEL_NAME,
            binaryMessenger: flutterEngine.binaryMessenger
        )
        
        tokenSDKManager = TokenSDKManager(messenager: flutterEngine)
    }
    
    /// 依序呼叫 103i, 101i, 102i，進行身分建立
    /// - Parameter keyTag: 皮夾的uuid string
    /// - Returns: DIDDataResponse 作為後續流程會使用到的資料
    public func createDecentralizedIdentifier(uuid keyTag: String) async throws -> DIDDataResponse {
        // 初始化關鍵SDK
        try await dwsdk103i(keyTag: keyTag)
        // 呼叫dwsdk101i建立DID金鑰
        let keyData: KeyData = try await dwsdk101i()
        // 呼叫dwsdk102i建立DID身分
        let didData = try await dwsdk102i(publicKey: keyData.publicKey)
        // 回傳已建立的DID身分
        return didData
    }
    
    /// 初始化關鍵SDK，帶入對應皮夾的UUID
    /// - Parameter keyTag: 對應皮夾的UUID
    public func initializationKxSDK(walletUUID keyTag: String) async throws {
        try await dwsdk103i(keyTag: keyTag)
    }
    
    /// 透過qrcode加入卡片
    /// - Parameters:
    ///   - qrcode: 動態qrcode
    ///   - didDataString: 使用者的DID身分
    ///   - otp: 單次驗證碼，確保為本人使用
    /// - Returns: 成功加卡後的資料
    func createVerifiableCredential(qrcode: String,
                                    didDataString: String,
                                    otp: String) async throws -> ApplyVCDataResponse {
        guard let base64DecodedDidData = Data(base64Encoded: didDataString),
              let didData = try? JSONDecoder().decode(DIDDataResponse.self, from: base64DecodedDidData)
        else {
            throw SDKError.LocalDataIsEmpty
        }
        
        let did: Data = try JSONEncoder().encode(didData)
        
        guard let didFile: String = String(data: did, encoding: .utf8) else{
            throw DIDError.encodeFail
        }
        
        let applyVCData: ApplyVCDataResponse = try await applyVC(didFile: didFile, qrCode: qrcode, otp: otp)
        
        return applyVCData
    }
    
    /// dwsdk-301i:持有端(Holder)進行授權VC卡片資訊
    /// - Returns: VerifyVCResponse: nil為成功
    func verifyVC(applyVCData: ApplyVCDataResponse, didDataString: String) async throws -> VerifyVCResponse? {
        guard let base64DecodedDidData = Data(base64Encoded: didDataString),
              let didData = try? JSONDecoder().decode(DIDDataResponse.self, from: base64DecodedDidData)
        else {
            throw SDKError.LocalDataIsEmpty
        }
        
        let did: Data = try JSONEncoder().encode(didData)
        
        guard let didFile: String = String(data: did, encoding: .utf8) else{
            throw DIDError.encodeFail
        }
        
        let verifyVCRequest: VerifyVCRequest = VerifyVCRequest(credential: applyVCData.credential, didFile: didFile, frontUrl: Config.FRONTEND_URL)
        
        guard let jsonData = try? JSONEncoder().encode(verifyVCRequest),
              let json = String(data: jsonData, encoding: .utf8) else {
            throw DIDError.encodeFail
        }
        
        let arguments = [Config.DID.verifyVC: json]
        
        return try await callSDK(method: Config.DID.verifyVC, arguments: arguments)
    }
    
    /// dwsdk-302i:持有端(Holder)進行離線授權VC卡片資訊
    func verifyVCOffline(applyVCData: ApplyVCDataResponse, issList: Array<DownloadIssListResponse>, vcList: Array<Dictionary<String, Array<Dictionary<String, String>>>>, didDataString: String) async throws -> VerifyVCResponse?{
        guard let base64DecodedDidData = Data(base64Encoded: didDataString),
              let didData = try? JSONDecoder().decode(DIDDataResponse.self, from: base64DecodedDidData)
        else {
            throw SDKError.LocalDataIsEmpty
        }
        
        let did: Data = try JSONEncoder().encode(didData)
        
        guard let didFile: String = String(data: did, encoding: .utf8) else{
            throw DIDError.encodeFail
        }
        
        let verifyVCOfflineRequest: VerifyVCOfflineRequest = VerifyVCOfflineRequest(credential: applyVCData.credential, didFile: didFile, issList: issList, vcList: vcList)
        
        guard let jsonData = try? JSONEncoder().encode(verifyVCOfflineRequest),
              let json = String(data: jsonData, encoding: .utf8) else {
            throw DIDError.encodeFail
        }
        
        let arguments = [Config.DID.verifyVCOffline: json]
        
        return try await callSDK(method: Config.DID.verifyVCOffline, arguments: arguments)
    }
    
    /**dwsdk-401i:持有端(Holder)進行解析VP展示授權申請*/
    func parseVerifiablePresentation(qrcode: String) async throws -> ParseVPDataResponse {
        let parseData: ParseVPDataResponse = try await parseVPQrcode(qrcode: qrcode)
        return parseData
    }
    
    /**dwsdk-402i:持有端(Holder)進行建立VP展示*/
    func verifyVerifiablePresentationWithKx(parseData: ParseVPDataResponse, selectVCs: [SelectVC], didDataString: String, customData: VPCustomData?) async throws -> Bool {
        guard let base64DecodedDidData = Data(base64Encoded: didDataString),
              let didData = try? JSONDecoder().decode(DIDDataResponse.self, from: base64DecodedDidData)
        else {
            throw SDKError.LocalDataIsEmpty
        }
        
        let did: Data = try JSONEncoder().encode(didData)
        
        guard let didFile: String = String(data: did, encoding: .utf8) else{
            throw DIDError.encodeFail
        }
        
        let reqT: String = parseData.reqT
        let result: Bool = try await generateVP(reqT: reqT, didFile: didFile, vcs: selectVCs, customData: customData)
        
        return result
    }
    
    /// dwsdk-501i:持有端(Holder)進行查詢VC卡片資訊
    /// - Returns:
    func getVerifiableCredential(applyVCData: ApplyVCDataResponse) async throws -> DecodeVCDataResponse {
        let decodeVCData: DecodeVCDataResponse = try await decodeVC(credential: applyVCData.credential)
        return decodeVCData
    }
    
    /// dwsdk-601i:持有端(Holder)下載發行端(Issuer)機關清單狀態列表
    /// - Parameter url:
    func downloadIssList() async throws -> [DownloadIssListResponse] {
        let downloadIssListRequest: DownloadIssListRequest = DownloadIssListRequest(url: Config.FRONTEND_URL, newFlag: true)
        
        guard let jsonData = try? JSONEncoder().encode(downloadIssListRequest),
              let json = String(data: jsonData, encoding: .utf8) else {
            throw DIDError.encodeFail
        }
        
        let arguments = [Config.DID.downloadIssList: json]
        
        return try await callSDK(method: Config.DID.downloadIssList, arguments: arguments)!
    }
    
    /// dwsdk-602i:持有端(Holder)下載VC狀態清冊列表
    /// - Parameter vcs: vcs資料陣列
    func downloadAllVCList(vcs: Array<String>, vcList: Array<Dictionary<String, Array<VCJWTAndSHA256Hash>>>) async throws -> DownloadAllVCListResponse {
        let downloadAllVCListRequest: DownloadAllVCListRequest = DownloadAllVCListRequest(vcs: vcs, vcList: vcList)
        
        guard let jsonData = try? JSONEncoder().encode(downloadAllVCListRequest),
              let json = String(data: jsonData, encoding: .utf8) else {
            throw DIDError.encodeFail
        }
        
        let arguments = [Config.DID.downloadAllVCList: json]
        
        return try await callSDK(method: Config.DID.downloadAllVCList, arguments: arguments)!
    }
}

// MARK: 透過Fullter呼叫API
extension VerifiableManager {
    /**dwsdk-moda-101i: SDK轉發APP api(無payload)*/
    func requestFromSDK<T: Codable, RS: Codable>(url: String,
                                                 type: RequestMethod,
                                                 bodyModel: T) async throws -> RS {
        guard let bodyJsonData = try? JSONEncoder().encode(bodyModel),
              let bodyJson = String(data: bodyJsonData, encoding: .utf8) else {
            throw DIDError.encodeFail
        }

        let request = SendRequest(url: url, type: type, body: bodyJson)

        guard let requestJsonData = try? JSONEncoder().encode(request),
              let requestJson = String(data: requestJsonData, encoding: .utf8) else {
            throw DIDError.encodeFail
        }

        return try await sendToSDKAndDecode(method: Config.DID.sendRequest,
                                            requestJson: requestJson,
                                            logName: "dwsdk-moda-101i")
    }
    
    /**dwsdk-moda-201i: SDK轉發APP api (payload)*/
    func requestPayloadFromSDK<T: Codable>(type: RequestMethod,
                                           payload: T,
                                           url: String,
                                           didFile: String) async throws -> DwVerIfierMgr402iResponse {
        let encoder = JSONEncoder()
        encoder.outputFormatting = [.prettyPrinted, .withoutEscapingSlashes]
        guard let data = Data(base64Encoded: didFile), let decodedString = String(data: data, encoding: .utf8) else  {
            throw DIDError.encodeFail
        }
        
        let payloadString = String(data: try encoder.encode(payload), encoding: .utf8)!
        let request = SendJWTRequest(didFile: decodedString, payload: payloadString, url: url)
        
        let jsonData = try encoder.encode(request)
        let jsonString = String(data: jsonData, encoding: .utf8) ?? ""
        
        let arguments = [Config.DID.sendJWTRequest: String(jsonString)]
        
        guard let response: DwVerIfierMgr402iResponse = try await callSDK(method: Config.DID.sendJWTRequest,
                                                                          arguments: arguments) else {
            throw DIDError.responseError(code: "", message: "")
        }
        return response
        
    }
    
    /**dwsdk-moda-101i、201i: SDK轉發APP api*/
    private func sendToSDKAndDecode<RS: Codable>(method: String,
                                                 requestJson: String,
                                                 logName: String) async throws -> RS {
        let arguments = [method: requestJson]
        
        guard let dwSDKBaseResponse: DWSDKModaModel = try await callSDK(method: method,
                                                                        arguments: arguments) else {
            throw DIDError.responseError(code: SDKResultCode.SDKFail.rawValue, message: nil)
        }
        
        guard let responseString = dwSDKBaseResponse.response,
              let json = responseString.data(using: .utf8) else {
            throw DIDError.responseError(code: SDKResultCode.SDKFail.rawValue,
                                         message: dwSDKBaseResponse.response)
        }
        
        do {
            let result = try JSONDecoder().decode(DWSDKModaBaseModelResult<RS>.self, from: json)
            guard let response = result.data else {
                throw DIDError.responseError(code: SDKResultCode.SDKFail.rawValue,
                                             message: dwSDKBaseResponse.response)
            }
            return response
        } catch {
            throw DIDError.responseError(code: SDKResultCode.SDKFail.rawValue,
                                         message: NSLocalizedString("SDKDecodeError", comment: ""))
        }
    }

    
}

extension VerifiableManager{
    
    /// dwsdk-101i:持有端(Holder) DID 金鑰產生
    /// - Returns: KeyData
    private func dwsdk101i() async throws -> KeyData {
        return try await callSDK(method: Config.DID.generateKey, arguments: nil)!
    }
    
    /// dwsdk-102i:持有端(Holder)DID 身分產生
    /// - Parameter publicKey: generateKey的KeyData
    /// - Returns: DIDData
    private func dwsdk102i(publicKey: PublicKey) async throws -> DIDDataResponse {
        guard let jsonData = try? JSONEncoder().encode(publicKey),
              let json = String(data: jsonData, encoding: .utf8) else {
            throw DIDError.encodeFail
        }
        
        let arguments = [Config.DID.generateDID: json]
        
        return try await callSDK(method: Config.DID.generateDID, arguments: arguments)!
    }
    
    
    /// 初始化關鍵SDK
    /// - Parameter keyTag: 皮夾對應的UUID
    /// - Returns: 是否初始化成功
    @discardableResult
    func dwsdk103i(keyTag: String) async throws -> Bool {
        let generateKxRequest: GenerateKxRequest = GenerateKxRequest(keyTag: keyTag, type: "platform", pinForKx: "")
        
        guard let jsonData = try? JSONEncoder().encode(generateKxRequest),
              let json = String(data: jsonData, encoding: .utf8) else {
            throw DIDError.encodeFail
        }
        
        let arguments = [Config.DID.generateKx: json]
        
        return try await callSDK(method: Config.DID.generateKx, arguments: arguments)!
    }
    
    
    
    /// dwsdk-201i:持有端解析VC卡片申請請求與進行申請
    /// - Parameters:
    ///   - didFile: didData encoder
    ///   - qrCode: qrcode
    /// - Returns: ApplyVCData
    private func applyVC(didFile: String, qrCode: String, otp: String) async throws -> ApplyVCDataResponse {
        let applyVC: ApplyVCDataRequest = ApplyVCDataRequest(didFile: didFile, qrCode: qrCode, otp: otp)
        
        guard let jsonData = try? JSONEncoder().encode(applyVC),
              let json = String(data: jsonData, encoding: .utf8)  else {
            throw DIDError.encodeFail
        }
        
        let arguments = [Config.DID.applyVC: json]
        
        return try await callSDK(method: Config.DID.applyVC, arguments: arguments)!
    }
    
    /// dwsdk-401i:持有端(Holder)進行解析VP展示授權申請
    /// - Parameter qrcode:
    /// - Returns:
    private func parseVPQrcode(qrcode: String) async throws -> ParseVPDataResponse {
        let parseVP: ParseVPDataRequest = ParseVPDataRequest(qrCode: qrcode, frontUrl: Config.FRONTEND_URL)
        
        guard let jsonData = try? JSONEncoder().encode(parseVP),
              let json = String(data: jsonData, encoding: .utf8) else {
            throw DIDError.encodeFail
        }
        
        let arguments = [Config.DID.parseVPQrcode: json]
        
        return try await callSDK(method: Config.DID.parseVPQrcode, arguments: arguments)!
    }
    
    /// dwsdk-402i:持有端(Holder)進行建立VP展示
    /// - Parameters:
    ///   - reqT:
    ///   - didFile:
    ///   - vcs:
    /// - Returns:
    private func generateVP(reqT: String, didFile: String, vcs: [SelectVC], customData: VPCustomData?) async throws -> Bool {
        var customDataString = ""
        if let custom = customData?.jsonString {
            customDataString = custom
        }
        
        let generateVPData: GenerateVPRequest = GenerateVPRequest(reqT: reqT, didFile: didFile, vcs: vcs, frontUrl: Config.FRONTEND_URL, customData: customDataString)
        
        guard let jsonData = try? JSONEncoder().encode(generateVPData),
              let json = String(data: jsonData, encoding: .utf8) else {
            throw DIDError.encodeFail
        }
        
        let arguments = [Config.DID.generateVP: json]
        
        return try await callSDK(method: Config.DID.generateVP, arguments: arguments)!
    }
    
    /**dwsdk-501i:持有端(Holder)進行查詢VC卡片資訊*/
    private func decodeVC(credential: String) async throws -> DecodeVCDataResponse {
        let decodeVC: DecodeVCDataRequest = DecodeVCDataRequest(credential: credential)
        
        guard let jsonData = try? JSONEncoder().encode(decodeVC),
              let json = String(data: jsonData, encoding: .utf8) else {
            throw DIDError.encodeFail
        }
        
        let arguments = [Config.DID.decodeVC: json]
        
        return try await callSDK(method: Config.DID.decodeVC, arguments: arguments)!
    }
    
    /// 呼叫SDK
    /// - Parameters:
    ///   - method: API Method name
    ///   - arguments: request parameter
    /// - Returns: response data
    private func callSDK<T: Codable>(method: String, arguments: [String: String]?) async throws -> T? {
        return try await withCheckedThrowingContinuation { continuation in
            methodChannel?.invokeMethod(method, arguments: arguments, result: { response in
                if let error = response as? FlutterError {
                    continuation.resume(throwing: DIDError.responseError(code: error.code, message: "[\(error.code)]：\(error.message ?? "")"))
                    return
                }
                
                guard let response = response as? String,
                      let json = response.data(using: .utf8) else {
                    
                    let message: String = "[\(SDKResultCode.SystemFail.rawValue)]：\(NSLocalizedString("SDKServiceUnknownError", comment: ""))"
                    continuation.resume(throwing: DIDError.responseError(code: SDKResultCode.SystemFail.rawValue, message: message))
                    return
                }
                
                
                /*dwsdk-501i:持有端(Holder)進行查詢VC卡片資訊*/
                if method == Config.DID.decodeVC{
                    if let jsonObject = try? JSONSerialization.jsonObject(with: json),
                       let dict = jsonObject as? NSDictionary {
                        let decodeVCDataResponseDic = dict["data"] as? NSDictionary
                        let decodeVCData: DecodeVCDataResponse = DecodeVCDataResponse(dictionary: decodeVCDataResponseDic, orgJsonString: response)
                        continuation.resume(returning: decodeVCData as? T)
                    } else {
                        let message: String = "[\(SDKResultCode.SystemFail.rawValue)]：\(NSLocalizedString("SDKSystemBasicUnknownError", comment: ""))"
                        
                        continuation.resume(throwing: DIDError.responseError(code: SDKResultCode.SystemFail.rawValue, message: message))
                    }
                    
                    return
                }
                
                /// dwsdk-401i 持有端(Holder)進行解析VP展示授權申請
                if method == Config.DID.parseVPQrcode {
                    if let model = try? JSONDecoder().decode(BaseModel<ParseVPDataResponse>.self, from: json) {
                        var decodeVCData = model.data as ParseVPDataResponse
                        if model.code == SDKResultCode.NotInTrustedList.rawValue {
                            decodeVCData.inTrustedList = false
                        }
                        continuation.resume(returning: decodeVCData as? T)
                    } else {
                        if let responseData = response.data(using: .utf8) {
                            let dicData = try? JSONSerialization.jsonObject(with: responseData, options: []) as? Dictionary<String, AnyObject>
                            
                            guard let dicData = dicData else{
                                let message: String = "[\(SDKResultCode.AppError.rawValue)]：\(NSLocalizedString("AppError", comment: ""))"
                                continuation.resume(throwing: DIDError.responseError(code: SDKResultCode.AppError.rawValue, message: message))
                                return
                            }
                            let resultCode: String = dicData["code"] as? String ?? ""
                            let resultMessage: String = String(describing:(dicData["message"] as AnyObject))

                            let message: String = String(format: NSLocalizedString("NetworkError", comment: ""), "\(resultCode)-\(resultMessage)")
                            
                            continuation.resume(throwing: DIDError.responseError(code: resultCode, message: message))
                        }
                    }
                    
                    return
                }
                
                if let responseData = response.data(using: .utf8) {
                    let dicData = try? JSONSerialization.jsonObject(with: responseData, options: []) as? Dictionary<String, AnyObject>
                    
                    guard let dicData = dicData else{
                        let message: String = "[\(SDKResultCode.AppError.rawValue)]：\(NSLocalizedString("AppError", comment: ""))"
                        continuation.resume(throwing: DIDError.responseError(code: SDKResultCode.AppError.rawValue, message: message))
                        return
                    }
                    
                    /*SDK呼叫是否成功*/
                    let resultCode: String = dicData["code"] as? String ?? ""
                    let resultMessage: String = String(describing:(dicData["message"] as AnyObject))
                    let isSuccess: Bool = resultCode == SDKResultCode.Success.rawValue
                    
                    /*針對VC驗證，先判斷回傳代碼
                     *dwsdk-301i:持有端(Holder)進行授權VC卡片資訊
                     *dwsdk-302i:持有端(Holder)進行離線授權VC卡片資訊*/
                    if method == Config.DID.verifyVC || method == Config.DID.verifyVCOffline{
                        /*可以轉成數字*/
                        if let errorCode: Int = Int(resultCode) {
                            do {
                                let baseModel = try JSONDecoder().decode(BaseModel<T>.self, from: json)
                                continuation.resume(returning: baseModel.data)
                            } catch {
                                let message: String = "[\(resultCode)-\(errorCode)]：\(NSLocalizedString("AppError", comment: ""))"
                                
                                continuation.resume(throwing: DIDError.responseError(code: resultCode, message: message))
                            }
                            
                            return
                        }
                    }
                    
                    if T.self == String.self{
                        continuation.resume(returning: response as? T)
                    }
                    else if T.self == Bool.self{
                        continuation.resume(returning: isSuccess as? T)
                    }
                    else{
                        /*SDK呼叫成功*/
                        if isSuccess{
                            /*dwsdk-602i:持有端(Holder)下載VC狀態清冊列表*/
                            if method == Config.DID.downloadAllVCList{
                                var data: DownloadAllVCListResponse = DownloadAllVCListResponse()
                                
                                do {
                                    /*成功情境*/
                                    let baseModel = try JSONDecoder().decode(BaseModel<Array<Dictionary<String, Array<VCJWTAndSHA256Hash>>>>.self, from: json)
                                    data.result = baseModel.data
                                    continuation.resume(returning: data as? T)
                                } catch {
                                    let message: String = "[\(resultCode)]：\(NSLocalizedString("AppError", comment: ""))"
                                    continuation.resume(throwing: DIDError.responseError(code: resultCode, message: message))
                                }
                            } else if method == Config.DID.sendJWTRequest {
                                do {
                                        let baseModel = try JSONDecoder().decode(BaseModel<String>.self, from: responseData)
                                        guard let nestedJsonData = baseModel.data.data(using: .utf8) else {
                                            throw DIDError.responseError(code: baseModel.code, message: baseModel.message)
                                        }

                                    let nestedDecoded = try JSONDecoder().decode(BaseModel<T>.self, from: nestedJsonData)
                                    continuation.resume(returning: nestedDecoded.data)

                                    } catch {
                                        let message: String = "[\(resultCode)]：\(NSLocalizedString("AppError", comment: ""))"
                                        continuation.resume(throwing: DIDError.responseError(code: SDKResultCode.SystemFail.rawValue, message: message))
                                    }
                            }
                            else{
                                do {
                                    let baseModel = try JSONDecoder().decode(BaseModel<T>.self, from: json)
                                    continuation.resume(returning: baseModel.data)
                                } catch {
                                    let message: String = "[\(resultCode)]：\(NSLocalizedString("AppError", comment: ""))"
                                    
                                    continuation.resume(throwing: DIDError.responseError(code: resultCode, message: message))
                                }
                            }
                        }
                        else if resultCode == SDKResultCode.NetworkFail.rawValue {
                            /*可以轉成數字*/
                            if let errorCode: Int = Int(resultMessage) {
                                switch errorCode {
                                    case 201...599:
                                        let code: String = resultCode.appending("-").appending("F".appending(self.firstDigit(errorCode: errorCode)))
                                        let message: String = String(format: NSLocalizedString("NetworkError", comment: ""), code)
                                        
                                        continuation.resume(throwing: DIDError.responseError(code: resultCode, message: message))
                                    default:
                                    
                                    let message: String = String(format: NSLocalizedString("NetworkError", comment: ""), resultCode)
                                        continuation.resume(throwing: DIDError.responseError(code: resultCode, message: message))
                                }
                            }
                            /*無法轉換*/
                            else {
                                
                                let message: String = "[\(resultCode)]：\(NSLocalizedString("SDKUnknownErrorReason", comment: ""))"
                                continuation.resume(throwing: DIDError.responseError(code: resultCode, message: message))
                            }
                        }
                        else if resultCode.count == 4 {
                            /*RS.code = 4碼數字，RS.message對應SDK錯誤代碼與中文訊息對應表，對應不到中文，則顯示"不明錯誤原因(message的錯代碼)"*/
                            if let errorMessage = SDKErrorEnum.fromCode(resultMessage) {
                                let message: String = "[\(resultCode)-\(resultMessage)]：\(errorMessage.message)"
                                continuation.resume(throwing: DIDError.responseError(code: resultMessage, message: message))
                            }
                            /*代碼表對應不到*/
                            else{
                                
                                let message: String = "[\(resultCode)-\(resultMessage)]：\(NSLocalizedString("SDKUnknownErrorReason", comment: ""))"
                                continuation.resume(throwing: DIDError.responseError(code: resultCode, message: message))
                            }
                        }
                        /*失敗(顯示狀態)*/
                        else {
                            /*
                             *  dwsdk-301i:持有端(Holder)進行授權VC卡片資訊
                             *  dwsdk-302i:持有端(Holder)進行離線授權VC卡片資訊
                             */
                            if method == Config.DID.verifyVC || method == Config.DID.verifyVCOffline {
                                do {
                                    let baseModel = try JSONDecoder().decode(BaseModel<T>.self, from: json)
                                    continuation.resume(returning: baseModel.data)
                                } catch {
                                    let message: String = "[\(resultCode)]：\(NSLocalizedString("AppError", comment: ""))"
                                    
                                    continuation.resume(throwing: DIDError.responseError(code: resultCode, message: message))
                                }
                            } else {
                                
                                let message: String = "[\(resultCode)]：\(NSLocalizedString("SDKUnknownErrorReason", comment: ""))"
                                continuation.resume(throwing: DIDError.responseError(code: resultCode, message: message))
                            }
                        }
                    }
                }else{
                    let message: String = "[\(SDKResultCode.SystemFail.rawValue)]：\(NSLocalizedString("SDKSystemBasicUnknownError", comment: ""))"
                    continuation.resume(throwing: DIDError.responseError(code: SDKResultCode.SystemFail.rawValue, message: message))
                }
            })
        }
    }
    
    /// 轉換錯誤代碼
    /// - Parameter errorCode: 錯誤代碼
    /// - Returns: 回傳
    private func firstDigit(errorCode: Int) -> String {
        var result: String = ""//最終輸出內容
        let str = String(abs(errorCode))//轉為字串並取絕對值，避免負號影響
        
        if let firstChar = str.first, let digit = Int(String(firstChar)) {
            result =
                switch digit{
                    case 2: "A"
                    case 3: "B"
                    case 4: "C"
                    case 5: "D"
                    default: ""
            }
            
            let code: String? = getSecondAndThirdDigits(number: errorCode)
            
            return result.appending(code ?? "")
        }
        
        return ""
    }
    
    /**取得錯誤代碼第二、三位數*/
    private func getSecondAndThirdDigits(number: Int) -> String? {
        let num = abs(number)//取絕對值，避免負數影響
        
        /*確保至少三位數*/
        if num < 100 || num > 999{
            return nil
        }
        
        /*取後兩位數*/
        let str = String(abs(num)) // 轉成字串並取絕對值
        
        let secondDigit = str[str.index(str.startIndex, offsetBy: 1)]
        let thirdDigit = str[str.index(str.startIndex, offsetBy: 2)]
        
        return "\(secondDigit)\(thirdDigit)"
    }
}

