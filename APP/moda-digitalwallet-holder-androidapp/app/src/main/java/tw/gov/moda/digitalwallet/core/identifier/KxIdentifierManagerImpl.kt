package tw.gov.moda.digitalwallet.core.identifier

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.ToNumberPolicy
import com.google.gson.reflect.TypeToken
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.data.annotation.APIMethod
import tw.gov.moda.digitalwallet.data.element.SDKErrorEnum
import tw.gov.moda.digitalwallet.data.model.BaseModel
import tw.gov.moda.digitalwallet.data.model.DownloadIssuers
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk101i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk102i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk103i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk201i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk301i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk401i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk402i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk501i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk602i
import tw.gov.moda.digitalwallet.data.model.dwsdk.moda.DwsdkModa101i
import tw.gov.moda.digitalwallet.data.model.dwsdk.moda.DwsdkModa201i
import tw.gov.moda.digitalwallet.exception.IdentifierException
import tw.gov.moda.digitalwallet.exception.SDKException
import tw.gov.moda.diw.BuildConfig
import tw.gov.moda.diw.R
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.net.URLEncoder
import kotlin.coroutines.resumeWithException

/**
 * Flutter Channel Method 物件
 *
 * @constructor [MethodChannel], [Context], [Gson], [WalletRepository]。
 */
