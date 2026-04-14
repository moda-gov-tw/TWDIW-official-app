import Foundation
import Flutter

public class TokenSDKManager {
    
    private let methodChannel: FlutterMethodChannel
    private var modaDidKey: ModaDidKey?
    
    public init(messenager: FlutterEngine) {

        methodChannel = FlutterMethodChannel(name: "com.flutter_method_channel/gov.moda.dw", binaryMessenger: messenager.binaryMessenger)
        setMethodCallHandler()
    }
    
    func setMethodCallHandler() {
        methodChannel.setMethodCallHandler(handle)
    }
    
    private func handle(call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "init":
            if let args = call.arguments as? Dictionary<String, Any>,
               let tag = args["tag"] as? String,
               let type = args["type"] as? String,
               let pin = args["pin"] as? FlutterStandardTypedData {
                self.initModaDidKey(tag: tag, type: type, pin: pin.data, result: result)
            }else {
                result(FlutterError(code: "UNAVAILABLE",
                                    message: "init arg faild",
                                    details: nil))
            }
        case "getP256Key":
            self.getP256Key(result: result)
        case "verifyUser":
            if let args = call.arguments as? Dictionary<String, Any>,
               let publicKey = args["publicKey"] as? String {
                self.verifyUser(publicKey: publicKey, result: result)
            }else {
                result(FlutterError(code: "UNAVAILABLE",
                                    message: "verifyUser arg faild",
                                    details: nil))
            }
        case "sign":
            if let args = call.arguments as? Dictionary<String, Any>,
               let header = args["header"] as? String,
               let payload = args["payload"] as? String {
                self.sign(header: header, payload: payload, result: result)
            }else {
                result(FlutterError(code: "UNAVAILABLE",
                                    message: "sign arg faild",
                                    details: nil))
            }
        case "delete":
            self.delete(result: result)
        default:
            PrintPro.print("Method not found")
            result(FlutterMethodNotImplemented)
        }
    }
    
    
    private func initModaDidKey(tag: String, type: String, pin :Data, result: FlutterResult) {
        if let sourceType = SourceType(rawValue: type) {
            modaDidKey = ModaDidKey(keyTag: tag, type: sourceType, pin: pin)
            result(true)
        } else {
            result(FlutterError(code: "INVALID_TYPE", message: "Invalid type: \(type)", details: nil))
        }
    }
    
    private func getP256Key(result: FlutterResult){
        if let (publicKey,generateError) = modaDidKey?.getP256Key() {
            if (generateError != nil) {
                result(FlutterError(code: "UNAVAILABLE",
                                    message: generateError?.errorDescription,
                                    details: nil))
            }else {
                result(publicKey)
            }
        }else {
            result(FlutterError(code: "UNAVAILABLE",
                                message: "getP256Key faild",
                                details: nil))
        }
    }
    private func verifyUser(publicKey: String, result: FlutterResult){
        if let (isValid, verifyError) = modaDidKey?.verifyUser(publicKey:publicKey) {
            if (verifyError != nil) {
                result(FlutterError(code: "UNAVAILABLE",
                                    message: verifyError?.errorDescription,
                                    details: nil))
            }else {
                result(isValid)
            }
        }else {
            result(FlutterError(code: "UNAVAILABLE",
                                message: "verifyUser faild",
                                details: nil))
        }
    }
    private func sign(header: String, payload: String, result: FlutterResult){
        if let (signature, signError) = modaDidKey?.sign(header: header, payload: payload) {
            if (signError != nil) {
                result(FlutterError(code: "UNAVAILABLE",
                                    message: signError?.errorDescription,
                                    details: nil))
            }else {
                result(signature)
            }
        }else {
            result(FlutterError(code: "UNAVAILABLE",
                                message: "sign faild",
                                details: nil))
        }
    }
    private func delete(result: FlutterResult){
        if let (isDeleted, deleteError) = modaDidKey?.delete() {
            if (deleteError != nil) {
                result(FlutterError(code: "UNAVAILABLE",
                                    message: deleteError?.errorDescription,
                                    details: nil))
            }else {
                result(isDeleted)
            }
        }else {
            result(FlutterError(code: "UNAVAILABLE",
                                message: "delete faild",
                                details: nil))
        }
    }
}
