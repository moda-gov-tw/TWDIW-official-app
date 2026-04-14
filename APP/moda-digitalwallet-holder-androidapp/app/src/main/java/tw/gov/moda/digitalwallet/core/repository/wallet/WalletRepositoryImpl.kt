package tw.gov.moda.digitalwallet.core.repository.wallet

import android.net.Uri
import android.util.LruCache
import androidx.lifecycle.MutableLiveData
import tw.gov.moda.digitalwallet.data.db.VerifiableCredential
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.digitalwallet.data.element.PageEnum
import tw.gov.moda.digitalwallet.data.element.VerificationSourceEnum
import tw.gov.moda.digitalwallet.data.model.AddCredential
import tw.gov.moda.digitalwallet.data.model.BaseModel
import tw.gov.moda.digitalwallet.data.model.RequireVerifiableCredential
import tw.gov.moda.digitalwallet.data.model.VerifiablePresentationResultData
import tw.gov.moda.digitalwallet.data.model.dwmoda.DwModa201i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk401i

/**
 * 皮夾的儲存庫
 *
 * 控管各個頁面之間資料的傳遞
 */
class WalletRepositoryImpl : WalletRepository {
    private var mArgumentsOfVerifiablePresentation = HashMap<Long, Array<String>>()
    private var mApplyVerifiableCredential: VerifiableCredential? = null
    private var mAppLinkUri: Uri? = null
    private var mIsLogin = false
    private var mDecodeVerifiableCredential: VerifiableCredential? = null
    private var mParseVPData: BaseModel<Dwsdk401i.Response>? = null
    private var mWallet: Wallet? = null
    private var mCreatingWallet: Wallet? = null
    private var mRequireVerifiableCredential: RequireVerifiableCredential? = null
    private var mVerifiablePresentationResult = false
    private var mRequireVerifiableCredentials = ArrayList<RequireVerifiableCredential>()
    private val mContractAgreeLiveData = MutableLiveData<Boolean>()
    private var mIsNewWallet = false
    private var mVerificationCode = ""
    private var mIsContinueUsing = false
    private var mVerificationSourceEnum = VerificationSourceEnum.ChangePinCode
    private var mContractTitle = ""
    private var mContractContent = ""
    private var mVerifiablePresentationResultData: VerifiablePresentationResultData? = null
    private var mPageEnum = PageEnum.Wallet
    private var mAddCredentialList: List<AddCredential.VCItem>? = null
    private var mAddVerifiableCredentialSuccessType: Boolean = false
    private var mVerifiablePresentationCustom: DwModa201i.VPCustom? = null

    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 8
    private val mImageCache: LruCache<String, String> = object : LruCache<String, String>(cacheSize) {
        override fun sizeOf(key: String, value: String): Int {
            return value.length / 1024
        }
    }

    /**
     * 設定已登入皮夾
     *
     * @param wallet 皮夾
     */
    override fun setWallet(wallet: Wallet?) {
        this.mWallet = wallet
    }

    /**
     * 取得已登入皮夾
     *
     * @return 皮夾
     */
    override fun getWallet(): Wallet? {
        return this.mWallet
    }

    /**
     * 設定建立中皮夾
     *
     * @param wallet 皮夾
     */
    override fun setCreatingWallet(wallet: Wallet?) {
        this.mCreatingWallet = wallet
    }

    /**
     * 取得建立中皮夾
     *
     * @return 皮夾
     */
    override fun getCreatingWallet(): Wallet? {
        return this.mCreatingWallet
    }

    /**
     * 紀錄是否需要直接開啟新增及皮
     *
     * @param isNew 是否是新增皮夾
     */
    override fun isNewWallet(isNew: Boolean) {
        this.mIsNewWallet = isNew
    }

    /**
     * 取得是否需要直接開啟新增及皮
     */
    override fun isNewWallet(): Boolean = this.mIsNewWallet

    override fun setContractTitle(title: String) {
        this.mContractTitle = title
    }

    override fun getContractTitle(): String {
        return this.mContractTitle
    }

    override fun setContractContent(content: String) {
        this.mContractContent = content
    }

    override fun getContractContent(): String {
        return this.mContractContent
    }

    override fun getContractAgreeLiveData(): MutableLiveData<Boolean> {
        return this.mContractAgreeLiveData
    }

    /**
     * 設定 dwsdk-201i 的結果
     *
     * @param vc [VerifiableCredential]
     */
    override fun setApplyVerifiableCredential(vc: VerifiableCredential?) {
        this.mApplyVerifiableCredential = vc
    }

    /**
     * 取得 dwsdk-201i 的結果
     */
    override fun getApplyVerifiableCredential(): VerifiableCredential? = mApplyVerifiableCredential

    /**
     * 設定 AppLink 的 URL
     *
     * @param uri AppLink的URI
     */
    override fun setAppLink(uri: Uri?) {
        this.mAppLinkUri = uri
    }

    /**
     * 取得 AppLink 的 URL
     */
    override fun getAppLink(): Uri? {
        return this.mAppLinkUri
    }


    /**
     * 設定登入狀態
     */
    override fun setLoginStatus(isLogin: Boolean) {
        mIsLogin = isLogin
    }

    /**
     * 取得登入狀態
     */
    override fun isLogin(): Boolean = mIsLogin

    /**
     * 設定 VerifiableCredential 的 解析資料 (dwsdk-501i)
     */
    override fun setDecodeVerifiableCredential(verifiableCredential: VerifiableCredential?) {
        this.mDecodeVerifiableCredential = verifiableCredential
    }

    /**
     * 取得 VerifiableCredential 的 解析資料 (dwsdk-501i)
     */
    override fun getDecodeVerifiableCredential(): VerifiableCredential? {
        return this.mDecodeVerifiableCredential
    }

