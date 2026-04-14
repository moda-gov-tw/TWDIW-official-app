package tw.gov.moda.digitalwallet.core.verifiable

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.core.net.toUri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.core.identifier.IdentifierManager
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.data.annotation.APIMethod
import tw.gov.moda.digitalwallet.data.annotation.APIMethod.Companion.GET
import tw.gov.moda.digitalwallet.data.db.Issuer
import tw.gov.moda.digitalwallet.data.db.VerifiableCredential
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.digitalwallet.data.element.CardStatusEnum
import tw.gov.moda.digitalwallet.data.model.AddCredential
import tw.gov.moda.digitalwallet.data.model.BaseModel
import tw.gov.moda.digitalwallet.data.model.DIDData
import tw.gov.moda.digitalwallet.data.model.IssuerDid
import tw.gov.moda.digitalwallet.data.model.dwmoda.DwModa201i
import tw.gov.moda.digitalwallet.data.model.dwmoda.DwModa301i
import tw.gov.moda.digitalwallet.data.model.dwmoda.DwModa401i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk301i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk401i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk402i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk501i
import tw.gov.moda.digitalwallet.data.model.dwsdk.moda.DwsdkModa101i
import tw.gov.moda.digitalwallet.data.model.dwverifier.DwVerifierMock101i
import tw.gov.moda.digitalwallet.extension.sha256
import tw.gov.moda.digitalwallet.util.BitmapUtil
import tw.gov.moda.diw.BuildConfig
import tw.gov.moda.diw.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.security.KeyStore
import java.security.SecureRandom
import java.text.Collator
import java.util.Locale
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.TrustManagerFactory

/**
 * 可驗證憑證管理器實作類別
 *
 * 負責處理與可驗證憑證 (Verifiable Credential, VC) 相關的各種操作，
 *
 * @property mContext Android 應用程式上下文，用於獲取資源 (如字串) 或其他系統服務。
 * @property mPref SharedPreferences 實例。
 * @property mGson Gson 實例，用於 JSON 數據的序列化和反序列化。
 * @property mWalletRepository 皮夾數據倉庫，提供對當前皮夾資訊的存取。
 * @property mIdentifierManager 身份識別管理器，用於處理 DID 相關的操作或解析。
 *
 * @constructor 依賴注入 [Context], [ModaSharedPreferences], [Gson], [WalletRepository], [IdentifierManager]。
 */