class KxIdentifierManagerImpl(
    private val mMethodChannel: MethodChannel,
    private val mContext: Context,
    private val mGson: Gson,
    private val mWalletRepository: WalletRepository
) : IdentifierManager {
    companion object {
        private const val TAG = "KxIdentifierManagerImpl"
    }

    /**
     * Generate a pair of keys by holder.
     */
    override suspend fun dwsdk101i(): Dwsdk101i.Response? = withContext(Dispatchers.Main) {
        val methodName = "dwsdk-101i"
        val typeToken = object : TypeToken<BaseModel<Dwsdk101i.Response>>() {}.type
        val result: BaseModel<Dwsdk101i.Response>? = invokeMethod(methodName, AppConstants.DID.GENERATE_KEY, null, typeToken)
        return@withContext result?.data
    }

    /**
     * Generate a Decentralized Identifier by holder.
     */
    override suspend fun dwsdk102i(publicKey: Dwsdk101i.Response.PublicKey): Dwsdk102i.Response? = withContext(Dispatchers.Main) {
        // 102i
        val methodName = "dwsdk-102i"
        val json = mGson.toJson(publicKey)
        val arguments = mapOf(
            AppConstants.DID.GENERATE_DID to json
        )
        val typeToken = object : TypeToken<BaseModel<Dwsdk102i.Response>>() {}.type
        val result: BaseModel<Dwsdk102i.Response>? = invokeMethod(methodName, AppConstants.DID.GENERATE_DID, arguments, typeToken)
        return@withContext result?.data
    }

    /**
     * Initialize the Kx SDK.
     */
    override suspend fun dwsdk103i(keyTag: String): Any? = withContext(Dispatchers.Main) {
        // 103i
        val methodName = "dwsdk-103i"
        val json = mGson.toJson(Dwsdk103i.Request(keyTag, "platform", ""))
        val arguments = mapOf(
            AppConstants.DID.GENERATE_KX to json
        )
        val typeToken = object : TypeToken<BaseModel<Any>>() {}.type
        val result: Any? = invokeMethod(methodName, AppConstants.DID.GENERATE_KX, arguments, typeToken)
        return@withContext result
    }

    /**
     * Decoding the verifiable credential from holder, and parsing the arguments for requiring.
     */
    override suspend fun dwsdk201i(otp: String, qrCode: String, didJson: String): Dwsdk201i.Resposne? = withContext(Dispatchers.Main) {
        // 201i
        val methodName = "dwsdk-201i"
        val params = Dwsdk201i.Request(didJson, qrCode, otp)
        val json = mGson.toJson(params)
        val arguments = mapOf(
            AppConstants.DID.APPLY_VC to json
        )

        val typeToken = object : TypeToken<BaseModel<Dwsdk201i.Resposne>>() {}.type
        val result: BaseModel<Dwsdk201i.Resposne>? = invokeMethod(methodName, AppConstants.DID.APPLY_VC, arguments, typeToken)
        return@withContext result?.data
    }

    /**
     * Verifying the verifiable credential from holder.
     */
    override suspend fun dwsdk301i(credential: String, didJson: String): BaseModel<Dwsdk301i.Response>? = withContext(Dispatchers.Main) {
        // 301i
        val methodName = "dwsdk-301i"
        val params = Dwsdk301i.Request(credential, didJson, BuildConfig.DID_ISSUER_URL)
        val json = mGson.toJson(params)
        val arguments = mapOf(
            AppConstants.DID.VERIFY_VC to json
        )
        val typeToken = object : TypeToken<BaseModel<Dwsdk301i.Response>>() {}.type
        val result: BaseModel<Dwsdk301i.Response>? = invokeMethod(methodName, AppConstants.DID.VERIFY_VC, arguments, typeToken)
        return@withContext result
    }


    /**
     * Verifying the verifiable credential from holder by offline.
     */
    override suspend fun dwsdk302i(credential: String, didJson: String, issuerListJson: String, verifiableCredentialListJson: String): BaseModel<Dwsdk301i.Response>? = withContext(Dispatchers.Main) {
        // 302i
        val methodName = "dwsdk-302i"
        val issuerList = mGson.fromJson(issuerListJson, JsonArray::class.java)
        val verifiableCredentialList = mGson.fromJson(verifiableCredentialListJson, JsonArray::class.java)
        val params = Dwsdk301i.Request(credential, didJson, null, issuerList, verifiableCredentialList)
        val json = mGson.toJson(params)
        val arguments = mapOf(
            AppConstants.DID.VERIFY_VC_OFFLINE to json
        )
        val typeToken = object : TypeToken<BaseModel<Dwsdk301i.Response>>() {}.type
        val result: BaseModel<Dwsdk301i.Response>? = invokeMethod(methodName, AppConstants.DID.VERIFY_VC_OFFLINE, arguments, typeToken)
        return@withContext result
    }

    /**
     * Decoding the verifiable presentation from holder, and showing the arguments for requiring.
     */
    override suspend fun dwsdk401i(qrCode: String): BaseModel<Dwsdk401i.Response>? = withContext(Dispatchers.Main) {
        val methodName = "dwsdk-401i"
        val params = Dwsdk401i.Request(qrCode, BuildConfig.DID_ISSUER_URL)
        val json = mGson.toJson(params)
        val arguments = mapOf(
            AppConstants.DID.PARSE_VP_QRCODE to json
        )
        val typeToken = object : TypeToken<BaseModel<Dwsdk401i.Response>>() {}.type
        val result: BaseModel<Dwsdk401i.Response>? = invokeMethod(methodName, AppConstants.DID.PARSE_VP_QRCODE, arguments, typeToken)
        return@withContext result
    }

    /**
     * Verifying the verifiable presentation from holder.
     */
    override suspend fun dwsdk402i(vpToken: String, vpData: List<Dwsdk402i.Request.VPData>, didJson: String, customData: String?): Any? = withContext(Dispatchers.Main) {
        val methodName = "dwsdk-402i"
        val generateVP = Dwsdk402i.Request(vpToken, didJson, vpData, BuildConfig.DID_ISSUER_URL, customData)
        val json = mGson.toJson(generateVP)
        val arguments = mapOf(
            AppConstants.DID.GENERATE_VP to json
        )
        val typeToken = object : TypeToken<BaseModel<Any>>() {}.type
        val result: Any? = invokeMethod(methodName, AppConstants.DID.GENERATE_VP, arguments, typeToken)
        return@withContext result
    }

    /**
     * Decoding the verifiable credential to the informations.
     */
    override suspend fun dwsdk501i(credential: String): Dwsdk501i.Response? = withContext(Dispatchers.Main) {
        val methodName = "dwsdk-501i"
        val params = Dwsdk501i.Request(credential)
        val json = mGson.toJson(params)
        val arguments = mapOf(
            AppConstants.DID.DECODE_VC to json
        )
        val typeToken = object : TypeToken<BaseModel<Dwsdk501i.Response>>() {}.type
        val result: BaseModel<Dwsdk501i.Response>? = invokeMethod(methodName, AppConstants.DID.DECODE_VC, arguments, typeToken)
        return@withContext result?.data
    }

    /**
     * Downloading the list of issuer from holder.
     */
    override suspend fun dwsdk601i(): String? = withContext(Dispatchers.Main) {
        val methodName = "dwsdk-601i"
        val json = mGson.toJson(DownloadIssuers(AppConstants.Net.DID_ISSUER_URL, true))
        val arguments = mapOf(
            AppConstants.DID.DOWNLOAD_ISSUER_LIST to json
        )
        val typeToken = object : TypeToken<BaseModel<Any?>>() {}.type
        val response: BaseModel<Any?>? = invokeMethod(methodName, AppConstants.DID.DOWNLOAD_ISSUER_LIST, arguments, typeToken)
        val dataJson = response?.data?.let { mGson.toJson(it) }
        return@withContext dataJson
    }

    /**
     * Downloading the status list of verifiable credential from holder.
     */
    override suspend fun dwsdk602i(vcs: List<String>, vcList: String): String? = withContext(Dispatchers.Main) {
        val methodName = "dwsdk-602i"
        val verifiableCredentialList = mGson.fromJson(vcList, JsonArray::class.java)

        val params = Dwsdk602i.Request(vcs, verifiableCredentialList)
        val json = mGson.toJson(params)
        val arguments = mapOf(
            AppConstants.DID.DOWNLOAD_ALL_VC_LIST to json
        )
        val typeToken = object : TypeToken<BaseModel<List<Dwsdk602i.Response>?>>() {}.type
        val response: BaseModel<List<Dwsdk602i.Response>?>? = invokeMethod(methodName, AppConstants.DID.DOWNLOAD_ALL_VC_LIST, arguments, typeToken)
        val dataJson = response?.data?.let { mGson.toJson(it) }
        return@withContext dataJson
    }

    override suspend fun <RQ, RS> dwsdkModa101i(url: String, type: String, body: RQ, typeToken: TypeToken<RS>): DwsdkModa101i.Response<RS>? = withContext(Dispatchers.Main) {
        val methodName = "dwsdk-moda-101i"
        val finalURL = if (type == APIMethod.GET){
            buildGetUrl(url, body)
        }else{
            url
        }
        val json = if (body != null) {
            val rqJson = mGson.toJson(body)
            mGson.toJson(DwsdkModa101i.Request(finalURL, type, rqJson))
        } else {
            ""
        }
        val arguments = mapOf(
            AppConstants.DID.SEND_REQUEST to json
        )
        val baseTypeToken = object : TypeToken<BaseModel<Any?>>() {}
        val response: BaseModel<Map<String, String>?>? = invokeMethod(methodName, AppConstants.DID.SEND_REQUEST, arguments, baseTypeToken.type)

        val responseJson = response?.data?.get("response")
        if (responseJson == null) {
            val message = mContext.getString(R.string.unknown_error_reason)
            error(IdentifierException(response?.code, message))
        } else {
            val apiResponse: BaseModel<Any?> = mGson.fromJson(responseJson, baseTypeToken.type)

            if (apiResponse.code == "0") {
                val apiDataJson = mGson.toJson(apiResponse.data)
                val resultData: RS? = mGson.fromJson<RS?>(apiDataJson, typeToken.type)
                val result = DwsdkModa101i.Response(apiResponse.code, apiResponse.message, resultData)
                return@withContext result
            } else {
                // RS.code = 4碼數字，RS.message對應SDK錯誤代碼與中文訊息對應表，對應不到中文，則顯示”[RS.message]:不明錯誤原因”
                val error = SDKErrorEnum.entries.find { it.code == apiResponse.code }
                val errorMessage = if (error != null) {
                    mContext.getString(error.message)
                } else {
                    mContext.getString(R.string.format_unknown_error_reason)
                }
                error(IdentifierException(apiResponse.code, errorMessage))
            }
        }
    }

    override suspend fun <RQ, RS> dwsdkModa201i(url: String, payload: RQ, didFile: String, typeToken: TypeToken<RS>): DwsdkModa201i.Response<RS>? = withContext(Dispatchers.Main) {
        val methodName = "dwsdk-moda-201i"
        val json = if (payload != null) {
            val rqJson = mGson.toJson(payload)
            mGson.toJson(DwsdkModa201i.Request(url, rqJson, didFile))
        } else {
            ""
        }

        val arguments = mapOf(
            AppConstants.DID.SEND_JWT_REQUEST to json
        )
        val baseTypeToken = object : TypeToken<BaseModel<Any?>>() {}
        val response: BaseModel<String?>? = invokeMethod(methodName, AppConstants.DID.SEND_JWT_REQUEST, arguments, baseTypeToken.type)

        val responseJson = response?.data
        if (responseJson == null) {
            val message = mContext.getString(R.string.unknown_error_reason)
            error(IdentifierException(response?.code, message))
        } else {
            val apiResponse: BaseModel<Any?> = mGson.fromJson(responseJson, baseTypeToken.type)

            if (apiResponse.code == "0") {
                val apiDataJson = mGson.toJson(apiResponse.data)
                val resultData: RS? = mGson.fromJson<RS?>(apiDataJson, typeToken.type)
                val result = DwsdkModa201i.Response(apiResponse.code, apiResponse.message, resultData)
                return@withContext result
            } else {
                // RS.code = 4碼數字，RS.message對應SDK錯誤代碼與中文訊息對應表，對應不到中文，則顯示”[RS.message]:不明錯誤原因”
                val error = SDKErrorEnum.entries.find { it.code == apiResponse.code }
                val errorMessage = if (error != null) {
                    mContext.getString(error.message)
                } else {
                    mContext.getString(R.string.format_unknown_error_reason)
                }
                error(IdentifierException(apiResponse.code, errorMessage))
            }
        }
    }


    private suspend fun <RQ, RS> invokeMethod(methodName: String, method: String, arguments: RQ?, typeToken: Type): RS? {
        return suspendCancellableCoroutine { coroutine ->
            mMethodChannel.invokeMethod(method, arguments, object : MethodChannel.Result {
                override fun success(result: Any?) {
                    val responseJson = result.toString()
                    if (method == AppConstants.DID.DOWNLOAD_ISSUER_LIST || method == AppConstants.DID.DOWNLOAD_ALL_VC_LIST) {
                        parseInputstreaming(responseJson, coroutine)
                    } else {
                        parseJsonText<RS>(responseJson, coroutine, typeToken)
                    }
                }

                override fun error(errorCode: String, errorMessage: String?, errorDetails: Any?) {
                    coroutine.resumeWithException(SDKException(mContext.getString(R.string.service_unknown_error), "D$errorCode"))
                }

                override fun notImplemented() {
                    coroutine.resumeWithException(SDKException(mContext.getString(R.string.system_basic_unknown_error), "C9998"))
                }
            })
        }
    }

    private fun <RS> parseInputstreaming(text: String, coroutine: CancellableContinuation<RS?>) {
        val inputStream: InputStream = ByteArrayInputStream(text.toByteArray(Charsets.UTF_8))
        val type = object : TypeToken<BaseModel<Any?>>() {}
        val response = mGson.getAdapter(type).read(mGson.newJsonReader(InputStreamReader(inputStream, Charsets.UTF_8)))
        if (response is BaseModel<*>) {
            parseResult(response, coroutine)
        } else {
            coroutine.resumeWithException(SDKException(mContext.getString(R.string.system_basic_unknown_error), "C9998"))
        }
    }

    private fun <RS> parseJsonText(responseJson: String, coroutine: CancellableContinuation<RS?>, typeToken: Type) {
        val response = mGson.fromJson<RS>(responseJson, typeToken)
        if (response is BaseModel<*>) {
            parseResult(response, coroutine)
        } else {
            coroutine.resumeWithException(SDKException(mContext.getString(R.string.system_basic_unknown_error), "C9998"))
        }
    }

    private fun <RS> parseResult(response: BaseModel<*>, coroutine: CancellableContinuation<RS?>) {
        if (response.code == "0") {
            coroutine.resumeWith(Result.success(response as? RS))
        } else if (response.code == "2" && ((response.message?.toIntOrNull() ?: -1) in 201..599)) {
            // RS.code = 2，且RS.message為201~599之間顯示連線失敗訊息
            val returnCode = "F" + when (response.message?.take(1)) {
                "2" -> "A"
                "3" -> "B"
                "4" -> "C"
                "5" -> "D"
                else -> "S"
            } + response.message?.drop(1)
            coroutine.resumeWithException(IdentifierException(returnCode, mContext.getString(R.string.connection_error_try_again)))
        } else if (response.code?.length == 4) {
            // 如果錯誤代碼是4012且是401i，則跳出信任單位警告視窗並且繼續往下走
            if (response.code == SDKErrorEnum.ERROR_4012.code && response.data is Dwsdk401i.Response) {
                coroutine.resumeWith(Result.success(response as? RS))
                return
            }
            // RS.code = 4碼數字，RS.message對應SDK錯誤代碼與中文訊息對應表，對應不到中文，則顯示”[RS.message]:不明錯誤原因”
            val error = SDKErrorEnum.entries.find { it.code == response.message }
            val errorMessage = if (error != null) {
                mContext.getString(error.message)
            } else {
                mContext.getString(R.string.format_unknown_error_reason)
            }
            val errorCode = if (error != null) {
                "${response.code}-${response.message}"
            } else {
                if (response.message?.toIntOrNull() != null) {
                    "${response.code}-${response.message}"
                } else {
                    response.code
                }
            }
            coroutine.resumeWithException(IdentifierException(errorCode, errorMessage))
        } else if (mWalletRepository.getIsAddVerifiableCredentialResultFullPage()) {
            coroutine.resumeWith(Result.success(response as? RS))
        } else {
            if (response.data is Dwsdk301i.Response) {
                coroutine.resumeWith(Result.success(response as? RS))
            } else {
                val message = mContext.getString(R.string.unknown_error_reason)
                coroutine.resumeWithException(IdentifierException(response.code, message))
            }
        }
    }

    private fun <T> buildGetUrl(baseUrl: String, params: T): String {
        val gson = GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER).create()
        val json = gson.toJson(params)

        // JSON → Map<String, Any>
        val type = object : TypeToken<Map<String, Any>>() {}.type
        val map: Map<String, Any> = gson.fromJson(json, type)

        // 組成 query string（自動 URL 編碼）
        val queryString = map.entries.joinToString("&") { (key, value) ->
            "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value.toString(), "UTF-8")}"
        }

        // 判斷 baseUrl 是否已有 ?，避免重複
        val separator = if (baseUrl.contains("?")) "&" else "?"
        return "$baseUrl$separator$queryString"
    }
}