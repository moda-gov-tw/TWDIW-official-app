package tw.gov.moda.digitalwallet.core.deeplink

import androidx.core.net.toUri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.core.db.DigitalWalletDB
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.core.verifiable.VerifiableManager
import tw.gov.moda.digitalwallet.data.db.OperationRecord
import tw.gov.moda.digitalwallet.data.db.VerifiableCredential
import tw.gov.moda.digitalwallet.data.element.LinkOpenEnum
import tw.gov.moda.digitalwallet.data.element.OperationEnum
import tw.gov.moda.digitalwallet.data.model.dwmoda.DwModa201i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk602i.Response.StatusList
import tw.gov.moda.digitalwallet.ui.main.controller.DeeplinkController
import tw.gov.moda.diw.BuildConfig
import tw.gov.moda.diw.R

class DeeplinkManagerImpl(
    private val mWalletRepository: WalletRepository,
    private val mVerifiableManager: VerifiableManager,
    private val mPref: ModaSharedPreferences,
    private val mGson: Gson,
    private val mDatabase: DigitalWalletDB,
    private val mResourceProvider: ResourceProvider,
    private val mViewController: DeeplinkController
) : DeeplinkManager {
    override suspend fun parseDeeplink(deeplink: String) = withContext(Dispatchers.IO) {
        val uri = deeplink.toUri()
        val validKeywords = arrayOf(AppConstants.Deeplink.ApplyVC, AppConstants.Deeplink.ApplyVP, AppConstants.Deeplink.StaticQRCode)
        var keyword = ""
        // 解析URL是否為認可的 deeplink or assetlinks
        val isValidationURL = if (uri.scheme == AppConstants.Deeplink.SCHEME) {
            // deeplink
            keyword = uri.host?.lowercase() ?: ""
            validKeywords.contains(keyword)
        } else if (uri.scheme?.lowercase() == "https") {
            // assetlinks
            if (deeplink.contains(BuildConfig.DID_ISSUER_URL)) {
                keyword = uri.getQueryParameter("type")?.lowercase() ?: ""
                validKeywords.contains(keyword)
            } else {
                false
            }
        } else {
            false
        }

        // 判斷是否是認可的 deeplink or assetlinks
        if (!isValidationURL) {
            mViewController.alertParsingDeeplinkError(true)
            return@withContext
        }

        // 解析並執行對應行爲
        when (keyword) {
            AppConstants.Deeplink.ApplyVC -> applyVC(deeplink)
            AppConstants.Deeplink.ApplyVP -> applyVP(deeplink)
            AppConstants.Deeplink.StaticQRCode -> staticQRCode(deeplink)
            else -> mViewController.alertParsingDeeplinkError(true)
        }
    }

    /**
     * 加入憑證
     * @param deeplink 解析用鏈結
     */
    private suspend fun applyVC(deeplink: String) {
        mViewController.progressBar(true)

        // step1. 取得現在登入的Wallet
        val wallet = mWalletRepository.getWallet()
        if (wallet == null) {
            mViewController.alertParsingDeeplinkError(true)
            return
        }

        // step2. 加入VerifiableCredential
        val verifiableCredential = mVerifiableManager.createVerifiableCredential(wallet, deeplink)
        if (verifiableCredential == null) {
            if (mWalletRepository.getIsAddVerifiableCredentialResultFullPage()) {
                mWalletRepository.setApplyVerifiableCredential(null)
                mWalletRepository.setIsAddVerifiableCredentialResultFullPage(false)
                mViewController.addVerifiableCredentialSuccessfulFullPage("")
            } else {
                mViewController.alertParsingDeeplinkError(true)
            }
            return
        }

        // step3. 驗證VerifiableCredential取得信任標記
        val response = mVerifiableManager.verifyVerifiableCredential(wallet, verifiableCredential)
        val trustBadge = response?.data?.trust_badge ?: false
        verifiableCredential.trustBadge = trustBadge

        // step4. 寫入VerifiableCredential
        insertVerifiableCredential(verifiableCredential)
        mViewController.progressBar(false)
        if (mWalletRepository.getIsAddVerifiableCredentialResultFullPage()) {
            mWalletRepository.setIsAddVerifiableCredentialResultFullPage(false)
            mViewController.addVerifiableCredentialSuccessfulFullPage(verifiableCredential.display)
        } else {
            mViewController.addVerifiableCredentialSuccessful(verifiableCredential.display)
        }

        // step5. 更新發證單位與憑證清冊
        try {
            if (mPref.issuerList.isNullOrBlank()) {
                // 信任清單為空則重新取得
                mVerifiableManager.getIssuerList()?.also {
                    mPref.issuerList = it
                }
            }
            // 更新VerifiableCredential清冊
            mWalletRepository.getWallet()?.uid?.also { uid ->
                // 取得最後一次儲存成功的 602i.Response.data
                val keyName = AppConstants.PREF.WALLET_VC_LIST_PREFIX + uid
                val vcList = mPref.getSharedPreferences().getString(keyName, "[]") ?: "[]"

                // 先取的儲存的憑證清單
                val typeToken = object : TypeToken<List<LinkedHashMap<String, Array<StatusList>?>>>() {}.type
                val storedVCList: List<LinkedHashMap<String, Array<StatusList>?>> = mGson.fromJson(vcList, typeToken)
                mVerifiableManager.getAllVerifiableCredentialList(listOf(verifiableCredential), "[]")?.also {
                    // 將已合併憑證清單轉換為 JSON 字串並儲存
                    val newVCList: List<LinkedHashMap<String, Array<StatusList>?>> = mGson.fromJson(it, typeToken)
                    val combineVCList = storedVCList + newVCList
                    val json = mGson.toJson(combineVCList)
                    mPref.getSharedPreferences().edit().putString(keyName, json).commit()
                }
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    /**
     * 出示憑證
     * @param deeplink 解析用鏈結
     */
    private suspend fun applyVP(deeplink: String, custom: DwModa201i.VPCustom? = null) {
        mViewController.progressBar(true)
        // step1. 初始化資料
        mWalletRepository.setParseVerifiablePresentation(null)
        mWalletRepository.clearAllOfRequireVerifiableCredentials()
        mWalletRepository.setRequireVerifiableCredential(null)
        mWalletRepository.setVerifiablePresentationCustom(null)

        // step2. 解析 Deeplink 資料
        mVerifiableManager.parseVerifiablePresentation(deeplink)?.also { dwsdk401i ->
            mWalletRepository.setParseVerifiablePresentation(dwsdk401i)
        }

        // step3. 檢查有沒有custom欄位
        custom?.let {
            mWalletRepository.setVerifiablePresentationCustom(it)
        }

        // step4. 判斷是否顯示自主揭露
        if (mWalletRepository.getParseVerifiablePresentation() != null) {
            mViewController.launchVerifiablePresentationFragment(true)
        } else {
            mViewController.progressBar(false)
            mViewController.alertEmptyVerifiableCredential(true)
        }
    }

    private suspend fun staticQRCode(deeplink: String) {
        val uri = deeplink.toUri()
        val mode = uri.getQueryParameter(AppConstants.Deeplink.Mode)
        // 判斷是VC還是VP
        when (mode) {
            AppConstants.Deeplink.ModeVC -> {
                applyStaticVC(deeplink)
            }

            AppConstants.Deeplink.ModeVP -> {
                applyStaticVP(deeplink)
            }

            else -> {
                mViewController.alertParsingDeeplinkError(true)
            }
        }
    }

    /**
     * 靜態QRCode VP02
     */
    private suspend fun applyStaticVP(deeplink: String) {
        mViewController.progressBar(true)
        // step1. 解析Deeplink 201i
        val uri = deeplink.toUri()
        val mode = uri.getQueryParameter(AppConstants.Deeplink.Mode) ?: ""
        val vpUid = uri.getQueryParameter(AppConstants.Deeplink.VPUid) ?: ""
        val data = mVerifiableManager.getVerifiableServiceData(vpUid = vpUid, mode = mode)

        // step2. 取得Deeplink
        data?.verifierServiceUrl?.let { url ->
            // 呼叫驗證端業務系統
            mVerifiableManager.getVerifiableServiceDeepLink(url, vpUid)?.deepLink?.let {
                applyVP(it, data.custom)
            } ?: run {
                error(mResourceProvider.getString(R.string.unknown_error_reason))
            }
        }
    }

    /**
     * 靜態QRCode VC
     */
    private suspend fun applyStaticVC(deeplink: String) {
        mViewController.progressBar(true)
        // step1. 解析Deeplink 201i
        val uri = deeplink.toUri()
        val mode = uri.getQueryParameter(AppConstants.Deeplink.Mode) ?: ""
        val vpUid = uri.getQueryParameter(AppConstants.Deeplink.VCUid) ?: ""
        val data = mVerifiableManager.getIssuerServiceData(vcUid = vpUid, mode = mode)

        mViewController.progressBar(false)
        // step2. 取得發證端輸入資料URL
        data?.issuerServiceUrl?.let { url ->
            mPref.openURL = url
            //根據type決定外開還是內嵌
            if (data.type == LinkOpenEnum.BROWSER.type) {
                mViewController.alertOpenBrowser(data.name)
            } else if (data.type == LinkOpenEnum.WEBVIEW.type) {
                mPref.openURLTitle = String.format(mResourceProvider.getString(R.string.online_application), data.name)
                mViewController.launchWebViewFragment(true)
            }
        }
    }

    /**
     * 加入憑證到 Database
     */
    private suspend fun insertVerifiableCredential(verifiableCredential: VerifiableCredential) = withContext(Dispatchers.Default) {
        // 解析verifiableCredential資料
        mVerifiableManager.getVerifiableCredential(verifiableCredential.credential)?.also { decodeVCData ->
            // 顯示第一個欄位資料至卡面
            verifiableCredential.previewData = decodeVCData.vc?.credentialSubject?.field?.data?.toList()?.first()?.second ?: ""
            // 解析發卡單位名稱
            mVerifiableManager.parseIsserListToName()?.also { issuerNameMap ->
                verifiableCredential.issuingUnit = mVerifiableManager.getIssuerName(issuerNameMap, decodeVCData)
            }
        }
        // 寫入至Database
        val vcId = mDatabase.verifiableCredentialDao().insert(verifiableCredential)
        mWalletRepository.setApplyVerifiableCredential(verifiableCredential)
        // 操作紀錄：紀錄新增卡片
        mWalletRepository.getWallet()?.uid?.also { walletId ->
            val operationRecord = OperationRecord(
                walletId = walletId,
                vcId = vcId,
                text = mResourceProvider.getString(R.string.format_add_card).format(verifiableCredential.display),
                datetime = System.currentTimeMillis(),
                status = OperationEnum.ADD_CARD
            )
            mDatabase.operationRecordDao().insert(operationRecord)
        }
    }

}