class VerifiableManagerImpl constructor(
    private val mContext: Context,
    private val mPref: ModaSharedPreferences,
    private val mGson: Gson,
    private val mWalletRepository: WalletRepository,
    private val mIdentifierManager: IdentifierManager
) : VerifiableManager {

    // 用於記錄上次因 Exception 而重試的圖片 URL，避免無限遞迴重試
    private var mRetryImageURL = ""

    /**
     * 獲取或創建皮夾的去中心化身份識別 (Decentralized Identifier, DID)。
     *
     * 首先會嘗試使用皮夾的 `keyTag` 通過 `mIdentifierManager.dwsdk103i` 來設定或載入現有的身份識別DID。
     *
     * 檢查皮夾物件[Wallet]的 `did` 和 `pair`是否為空。
     * 如果任一為空，表示需要創建新的 DID 和金鑰對：
     * 1. 調用 dwsdk-101i 來生成一個新的金鑰對。
     * 2. 將生成的金鑰對序列化為 JSON 字串並儲存到。
     * 3. 從金鑰對中獲取公鑰。
     * 4. 使用公鑰調用 dwsdk-102i 來生成對應的 DID 數據。
     * 5. 將生成的 DID 數據序列化為 JSON 字串並儲存到 `wallet.did`。
     **
     * @param wallet 要為其獲取或創建 DID 的皮夾物件。
     * @return 更新（或未變更）後的皮夾物件。
     */
    override suspend fun createDecentralizedIdentifier(wallet: Wallet): Wallet = withContext(Dispatchers.IO) {
        // 通知Flutter SDK 載入與 wallet.keyTag 相關的身份識別環境/密鑰。
        mIdentifierManager.dwsdk103i(wallet.keyTag)

        // 檢查皮夾是否已經有 DID 和金鑰對。
        // 如果 wallet.did 或 wallet.pair 是空白的，則需要創建新的。
        if (wallet.did.isBlank() || wallet.pair.isBlank()) {
            // 1. 生成新的金鑰對(dwsdk-101i) 預期返回 Pair 物件。
            mIdentifierManager.dwsdk101i()?.also { response ->
                response.publicKey?.also { publicKey ->
                    // 2. 將公鑰對序列化為 JSON 並儲存到皮夾。
                    wallet.pair = mGson.toJson(publicKey).sha256()

                    // 3. dwsdk-102i 使用公鑰生成一個 DID 數據物件。
                    mIdentifierManager.dwsdk102i(publicKey)?.also { didData ->
                        // 4. 將 DID 數據序列化為 JSON 並儲存到皮夾的did。
                        wallet.did = mGson.toJson(didData)
                    } ?: error(mContext.getString(R.string.unknow_reason))
                } ?: error(mContext.getString(R.string.unknow_reason))
            } ?: error(mContext.getString(R.string.unknow_reason))
        }

        // 返回更新後的皮夾物件。
        return@withContext wallet
    }

    /**
     * 根據提供的皮夾資訊和 QR Code 內容(DeepLink)創建一個新的憑證 (Verifiable Credential, VC)。
     *
     * 此函數執行以下主要步驟：
     * 1. 調用 `mIdentifierManager.dwsdk201i` 方法，傳入驗證碼、QR Code 內容、DID 和金鑰對，
     *    以申請或獲取 VC 的核心數據 (`applyVCData`)。這一步通常涉及與發行方或相關服務的交互。
     * 2. 如果成功獲取到 `applyVCData`：
     *    a. 嘗試從 `applyVCData` 中提取卡片背景圖片的 URL。
     *    b. 調用 `getImageBase64` 方法下載該圖片並將其轉換為 Base64 編碼。
     *    c. 如果成功獲取圖片 Base64，將其轉換為 Bitmap。
     *    d. 如果 Bitmap 創建成功，則在 `Dispatchers.Default` 上下文中將圖片壓縮到 500KB 以內，
     *       並將壓縮後的 Bitmap 重新轉換為 Base64 編碼。
     *    e. 使用獲取到的 VC 數據和處理後的圖片 Base64，創建一個新的 `VerifiableCredential` 物件。
     *
     * @param wallet 當前使用者的皮夾物件
     * @param qrcode 從 QR Code 中掃描到的內容字串(DeepLink)。
     * @return 如果成功創建，則返回一個新的 [VerifiableCredential] 物件，失敗則返回 `null`
     */
    override suspend fun createVerifiableCredential(wallet: Wallet, qrcode: String): VerifiableCredential? = withContext(Dispatchers.IO) {
        // 從皮夾中獲取 DID 和金鑰對的 JSON 字串
        val didJson = wallet.did

        // 步驟 1: 調用 dwsdk201i 申請或獲取 VerifiableCredential 數據
        // mWalletRepository.getVerificationCode() 為使用者 otp
        val applyVCData = mIdentifierManager.dwsdk201i(mWalletRepository.getVerificationCode(), qrcode, didJson)

        // 步驟 2: 檢查 applyVCData 是否成功獲取
        return@withContext if (applyVCData != null) {
            // 提取卡片背景圖片
            var imageBase64 = getImageBase64(applyVCData.credentialDefinition?.displayArray?.firstOrNull()?.backgroundImage?.uri ?: "")

            // 將卡片背景圖片 Base64 圖片轉換為 Bitmap
            val bitmap = BitmapUtil.createBitmap(imageBase64) // 假設 BitmapUtil.createBitmap 可以處理空字串的 imageBase64

            // 如果 Bitmap 創建成功，則壓縮圖片到500KB內
            if (bitmap != null) {
                // 在 Default 執行緒中執行圖片壓縮，因為這是一個 CPU 密集型操作
                val compressBitmap = withContext(Dispatchers.Default) {
                    BitmapUtil.compress(bitmap, 500 * 1024) // 壓縮目標大小為 500KB
                }
                // 將壓縮後的 Bitmap 重新轉換為 Base64
                imageBase64 = BitmapUtil.bitmap2Base64(compressBitmap)
            }


            // 創建 VerifiableCredential 物件
            VerifiableCredential(
                uid = 0L, // 通常在存入資料庫時由資料庫自動生成，此處為初始值
                walletId = mWalletRepository.getWallet()?.uid ?: -1L, // 獲取當前皮夾的 ID，如果失敗則為 -1L
                display = applyVCData.credentialDefinition?.displayArray?.firstOrNull()?.name ?: "", // VC 的顯示名稱
                issuingUnit = "", // 發行單位，此處初始為空，可能後續填充
                credential = applyVCData.credential ?: "", // VC 的核心 JWT 或 JSON-LD 字串
                types = applyVCData.credentialDefinition?.credentialDefinitionObj?.type ?: arrayOf(), // VC 的類型陣列
                credentialSubject = applyVCData.credentialDefinition?.credentialDefinitionObj?.credentialSubject ?: linkedMapOf(), // VC 的主題內容
                imageBase64 = imageBase64, // 處理後的卡片圖片 Base64 字串
                description = applyVCData.credentialDefinition?.displayArray?.firstOrNull()?.description ?: "", // VC 的描述
                updateDatetime = System.currentTimeMillis() // 創建或最後更新的時間戳
            )
        } else {
            // 如果 applyVCData 為 null，表示申請 VC 失敗，返回 null
            null
        }
    }

    /**
     * 驗證指定的可驗證憑證 (Verifiable Credential, VC) 的狀態。
     * @param wallet 當前使用者的皮夾物件。
     * @param vc 要進行狀態驗證的[VerifiableCredential]物件。
     * @return 返回一個 `BaseModel<Dwsdk301i>` 物件，如果在調用驗證服務的過程中發生異常，則返回 `null`。
     */
    override suspend fun verifyVerifiableCredential(wallet: Wallet, vc: VerifiableCredential): BaseModel<Dwsdk301i.Response>? {
        try {
            // 從錢包物件中獲取 DID 的 JSON 字串
            val didJson = wallet.did

            // 調用 IdentifierManager 的 dwsdk-301i 方法執行 VC 狀態驗證
            val response = mIdentifierManager.dwsdk301i(vc.credential, didJson)

            // 直接返回 dwsdk-301i 的回應結果
            return response
        } catch (e: Exception) {
            // 如果在調用 dwsdk301i 的過程中發生任何異常 (例如網路錯誤、服務端錯誤等)，返回 null 表示驗證操作失敗
            return null
        }
    }

    /**
     * 解析 VerifiableCredential 的內容
     * @param credential VerifiableCredential的憑證
     * @return 返回一個 `Dwsdk501i` 物件，如果在調用驗證服務的過程中發生異常，則返回 `null`。
     */
    override suspend fun getVerifiableCredential(credential: String): Dwsdk501i.Response? = withContext(Dispatchers.IO) {
        // 調用 IdentifierManager 的 dwsdk-501i 方法解析VerifiableCredential
        return@withContext mIdentifierManager.dwsdk501i(credential)
    }

    /**
     * 解析 VerifiablePresentation 的內容
     * @param qrcode 從 QR Code 中掃描到的內容字串(DeepLink)。
     * @return 返回一個 `Dwsdk401i` 物件，如果在調用驗證服務的過程中發生異常，則返回 `null`。
     */
    override suspend fun parseVerifiablePresentation(qrcode: String): BaseModel<Dwsdk401i.Response>? {
        // 調用 IdentifierManager 的 dwsdk-401i 方法解析VerifiablePresentation
        return mIdentifierManager.dwsdk401i(qrcode)
    }

    /**
     * 取讀業務端URL
     */
    override suspend fun getIssuerServiceData(vcUid: String, mode: String): DwModa201i.VCResponse? {
        val type = object : TypeToken<DwModa201i.VCResponse>() {}
        val body = DwModa201i.Request(mode = mode)
        val response = mIdentifierManager.dwsdkModa101i(
            url = BuildConfig.DID_ISSUER_URL + DwModa201i.URL_PATH + vcUid,
            type = GET,
            body = body,
            typeToken = type
        )?.data
        return response
    }

    override suspend fun getVerifiableServiceData(vpUid: String, mode: String): DwModa201i.VPResponse? {
        val type = object : TypeToken<DwModa201i.VPResponse>() {}
        val body = DwModa201i.Request(mode = mode)
        val response = mIdentifierManager.dwsdkModa101i(
            url = BuildConfig.DID_ISSUER_URL + DwModa201i.URL_PATH + vpUid,
            type = GET,
            body = body,
            typeToken = type
        )?.data
        return response
    }

    override suspend fun getVerifiableServiceDeepLink(verifierServiceUrl: String, vpUid: String): DwVerifierMock101i.Response? {
        val type = object : TypeToken<DwVerifierMock101i.Response>() {}
        val body = DwVerifierMock101i.Request(vpUid = vpUid)
        val response = mIdentifierManager.dwsdkModa101i(
            url = "$verifierServiceUrl/$vpUid",
            type = GET,
            body = body,
            typeToken = type
        )?.data
        return response
    }

    /**
     * 授權 VerifiablePresentation
     * @param wallet 當前使用者的皮夾物件。
     * @param vpToken VerifiablePresentation的Token
     * @param vpData 欲授權卡片欄位資料
     */
    override suspend fun verifyVerifiablePresentation(wallet: Wallet, vpToken: String, vpData: List<Dwsdk402i.Request.VPData>, customData: String?) {
        val didJson = wallet.did
        // 調用 IdentifierManager 的 dwsdk-402i 方法授權 VerifiablePresentation
        mIdentifierManager.dwsdk402i(vpToken, vpData, didJson, customData)
    }

    /**
     * 判斷 [VerifiableCredential] 的狀態。
     *
     * 此函數根據傳入的驗證結果 `data` (dwsdk-301i or dwsdk-302i)，
     * 來更新 `verifiableCredential` 物件的狀態 (`status`)、失效原因 (`invalidReason`) 和信任徽章 (`trustBadge`)。
     *
     * @param wallet 當前使用者的皮夾物件。
     * @param verifiableCredential 要判斷狀態的可驗證憑證物件。此物件會被直接修改。
     * @param data 包含憑證驗證結果的數據模型。`data.code` 表示驗證操作的成功或失敗，
     *             `data.data` 包含更詳細的驗證狀態布林值 (如 vc, trust, issuer, holder, exp, trust_badge)。
     * @return 更新狀態後的 [VerifiableCredential] 物件。
     */
    override suspend fun detectVerifiableCredentialStatus(wallet: Wallet, verifiableCredential: VerifiableCredential, data: BaseModel<Dwsdk301i.Response>): VerifiableCredential {
        // 預設將卡片狀態設為有效，並清空失效原因
        verifiableCredential.status = CardStatusEnum.Valid
        verifiableCredential.invalidReason = ""

        // 處理信任徽章 (藍勾勾)
        // 如果 data.data (詳細驗證結果) 不為空，則更新信任徽章狀態
        data.data?.also { detailData ->
            verifiableCredential.trustBadge = detailData.trust_badge ?: false // 如果 trust_badge 為 null，則預設為 false
        }

        // 判斷驗證結果碼 data.code
        if (data.code != "0") {
            // 如果驗證結果碼不為 "0"，表示憑證可能存在問題
            data.data?.also { detailData ->
                // 根據詳細的布林狀態判斷失效原因
                if (detailData.vc == false) { // 憑證本身無效
                    verifiableCredential.invalidReason = mContext.getString(R.string.vc_invalid)
                } else if (detailData.trust == false) { // 發行者不受信任
                    verifiableCredential.invalidReason = mContext.getString(R.string.untrustworthy_issuer)
                } else if (detailData.issuer == false) { // 發行者無效
                    verifiableCredential.invalidReason = mContext.getString(R.string.issuer_signature_invalid)
                } else if (detailData.holder == false) { // 持有者 DID 不一致
                    verifiableCredential.invalidReason = mContext.getString(R.string.holder_did_inconsistent)

                    // 特殊處理：當 holder 為 false 時，記錄 VC 的持有者 ID 和皮夾的持有者 ID，以便追蹤不一致的原因
                    // 這裡呼叫 dwsdk501i 可能是為了獲取 VC 中更詳細的持有者資訊 (sub)
                    mIdentifierManager.dwsdk501i(verifiableCredential.credential)?.also { vcHolderData ->
                        // 解析皮夾中的 DID 資訊
                        mGson.fromJson(wallet.did, DIDData::class.java)
                    }
                }

                // 如果經過上述判斷後，仍然沒有找到明確的失效原因
                if (verifiableCredential.invalidReason.isBlank()) {
                    // 檢查是否因為憑證過期
                    if (!detailData.exp) { // exp 為 false 表示已過期
                        verifiableCredential.status = CardStatusEnum.Expired // 設定狀態為已過期
                        verifiableCredential.invalidReason = mContext.getString(R.string.card_has_expired)
                    } else {
                        // 如果不是上述任何已知原因，且未過期，則設為未知狀態
                        verifiableCredential.status = CardStatusEnum.Unknown
                        verifiableCredential.invalidReason = mContext.getString(R.string.unknow_reason)
                    }
                } else {
                    // 如果已經設定了失效原因，則將卡片狀態設為無效
                    verifiableCredential.status = CardStatusEnum.Invalid
                }
            } ?: run {
                // 如果 data.data (詳細驗證結果) 為空，但 data.code 不是 "0"，
                // 這可能表示一個未預期的錯誤或無法獲取詳細狀態，將卡片狀態設為未知
                verifiableCredential.status = CardStatusEnum.Unknown
                verifiableCredential.invalidReason = mContext.getString(R.string.unknow_reason)
            }

        } else {
            // 如果 data.code 為 "0"，表示憑證驗證通過，狀態為有效
            verifiableCredential.status = CardStatusEnum.Valid
            verifiableCredential.invalidReason = "" // 清空失效原因
        }

        // 返回更新後的 verifiableCredential 物件
        return verifiableCredential
    }


    /**
     * 取得 issuer 的清單
     * @return dwsdk-601i 的 Response JSON
     */
    override suspend fun getIssuerList(): String? {
        // 調用 IdentifierManager 的 dwsdk-601i 方法取得 issuer 清單的 JSON 資料
        return mIdentifierManager.dwsdk601i()
    }

    /**
     * 根據已儲存的 VerifiableCredential 列表，透過 dwsdk-602i 獲取憑證清單 JSON 字串。
     * @param vcs [VerifiableCredential] 物件的列表。
     * @return 返回 dwsdk-602i JSON 字串，則此函數也會返回 `null`。
     */
    override suspend fun getAllVerifiableCredentialList(vcs: List<VerifiableCredential>, vcList: String): String? {
        // 檢查傳入的 VerifiableCredential 列表是否為空
        if (vcs.isEmpty()) {
            // 如果沒有任何 VC，直接返回一個表示空 JSON 陣列的字串
            return "[]"
        }

        // 如果列表不為空，返回 dwsdk-602i JSON 字串：
        // 1. 使用 filter 操作提取目前有效的憑證
        // 2. 使用 map 操作提取每個 VerifiableCredential 物件的 credential 字串。
        // 3. 將這個 credential 字串列表調用 IdentifierManager.dwsdk602i()。
        return vcs.filter { it.status != CardStatusEnum.Invalid || it.status != CardStatusEnum.Expired }
            .map { it.credential }
            .let { credentialList ->
                mIdentifierManager.dwsdk602i(credentialList, vcList)
            }
    }

    /**
     * 離線驗證指定的憑證 (Verifiable Credential, VC) 的狀態。

     * @param wallet 當前使用者的錢包物件。
     * @param vc 要進行離線狀態驗證的可驗證憑證物件。
     * @return 返回一個 `BaseModel<Dwsdk301i>` 物件，如果在調用驗證服務的過程中發生異常，則返回 `null`。
     */
    override suspend fun verifyVerifiableCredentialOffline(wallet: Wallet, vc: VerifiableCredential): BaseModel<Dwsdk301i.Response>? {
        // 1. 從錢包物件中獲取 DID 的 JSON 字串
        val didJson = wallet.did

        // 2. 從 SharedPreferences 中獲取已儲存的發行者列表 JSON 字串
        // 如果 mPref.issuerList 為 null，則使用 "[]" 作為預設值
        val issuerListJson = mPref.issuerList ?: "[]"

        // 3. 從 SharedPreferences 中獲取與當前錢包關聯的 VC 列表 JSON 字串
        val verifiableCredentialListJson = mWalletRepository.getWallet()?.uid?.let { uid ->
            // 構建用於儲存特定錢包 VC 列表的 SharedPreferences 鍵名
            val keyName = AppConstants.PREF.WALLET_VC_LIST_PREFIX + uid
            // 從 SharedPreferences 中讀取字串，如果不存在則預設為 "[]"
            mPref.getSharedPreferences().getString(keyName, "[]")
        } ?: "[]"

        // 4. 調用 IdentifierManager 的 dwsdk-302i 方法執行離線 VC 狀態驗證
        val key = vc.credential.sha256()
        if (verifiableCredentialListJson.contains(key)) {
            // 調用 IdentifierManager 的 dwsdk-302i 方法執行離線 VC 狀態驗證
            return mIdentifierManager.dwsdk302i(vc.credential, didJson, issuerListJson, verifiableCredentialListJson)
        } else {
            // 沒有對應的憑證清冊則直接回傳null
            return null
        }
    }

    /**
     * 從儲存的 issuer 清單 JSON 字串中解析發行單位名稱。
     *
     * 此函數會讀取儲存在 SharedPreferences 中的發行者列表 (預期為 JSON 陣列格式的字串)。
     * 對於列表中的每個發行者：
     * 1. 解析其JWT資料，取出第二部分(用 '.' 分隔)。
     * 2. 對該部分進行 Base64 URL 安全解碼，並轉換為 UTF-8 字串 (預期為另一個 JSON 字串)。
     * 3. 解析這個解碼後的 JSON 字串，獲取其中的 `id` 欄位。
     * 4. 對此 `id` 進行 SHA-256 雜湊。
     * 5. 將 SHA-256 雜湊值作為鍵 (Key)，發行者的組織名稱作為值 (Value) 存入 HashMap。
     *    如果發行單位名稱不存在，則使用預設的 "未知" 字串。
     *
     * @return 如果成功解析，則返回一個包含 DID.id (SHA-256)的 HashMap，對應發行單位名稱。
     */
    override suspend fun parseIsserListToName(): HashMap<String, String>? {
        // 從 SharedPreferences 獲取 issuerList 字串，並確保字串是一個 JSON 陣列
        mPref.issuerList?.takeIf { it.startsWith("[") }?.also { issuerListJsonString ->
            // 初始化一個 HashMap 用於存放 DID 到名稱的映射
            val hashMap = HashMap<String, String>()

            // 使用 Gson 將 JSON 字串解析為 Issuer 物件陣列
            mGson.fromJson(issuerListJsonString, Array<Issuer>::class.java).forEach { issuer ->
                // 解析 issuer 的 DID
                // 1. issuer.did.split(".")：將 DID 字串按 "." 分割成多個部分
                // 並且 getOrNull(1)：安全地獲取分割後的第二個元素 (索引為 1)。如果不存在則返回 null。
                issuer.did.split(".").getOrNull(1)?.let { base64EncodedPayload ->
                    // 2. Base64.decode(it, Base64.URL_SAFE)：對獲取到的部分進行 Base64 URL 安全解碼
                    Base64.decode(base64EncodedPayload, Base64.URL_SAFE)
                }?.let { decodedBytes ->
                    // String(it, Charsets.UTF_8)：將解碼後的位元組陣列轉換為 UTF-8 字串 (預期是 JSON 格式)
                    String(decodedBytes, Charsets.UTF_8)
                }?.also { didDocumentJson ->
                    // 使用 Gson 將 JSON 字串解析為 IssuerDid 物件
                    // 3. 解析這個解碼後的 JSON 字串，獲取其中的 `id` 欄位。
                    mGson.fromJson(didDocumentJson, IssuerDid::class.java).id?.also { idFromDidDocument ->
                        // 4. 對此 `id` 進行 SHA-256 雜湊。
                        // 5. 將 SHA-256 雜湊值作為鍵 (Key)，發行者的組織名稱作為值 (Value) 存入 HashMap。
                        //    如果發行單位名稱不存在，則使用預設的 "未知" 字串。
                        hashMap[idFromDidDocument.sha256()] = issuer.org?.name ?: mContext.getString(R.string.unmatch)
                    }
                }
            }
            // 如果成功遍歷並處理了所有 issuer，則返回填充好的 hashMap
            return hashMap
        }
        // 如果 SharedPreferences 的issuerList 為 null，或不是以 "[" 開頭，則直接返回 null
        return null
    }

    /**
     * 取得 issuer 名稱
     *
     * @param issuerMap 已解析的 issuer 清單名稱
     * @param decodeVCData 已解析的 VerifiableCredential
     * @return 發行單位的名稱
     */
    override suspend fun getIssuerName(issuerMap: HashMap<String, String>?, decodeVCData: Dwsdk501i.Response): String {
        if (issuerMap != null && decodeVCData.iss != null) {
            return issuerMap.get(decodeVCData.iss.sha256()) ?: mContext.getString(R.string.unmatch)
        } else {
            return mContext.getString(R.string.unknow)
        }
    }

    /**
     * 取得 出示憑證頁 常用情境清單
     *
     * @param page 頁碼
     * @param size 每頁顯示筆數
     * @param name 情境名稱關鍵字
     * @return 常用情境清單
     */
    override suspend fun getShowCredentialList(page: Int, size: Int, name: String?): DwsdkModa101i.Response<DwModa401i.Response>? {
        val url = BuildConfig.DID_ISSUER_URL + DwModa401i.URL_PATH
        val body = DwModa401i.Request(page, size, name, null)
        val typeToken = object : TypeToken<DwModa401i.Response>() {}
        val result = mIdentifierManager.dwsdkModa101i(url, GET, body, typeToken)
        return result
    }

    /**
     * 排序 出示憑證頁 常用情境清單
     * 1. 數字排序
     * 2. 英文排序
     * 3. 中文筆畫排序
     * @param list 出示憑證頁 常用情境清單
     * @return 已排序清單
     */
    override suspend fun sortShowCredentialList(list: List<DwModa401i.Response.VPItem>): List<DwModa401i.Response.VPItem> {
        val getCategory: (String?) -> Int = category@{ text ->
            val firstChar = text?.firstOrNull() ?: return@category 3
            return@category when {
                firstChar.isDigit() -> 0
                firstChar.isLetter() && firstChar.code in 0..127 -> 1
                else -> 2
            }
        }

        val collator = Collator.getInstance(Locale.TRADITIONAL_CHINESE)
        val sortedList = list.sortedWith(
            compareBy<DwModa401i.Response.VPItem> { getCategory(it.name) }
                .thenComparator { a, b -> collator.compare(a.name, b.name) }
        )

        return sortedList
    }

    /**
     * 取得 加入憑證頁 快速授權列表
     *
     * @param page 頁碼
     * @param size 每頁顯示筆數
     * @param name 憑證名稱關鍵字
     * @return 快速授權列表
     */
    override suspend fun getAddCredentialList(page: Int, size: Int, name: String?): AddCredential = withContext(Dispatchers.IO) {
        val type = object : TypeToken<DwModa301i.Response>() {}
        val response = mIdentifierManager.dwsdkModa101i(
            url = BuildConfig.DID_ISSUER_URL + DwModa301i.URL_PATH,
            type = GET,
            body = DwModa301i.Request(page, size, name, null),
            typeToken = type
        )?.data
        val vcItems = response?.vcItems?.map { item ->
            AddCredential.VCItem(
                item.vcUid,
                item.name,
                item.type,
                item.issuerServiceUrl,
                item.logoUrl
            )
        }
        val list = AddCredential(
            vcItems = vcItems,
            currentPage = response?.currentPage,
            pageSize = response?.pageSize,
            totalItems = response?.totalItems,
            totalPages = response?.totalPages
        )

        return@withContext list
    }

    override suspend fun loadImageBitmapForList(url: String): Bitmap? {
        if (url.isBlank()) return null

        val cacheKey = url.sha256()
        val cachedBase64 = mWalletRepository.getImageCache(cacheKey)

        val base64 = if (cachedBase64 != null) {
            cachedBase64
        } else {
            val downloaded = getImageBase64(url)
            if (downloaded.isNotEmpty()) {
                mWalletRepository.putImageCache(cacheKey, downloaded)
            }
            downloaded
        }

        val bitmap = BitmapUtil.createBitmap(base64)
        return withContext(Dispatchers.Default) {
            bitmap?.let { BitmapUtil.compress(it, 500 * 1024) }
        }
    }

    /**
     * 排序 加入憑證頁 快速授權列表
     * 1. 數字排序
     * 2. 英文排序
     * 3. 中文筆畫排序
     * @param list 加入憑證頁 快速授權列表
     * @return 已排序清單
     */
    override suspend fun sortAddCredentialList(list: List<AddCredential.VCItem>): List<AddCredential.VCItem> {
        val getCategory: (String?) -> Int = category@{ text ->
            val firstChar = text?.firstOrNull() ?: return@category 3
            return@category when {
                firstChar.isDigit() -> 0
                firstChar.isLetter() && firstChar.code in 0..127 -> 1
                else -> 2
            }
        }

        val collator = Collator.getInstance(Locale.TRADITIONAL_CHINESE)
        val sortedList = list.sortedWith(
            compareBy<AddCredential.VCItem> { getCategory(it.name) }
                .thenComparator { a, b -> collator.compare(a.name, b.name) }
        )

        return sortedList
    }

    /**
     * 從指定的 URL 獲取圖片，並將其轉換為 Base64 編碼的字串。
     *
     * 此函數會嘗試通過 HTTPS 連線下載圖片。
     *
     * 重試機制：如果第一次嘗試因為 SSLHandshakeException 失敗 (可能與 TLS 版本有關)，
     * 它會嘗試將 TLS 版本從 TLSv1.3 改到 TLSv1.2 並重試一次。
     *
     * @param imageUrl 圖片的 URL 網址。
     * @return 成功時返回圖片的 Base64 編碼字串 (不包含 "data:image/...;base64," 前綴)。
     *         如果 URL 為空、下載失敗或發生其他異常，則返回空字串。
     */
    private suspend fun getImageBase64(imageUrl: String): String = withContext(Dispatchers.IO) {
        var result: String // 用於儲存最終的 Base64 字串結果
        var inputStreamReader: InputStreamReader? = null // 用於讀取網路回應的輸入流

        try {
            // 如果圖片 URL 為空，直接返回空字串
            if (imageUrl.isBlank()) {
                return@withContext ""
            }

            // 建立 URL 物件
            val url = URL(imageUrl)
            val httpConnection = withContext(Dispatchers.IO) {
                url.openConnection()
            } as HttpsURLConnection // 強制轉換為 HttpsURLConnection

            // 載入 Android 系統的 CA 憑證庫
            val systemCAKeyStore = KeyStore.getInstance("AndroidCAStore").apply {
                load(null, null) // 使用 null 表示載入系統預設的 CA 憑證
            }

            // 初始化 TrustManagerFactory，使用系統 CA 憑證庫
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(systemCAKeyStore)

            // 根據是否為重試機制來決定使用的 SSLContext (TLS 版本)
            val sslContext = if (mRetryImageURL == imageUrl) {
                // 如果當前 URL 與上次重試的 URL 相同，表示這是第二次嘗試 (降級 TLS)
                SSLContext.getInstance("TLSv1.2")
            } else {
                // 第一次嘗試，使用 TLSv1.3
                SSLContext.getInstance("TLSv1.3")
            }

            // 初始化 SSLContext，使用系統的 TrustManagers
            sslContext.init(null, trustManagerFactory.trustManagers, SecureRandom())

            // 將自訂的 SSLSocketFactory 設定給 HttpsURLConnection
            httpConnection.sslSocketFactory = sslContext.socketFactory
            // 設定連線逾時為 10 秒
            httpConnection.connectTimeout = 10000

            httpConnection.connect()

            //  使用 BufferedReader(InputStreamReader) 讀取輸入流
            inputStreamReader = InputStreamReader(httpConnection.inputStream)
            val reader = BufferedReader(inputStreamReader) // 使用 BufferedReader 提高讀取效率
            val strBuilder = StringBuilder()

            // 逐行讀取Response內容
            reader.forEachLine { line ->
                strBuilder.append(line)
            }

            // 假設Response是內容格式 (例如 "data:image/png;base64,iVBOR...")
            // 提取逗號之後的 Base64 編碼部分
            // 如果沒有逗號，返回空字串
            val data = strBuilder.toString().substringAfter(",", "")


            // 關閉 reader (inputStreamReader 也會被間接關閉)
            reader.close()

            // 將提取到的 Base64 數據賦值給 result
            result = data
        } catch (e: SSLHandshakeException) {
            // 捕獲 SSL 握手異常
            if (mRetryImageURL == imageUrl) {
                // 如果當前 URL 與上次重試的 URL 相同，表示已經重試過一次了，不再重試
                // 清空重試 URL 標記，並返回空字串表示失敗
                mRetryImageURL = ""
                result = ""
            } else {
                // 第一次遇到 SSLHandshakeException，記錄當前 URL 以便下次降級 TLS 版本重試
                mRetryImageURL = imageUrl
                // 遞迴呼叫自身進行重試
                return@withContext getImageBase64(imageUrl)
            }
        } catch (e: Exception) {
            // 捕獲其他所有類型的異常，返回空字串表示失敗
            result = ""
        } finally {
            // 無論成功或失敗，最後都嘗試關閉 inputStreamReader
            // 這是為了確保資源被釋放
            try {
                inputStreamReader?.close()
            } catch (_: IOException) {

            }
        }
        // 如果成功重試並獲取到數據，在成功返回前清除重試標記
        if (result.isNotEmpty() && mRetryImageURL == imageUrl) {
            mRetryImageURL = ""
        }

        // 返回最終的 Base64 字串結果
        return@withContext result
    }
}