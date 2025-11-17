## twdiw-mobile-sdk(提供行動裝置進行與發證端與驗證端模組進行 OID4VC 與 OID4VP 通訊的專用 SDK)

部署於使用者行動裝置的應用程式，並包含核心功能所需的SDK，支援 Android 和 iOS 平台。負責使用者的電子卡的管理與存取，並確保使用者能夠在安全的環境中持有和管理電子卡。

### 編譯方式

#### Android
```bash
# 於 APPSDK 目錄下執行
flutter build aar --output=./output/android
```
#### iOS
```bash
flutter build ios-framework --output=./output/ios
```

### 執行方式

```
將 Android/iOS output 分別放入 Android/iOS 數位皮夾 App 專案中進行編譯
```

### 範例程式

#### Android - 產生 DID
```
val flutterEngine = FlutterEngine(mContext)
flutterEngine.dartExecutor.executeDartEntrypoint(
    DartExecutor.DartEntrypoint.createDefault()
)
mMethodChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, AppConstants.DID.CHANNEL_NAME)

val keyJsonAdapter = mMoshi.adapter(PublicKey::class.java)
val json = keyJsonAdapter.toJson(publicKey)
val arguments = mapOf(
    AppConstants.DID.generateDID to json
)
mMethodChannel?.invokeMethod(AppConstants.DID.generateDID, arguments, object : MethodChannel.Result {
    override fun success(result: Any?) {
        val jsonAdapter = mMoshi.adapter<BaseModel<DIDData>>(
            Types.newParameterizedType(BaseModel::class.java, DIDData::class.java)
        )
        jsonAdapter.fromJson(result.toString())?.also { response ->
            if (response.code == "0" && response.data != null) {
                coroutine.resumeWith(Result.success(response.data))
            } else {
                coroutine.resumeWithException(SDKException(response.message))
            }
        } ?: run {
            coroutine.resumeWithException(SDKException("${AppConstants.DID.generateDID} error"))
        }
    }

    override fun error(errorCode: String, errorMessage: String?, errorDetails: Any?) {
        coroutine.resumeWithException(SDKException(errorMessage ?: "${AppConstants.DID.generateDID} error."))
    }

    override fun notImplemented() {
        coroutine.resumeWithException(SDKException("${AppConstants.DID.generateDID} notImplemented"))
    }
})
```

#### iOS - 產生 DID
```
private func generateDID(publicKey: PublicKey) async throws -> DIDData {
        guard let jsonData = try? JSONEncoder().encode(publicKey),
              let json = String(data: jsonData, encoding: .utf8) else {
            throw DIDError.encodeFail
        }
        print(publicKey)
        let arguments = [Config.DID.generateDID: json]
        
        return try await callSDK(method: Config.DID.generateDID, arguments: arguments)
    }

private func callSDK<T: Codable>(method: String, arguments: [String: String]?) async throws -> T {
        return try await withCheckedThrowingContinuation { continuation in
            
            methodChannel?.invokeMethod(method, arguments: arguments, result: { response in
                
                if let error = response as? FlutterError {
                    print("Flutter Error code: \(error.code) message: \(error.message ?? "error沒有message")")
                    continuation.resume(throwing: DIDError.responseError(code: error.code, message: error.message))
                    return
                }
                
                guard let response = response as? String,
                      let json = response.data(using: .utf8) else {
                    print("response to string fail or responseStr to data fail")
                    continuation.resume(throwing: DIDError.stringToDataFail)
                    return
                }
                
                print(response)
                
                do {
                    let baseModel = try JSONDecoder().decode(BaseModel<T>.self, from: json)
                    continuation.resume(returning: baseModel.data)
                } catch {
                    continuation.resume(throwing: error)
                }
            })
        }
    }
```

### runtime 環境

* Android 9+
* iOS 13+