    /**
     * 設定 VerifiablePresentation 的 解析資料 (dwsdk-301i)
     */
    override fun setParseVerifiablePresentation(data: BaseModel<Dwsdk401i.Response>?) {
        this.mParseVPData = data
    }

    /**
     * 取得 VerifiablePresentation 的 解析資料 (dwsdk-301i)
     */
    override fun getParseVerifiablePresentation(): BaseModel<Dwsdk401i.Response>? {
        return this.mParseVPData
    }

    /**
     * 設定 VerifiablePresentation 的 Custom 欄位
     */
    override fun setVerifiablePresentationCustom(custom: DwModa201i.VPCustom?) {
        mVerifiablePresentationCustom = custom
    }

    /**
     * 取得 VerifiablePresentation 的 Custom 欄位
     */
    override fun getVerifiablePresentationCustom(): DwModa201i.VPCustom? {
        return mVerifiablePresentationCustom
    }

    /**
     * 設定 已選擇的 VerifiableCredential
     *
     * @param requireVerifiableCredential [RequireVerifiableCredential]
     */
    override fun setRequireVerifiableCredential(requireVerifiableCredential: RequireVerifiableCredential?) {
        this.mRequireVerifiableCredential = requireVerifiableCredential
    }

    /**
     * 取得 已選擇的 VerifiableCredential
     *
     * @return [RequireVerifiableCredential]
     */
    override fun getRequireVerifiableCredential(): RequireVerifiableCredential? {
        return this.mRequireVerifiableCredential
    }

    /**
     * 加入 已選擇的 VerifiableCredential 到 已選擇清單中
     *
     * @param requireVerifiableCredential [RequireVerifiableCredential]
     */
    override fun addRequireVerifiableCredential(requireVerifiableCredential: RequireVerifiableCredential) {
        this.mRequireVerifiableCredentials.add(requireVerifiableCredential)
    }

    /**
     * 移除已選擇VerifiableCredential清單的 [RequireVerifiableCredential]
     *
     * @param requireVerifiableCredential [RequireVerifiableCredential]
     */
    override fun clearRequireVerifiableCredential(requireVerifiableCredential: RequireVerifiableCredential) {
        this.mRequireVerifiableCredentials.iterator().also { iterator ->
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (item.requireData.card == requireVerifiableCredential.requireData.card) {
                    iterator.remove()
                }
            }
        }
    }

    /**
     * 移除全部已選擇VerifiableCredential清單
     */
    override fun clearAllOfRequireVerifiableCredentials() {
        this.mRequireVerifiableCredentials.clear()
    }

    /**
     * 取得已選擇VerifiableCredential清單
     */
    override fun getRequireVerifiableCredentials(): List<RequireVerifiableCredential> {
        return this.mRequireVerifiableCredentials
    }

    /**
     * 取得已選擇參數的容器
     * @return 已選擇授權參數的容器
     */
    override fun getArgumentsOfVerifiablePresentation(): HashMap<Long, Array<String>> {
        return this.mArgumentsOfVerifiablePresentation
    }

    /**
     * 設定 VerifiablePresentation授權結果
     */
    override fun setVerifiablePresentationResult(result: Boolean) {
        this.mVerifiablePresentationResult = result
    }

    /**
     * 取得 VerifiablePresentation授權結果
     */
    override fun getVerifiablePresentationResult(): Boolean {
        return this.mVerifiablePresentationResult
    }

    override fun setVerifiablePresentationResultData(resultData: VerifiablePresentationResultData) {
        mVerifiablePresentationResultData = resultData
    }

    override fun getVerifiablePresentationResultData(): VerifiablePresentationResultData? {
        return mVerifiablePresentationResultData
    }

    /**
     * 設定掃描用OTP驗證碼
     */
    override fun setVerificationCode(otp: String) {
        this.mVerificationCode = otp
    }

    /**
     * 取得掃描用OTP驗證碼
     */
    override fun getVerificationCode(): String {
        return this.mVerificationCode
    }

    /**
     * 是否已點擊自動登出的繼續使用
     * @param isContinue 繼續使用
     */
    override fun isContinueUsing(isContinue: Boolean) {
        this.mIsContinueUsing = isContinue
    }

    /**
     * 是否已點擊自動登出的繼續使用
     * @return 是否繼續使用
     */
    override fun isContinueUsing(): Boolean {
        return this.mIsContinueUsing
    }

    /**
     * 定義驗證來源途徑
     * @param sourceEnum [VerificationSourceEnum]
     */
    override fun setVerificationSource(sourceEnum: VerificationSourceEnum) {
        this.mVerificationSourceEnum = sourceEnum
    }

    /**
     * 取得驗證來源途徑
     */
    override fun getVerificationSource(): VerificationSourceEnum {
        return this.mVerificationSourceEnum
    }

    override fun setPageEnum(pageEnum: PageEnum) {
        this.mPageEnum = pageEnum
    }

    override fun getPageEnum(): PageEnum {
        return this.mPageEnum
    }

    override fun setAddCredentialList(list: List<AddCredential.VCItem>) {
        this.mAddCredentialList = list
    }

    override fun getAddCredentialList(): List<AddCredential.VCItem>? {
        return mAddCredentialList
    }

    override fun setIsAddVerifiableCredentialResultFullPage(type: Boolean) {
        this.mAddVerifiableCredentialSuccessType = type
    }

    override fun getIsAddVerifiableCredentialResultFullPage(): Boolean {
        return mAddVerifiableCredentialSuccessType
    }

    override fun getImageCache(key: String): String? = mImageCache.get(key)

    override fun putImageCache(key: String, base64: String) {
        mImageCache.put(key, base64)
    }
}