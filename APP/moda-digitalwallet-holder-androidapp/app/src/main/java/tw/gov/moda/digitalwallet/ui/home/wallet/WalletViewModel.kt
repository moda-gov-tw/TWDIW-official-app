package tw.gov.moda.digitalwallet.ui.home.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.core.db.DigitalWalletDB
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.core.verifiable.VerifiableManager
import tw.gov.moda.digitalwallet.data.db.OperationRecord
import tw.gov.moda.digitalwallet.data.db.VerifiableCredential
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.digitalwallet.data.element.CardStatusEnum
import tw.gov.moda.digitalwallet.data.element.OperationEnum
import tw.gov.moda.digitalwallet.data.element.OrderEnum
import tw.gov.moda.digitalwallet.data.element.RemindPeriodEnum
import tw.gov.moda.digitalwallet.data.model.RemindCredentialList
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.R
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * 憑證總覽頁 viewModel
 *
 * 管理憑證列表，包括獲取、刷新狀態、排序和處理使用者互動。
 *
 * @property mDatabase 資料庫實例，用於存取可驗證憑證等數據。
 * @property mVerifiableManager 可驗證憑證管理器，處理憑證的驗證、解析等邏輯。
 * @property mWalletRepository 皮夾數據倉庫，提供當前皮夾資訊和憑證相關操作。
 * @property mPref SharedPreferences 實例，用於儲存應用程式設定和輕量級數據。
 * @property mResourceProvider 資源提供者，用於獲取應用程式資源，如字串。
 * @constructor 透過 Hilt 注入依賴項。
 */
@HiltViewModel
class WalletViewModel @Inject constructor(
    private val mDatabase: DigitalWalletDB,
    private val mVerifiableManager: VerifiableManager,
    private val mWalletRepository: WalletRepository,
    private val mPref: ModaSharedPreferences,
    private val mResourceProvider: ResourceProvider
) : BaseViewModel() {
    // LiveData 用於顯示皮夾名稱(例如 "我的皮夾")
    private val mWalletName = MutableLiveData<String>()
    val walletName: LiveData<String> get() = mWalletName

    // LiveData 用於觀察憑證排序方式的變化
    private val mOrderType = MutableLiveData(OrderEnum.DESC)
    val orderType: LiveData<OrderEnum> get() = mOrderType

    // LiveData 用於觀察綁定的可驗證憑證列表的變化
    private val mBoundVerifiableCredentialList = MutableLiveData<List<VerifiableCredential>>()
    val boundVerifiableCredentialList: LiveData<List<VerifiableCredential>> get() = mBoundVerifiableCredentialList

    // LiveData 用於顯示正在刷新的憑證數量進度 (例如 "1/5")
    private val mRefreshingCountOfCard = MutableLiveData<String>()
    val refreshingCountOfCard: LiveData<String> get() = mRefreshingCountOfCard

    // LiveData 用於通知憑證刷新成功，並顯示成功數量 (例如 "5/5")
    private val mRefreshCardSuccessfully = MutableLiveData<String>()
    val refreshCardSuccessfully: LiveData<String> get() = mRefreshCardSuccessfully

    // LiveData 用於通知憑證刷新失敗，並顯示成功數量 (例如 "5")
    private val mRefreshCardFailure = MutableLiveData<String>()
    val refreshCardFailure: LiveData<String> get() = mRefreshCardFailure

    // LiveData 用於刷新憑證列表中特定位置的項目已變更，RecycleView 可以局部刷新
    private val mItemChangedPosition = MutableLiveData<Int>()
    val itemChangedPosition: LiveData<Int> get() = mItemChangedPosition

    // LiveData 用於觸發導航到憑證資訊頁面
    private val mLaunchCardInformationFragment = MutableLiveData<Boolean>()
    val launchCardInformationFragment: LiveData<Boolean> get() = mLaunchCardInformationFragment

    // LiveData 用於觸發提醒視窗顯示
    private val mRemindCredentialListAlerts = MutableLiveData<RemindCredentialList?>()
    val remindCredentialListAlerts: LiveData<RemindCredentialList?> get() = mRemindCredentialListAlerts

    // 發行者 ID 與名稱的映射表，用於顯示發行單位名稱
    private var mIssuerNameMap: HashMap<String, String>? = null

    // 標記是否已完成憑證狀態檢測，避免重複執行
    private var mIsCompletedDetectCard = AtomicBoolean(true)

    // 標記是否正在關閉刷新進度視圖，用於控制刷新進度的 UI 顯示
    private var mIsClosingRefreshingView = false

    private var mRemindCredentialList = RemindCredentialList()

    private var mLastRefreshCredentialsTime = 0L

    init {
        viewModelScope.launch(getExceptionHandler()) {
            // 從資料庫獲取當前皮夾的憑證列表
            val list = mDatabase.verifiableCredentialDao().getAll(mWalletRepository.getWallet()?.uid ?: 0L)
            if (list.isNotEmpty() && mPref.issuerList.isNullOrBlank()) {
                // 如果皮夾內有憑證，但是信任的發行者清單為空，
                // 則先更新發行者清單，然後刷新憑證狀態。
                mProgressBarWhite.postValue(true) // 顯示進度條
                updateIssuerList()
                mProgressBarWhite.postValue(false) // 隱藏進度條
                refreshVerifiableCredentialStatus(true) // 刷新憑證狀態並進行驗證
            } else {
                // 同時刷新憑證狀態與發行者清單
                refreshVerifiableCredentialStatus(true) // 刷新憑證狀態並進行驗證
                async { updateIssuerList() }.await() // 異步更新發行者清單並等待完成
            }
        }
        mWalletName.postValue(mWalletRepository.getWallet()?.nickname ?: "")
    }

    /**
     * 當畫面顯示時呼叫。
     *
     * @param orderType 可選的排序方式，如果提供，則更新排序。
     */
    fun onShown(orderType: OrderEnum? = null) {
        // 重置導航到憑證資訊頁面的標記
        mLaunchCardInformationFragment.postValue(false)

        viewModelScope.launch(getExceptionHandler()) {
            // 如果提供了新的排序方式，則更新Value
            orderType?.also { mOrderType.value = it }

            refreshVerifiableCredentialList()
            // 更新時間是否大於1小時
            val isOverRefreshTime = System.currentTimeMillis() - mLastRefreshCredentialsTime > 60 * 60 * 1_000L
            // 根據是否設定了自動刷新來決定是否刷新憑證
            if (isAutoRefreshCard() && isOverRefreshTime) {

                // 將憑證列表轉換為 JSON 字串並儲存
                updateVerifiableCredentialList()

                refreshVerifiableCredentialStatus(false) // 刷新憑證狀態並進行驗證
            }
        }
    }

    /**
     * 查看憑證更新失敗
     */
    fun checkFailureCard() {
        mRemindCredentialList.failure.isShow = mRemindCredentialList.failure.credentialList.isNotEmpty()
        if (mRemindCredentialList.failure.isShow) {
            val remindCredentialList = RemindCredentialList()
            remindCredentialList.failure = mRemindCredentialList.failure
            mRemindCredentialListAlerts.postValue(remindCredentialList)
        }
    }

    /**
     * 重試檢查憑證狀態。
     *
     * @param isDetectConnection 是否需要進行網路連線檢查。
     */
    fun retryDetectVC(isDetectConnection: Boolean) {
        if (isDetectConnection) {
            // 如果需要檢查網路，但目前沒有網路連線，則直接返回
            if (!mResourceProvider.isConnectedToInternet()) {
                return
            }
        }
        // 刷新憑證狀態並進行驗證
        refreshVerifiableCredentialStatus(true)
    }

    /**
     * 設定正在被使用者操作（例如點擊查看詳情）的憑證。
     *
     * @param verifiableCredential 使用者選擇的憑證。
     */
    fun setDecodingVerifiableCredential(verifiableCredential: VerifiableCredential) {
        viewModelScope.launch(getExceptionHandler()) {
            // 嘗試從資料庫獲取最新的憑證資訊，如果不存在則使用傳入的憑證
            mDatabase.verifiableCredentialDao().getItem(verifiableCredential.uid)?.also {
                mWalletRepository.setDecodeVerifiableCredential(it)
            } ?: run {
                mWalletRepository.setDecodeVerifiableCredential(verifiableCredential)
            }
            // 觸發導航到憑證資訊頁面
            mLaunchCardInformationFragment.postValue(true)
        }
    }

    /**
     * 使用者觸發關閉刷新憑證的進度頁面。
     *
     * @param isClose 是否關閉顯示。
     */
    fun closeRefreshingCardView(isClose: Boolean) {
        this.mIsClosingRefreshingView = isClose
    }

    fun resetRemindCredentialListAlerts() {
        mRemindCredentialListAlerts.value = null
    }

    /**
     * 檢查是否設定為自動刷新憑證。
     *
     * @return 是否自動刷新憑證
     */
    private fun isAutoRefreshCard(): Boolean = mWalletRepository.getWallet()?.autoRefreshCard ?: true

    /**
     * 刷新憑證狀態。
     * 此方法會遍歷所有綁定的可驗證憑證，並執行線上驗證(dwsdk-301i)，失敗則執行離線驗證(dwsdk-302i)。
     *
     * @param isOnline 是否執行線上憑證驗證。true 表示執行驗證，false 表示僅刷新列表。
     */
    private fun refreshVerifiableCredentialStatus(isOnline: Boolean) {
        // 如果目前正在進行檢測，則先刷新一次列表後返回，避免重複執行驗證。
        if (!mIsCompletedDetectCard.compareAndSet(true, false)) {
            viewModelScope.launch(getExceptionHandler()) {
                refreshVerifiableCredentialList()
            }
            return
        }
        mRemindCredentialList = RemindCredentialList()
        viewModelScope.launch(getExceptionHandler { _, _ ->
            // 發生錯誤時，將檢測狀態設回已完成，以便下次可以重新觸發
            mIsCompletedDetectCard.set(true)
        }) {

            // 先刷新一次本地列表，確保使用的是最新的數據
            val boundVerifiableCredentialList = refreshVerifiableCredentialList()

            // 重置關閉刷新視圖的標記，並標記開始檢測
            mIsClosingRefreshingView = false

            // 獲取當前綁定憑證的總數
            val verifiableCredentials = boundVerifiableCredentialList.filter { it.status != CardStatusEnum.Expired && it.status != CardStatusEnum.Invalid }
            val sumCards = verifiableCredentials.size // 已完成處理的憑證數量總數
            var countCompletedCards = 0 // 目前已完成處理的憑證數量
            var countFailureCards = 0 // 處理失敗的憑證數量
            var countRefreshingCards = 0 // 目前更新中的憑證數量
            // 解析發行者列表，獲取 ID 與名稱的映射
            mIssuerNameMap = mVerifiableManager.parseIsserListToName()

            // 標記是否有憑證狀態發生變化
            var isVerifiableCredentialStatusChanged = false

            // 如果沒有任何綁定的憑證，則直接返回
            if (sumCards == 0) {
                mIsCompletedDetectCard.set(true)
                return@launch
            }

            // 判斷是否為網路問題，是則不進行驗證
            if (isOnline && !mResourceProvider.isConnectedToInternet()) {
                mIsCompletedDetectCard.set(true)
                mRefreshCardFailure.value = ""
                mRemindCredentialList.failure.isShow = false
                mRemindCredentialList.disconnect.isShow = true
                mRemindCredentialListAlerts.postValue(mRemindCredentialList)
                return@launch
            }



            verifiableCredentials.forEach { verifiableCredential ->
                try {
                    // 更新處理中的憑證數量
                    countRefreshingCards++
                    // 如果需要驗證且使用者未關閉刷新視圖，則更新刷新進度
                    if (!mIsClosingRefreshingView) {
                        mRefreshingCountOfCard.postValue("$countRefreshingCards/$sumCards")
                    }

                    var expireTimestamp: Long? = null


                    // 解析憑證內容，獲取發行單位名稱並設定到憑證物件中
                    mVerifiableManager.getVerifiableCredential(verifiableCredential.credential)?.also { decodeVCData ->
                        verifiableCredential.issuingUnit = mVerifiableManager.getIssuerName(mIssuerNameMap, decodeVCData)
                        expireTimestamp = decodeVCData.exp
                    }

                    // 如果需要驗證憑證狀態
                    // 執行憑證驗證，如果狀態有變更，則標記並通知 UI 更新特定項目
                    if (verifyVerifiableCredential(verifiableCredential, isOnline)) {
                        isVerifiableCredentialStatusChanged = true
                        // 獲取該憑證於列表的位置，並通知 UI 更新項目
                        verifiableCredentials.indexOf(verifiableCredential)?.also { position ->
                            mItemChangedPosition.postValue(position)
                        }
                    }

                    detectRemindPeriod(verifiableCredential, expireTimestamp)
                    countCompletedCards += 1
                } catch (e: Exception) {
                    // 捕獲遍歷和處理過程中的異常
                    countFailureCards += 1 // 增加失敗計數
                    mRemindCredentialList.failure.credentialList.add(verifiableCredential)
                }
            }

            // 如果有憑證狀態發生異動，則更新儲存在 SharedPreferences 中的憑證列表 (dwsdk-602i)
            if (isVerifiableCredentialStatusChanged) {
                updateVerifiableCredentialList()
            }

            // 檢查是否有未顯示過的失效卡
            boundVerifiableCredentialList
                .filter { it.status == CardStatusEnum.Expired || it.status == CardStatusEnum.Invalid }
                .filter { it.remind != RemindPeriodEnum.EXPIRED }
                .also { list ->
                    list.forEach { verifiableCredential ->
                        verifiableCredential.remind = RemindPeriodEnum.EXPIRED
                        mRemindCredentialList.expired.credentialList.add(verifiableCredential)
                    }
                }


            // 是否需要顯示提醒視窗
            mRemindCredentialList.expired.isShow = mRemindCredentialList.expired.credentialList.isNotEmpty()
            mRemindCredentialList.willExpire.isShow = mRemindCredentialList.willExpire.credentialList.isNotEmpty()


            // 判斷是否為網路問題
            if (!mResourceProvider.isConnectedToInternet() && countFailureCards > 0) {
                mRefreshCardFailure.value = ""
                mRemindCredentialList.failure.isShow = false
                mRemindCredentialList.disconnect.isShow = true
            }


            // 如果執行了驗證，則顯示更新成功的狀態
            if (countFailureCards > 0) {
                if (mResourceProvider.isConnectedToInternet()) {
                    mRefreshCardFailure.postValue("$countFailureCards")
                } else {
                    mRefreshCardFailure.value = ""
                }
            } else {
                mRefreshCardSuccessfully.postValue("$countCompletedCards/$sumCards")
            }

            // 最後再次刷新憑證列表，以確保 UI 顯示的是最新的數據和排序
            refreshVerifiableCredentialList()
            // 標記檢測已完成
            mIsCompletedDetectCard.set(true)
            mLastRefreshCredentialsTime = System.currentTimeMillis()
            // 顯示 Popup 通知
            val isAlerts = mRemindCredentialList.disconnect.isShow || mRemindCredentialList.expired.isShow || mRemindCredentialList.willExpire.isShow
            mRemindCredentialListAlerts.postValue(if (isAlerts) mRemindCredentialList else null)
        }
    }

    /**
     * 驗證單個可驗證憑證的狀態。
     * 會嘗試線上驗證，如果失敗或特定情況下會嘗試離線驗證。
     *
     * @param verifiableCredential 要驗證的可驗證憑證。
     * @return 如果憑證的狀態或信任徽章(藍勾勾)發生了變化，則返回 true，否則返回 false。
     */
    private suspend fun verifyVerifiableCredential(verifiableCredential: VerifiableCredential, isOnline: Boolean): Boolean = withContext(Dispatchers.Default) {
        // 記錄原始的狀態和信任徽章，用於比較是否有變化
        val originalStatus = verifiableCredential.status
        val originalTrustBadge = verifiableCredential.trustBadge
        // 標記狀態是否變化
        var isVerifiableCredentialStatusChanged = false

        mWalletRepository.getWallet()?.also { wallet: Wallet ->
            // 嘗試線上驗證憑證，只處理成功(code "0")或特定情況(code "3")的結果，否則執行離線驗證
            var verifyVC = if (isOnline) {
                mVerifiableManager.verifyVerifiableCredential(wallet, verifiableCredential)?.takeIf { it.code == "0" || it.code == "3" }
            } else {
                // 無需線上驗證則改為離線驗證
                mVerifiableManager.verifyVerifiableCredentialOffline(wallet, verifiableCredential)
            }
            // 如果線上驗證有結果，則根據結果檢測並更新憑證狀態
            if (verifyVC != null) {
                mVerifiableManager.detectVerifiableCredentialStatus(wallet, verifiableCredential, verifyVC).also { verifiableCredential ->
                    // 如果憑證狀態變為失效或過期，進行憑證紀錄
                    if (verifiableCredential.status == CardStatusEnum.Invalid || verifiableCredential.status == CardStatusEnum.Expired) {
                        val sqlItem = mDatabase.operationRecordDao().getItem(verifiableCredential.uid, OperationEnum.INVAILD_CARD)
                        // 檢查資料庫中是否已存在此憑證的失效記錄，不存在則插入新的失效記錄
                        if (sqlItem == null) {
                            val operationRecord = OperationRecord(
                                walletId = wallet.uid,
                                vcId = verifiableCredential.uid,
                                text = mResourceProvider.getString(R.string.format_invalid_card).format(verifiableCredential.display),
                                status = OperationEnum.INVAILD_CARD,
                                datetime = System.currentTimeMillis()
                            )
                            mDatabase.operationRecordDao().insert(operationRecord)
                        }
                    }

                    val afterStatus = verifiableCredential.status
                    // 判斷憑證狀態是否已變更
                    if (originalStatus != afterStatus) {
                        isVerifiableCredentialStatusChanged = true
                    }
                    // 判斷信任徽章是否已變更
                    if (originalTrustBadge != verifiableCredential.trustBadge) {
                        isVerifiableCredentialStatusChanged = true
                    }

                    // 更新憑證的最後更新時間
                    verifiableCredential.updateDatetime = System.currentTimeMillis()
                    // 將更新後的憑證資訊存入資料庫
                    mDatabase.verifiableCredentialDao().update(verifiableCredential)
                }
            } else {
                error(mResourceProvider.getString(R.string.unknown_error_reason))
            }
        }
        return@withContext isVerifiableCredentialStatusChanged
    }

    /**
     * 更新發行者列表和相關的憑證清冊。
     */
    private suspend fun updateIssuerList() = withContext(Dispatchers.Default) {
        // 首先嘗試解析本地已有的發行者列表 (如果存在)
        mIssuerNameMap = mVerifiableManager.parseIsserListToName()
        // 用於記錄過程中發生的異常
        var exception: Exception? = null
        // 嘗試從遠端獲取並儲存最新的發行者列表
        try {
            mVerifiableManager.getIssuerList()?.also {
                mPref.issuerList = it
            }
        } catch (e: Exception) {
            exception = e
        }

        // 嘗試更新 SharedPreferences 中儲存的當前皮夾的憑證列表快照
        try {
            mWalletRepository.getWallet()?.uid?.also { uid ->
                // 取得最後一次儲存成功的 602i.Response.data
                val keyName = AppConstants.PREF.WALLET_VC_LIST_PREFIX + uid
                val vcList = mPref.getSharedPreferences().getString(keyName, "[]") ?: "[]"
                // 從資料庫獲取當前錢包的所有憑證
                val currentVCsInDb = mDatabase.verifiableCredentialDao().getAll(uid)
                // 將憑證列表轉換為 JSON 字串
                mVerifiableManager.getAllVerifiableCredentialList(currentVCsInDb, vcList)?.also {
                    // 儲存到 SharedPreferences，使用 commit() 以確保同步寫入
                    mPref.getSharedPreferences().edit().putString(keyName, it).commit()
                }
            }
        } catch (e: Exception) {
            exception = e
        }

        // 如果在上述任一步驟中發生了異常，則向上拋出
        if (exception != null) {
            throw exception
        }

        // 在所有操作成功後，再次解析最新的發行者列表
        mIssuerNameMap = mVerifiableManager.parseIsserListToName()
    }

    /**
     * 從資料庫重新整理可驗證憑證列表，並更新 LiveData。
     * 此方法會根據當前的排序方式 (OrderType) 從資料庫獲取憑證列表，
     */
    private suspend fun refreshVerifiableCredentialList(): List<VerifiableCredential> = withContext(Dispatchers.Default) {
        // 獲取當前錢包的 UID，如果錢包不存在則使用 0L (應注意此情況的處理)
        val walletUid = mWalletRepository.getWallet()?.uid ?: 0L

        // 根據排序方式從資料庫查詢憑證列表
        val finalVerifiableCredentialList = if (mOrderType.value == OrderEnum.DESC) {
            mDatabase.verifiableCredentialDao().getAll(walletUid)
        } else {
            mDatabase.verifiableCredentialDao().getAllByAscending(walletUid)
        }

        // 將查詢結果發佈到 LiveData 更新
        mBoundVerifiableCredentialList.postValue(finalVerifiableCredentialList)

        return@withContext finalVerifiableCredentialList
    }

    private suspend fun detectRemindPeriod(verifiableCredential: VerifiableCredential, exp: Long?) = withContext(Dispatchers.Default) {
        val now = System.currentTimeMillis()
        if (verifiableCredential.status == CardStatusEnum.Invalid) {
            // 已過期
            if (verifiableCredential.remind != RemindPeriodEnum.EXPIRED) {
                mRemindCredentialList.expired.credentialList.add(verifiableCredential)
            }
            verifiableCredential.remind = RemindPeriodEnum.EXPIRED
        } else {
            exp?.also { expiredTime ->
                val millisecondsOffset = (expiredTime * 1000L) - now
                val millisecondsDay = 60 * 60 * 24 * 1000L
                if (millisecondsOffset < 0) {
                    // 已過期
                    if (verifiableCredential.remind != RemindPeriodEnum.EXPIRED) {
                        mRemindCredentialList.expired.credentialList.add(verifiableCredential)
                    }
                    verifiableCredential.remind = RemindPeriodEnum.EXPIRED
                } else if (millisecondsOffset < 1 * millisecondsDay) {
                    // 今天即將到期
                    if (verifiableCredential.remind != RemindPeriodEnum.EXPIRED &&
                        verifiableCredential.remind != RemindPeriodEnum.ONE_DAY
                    ) {
                        mRemindCredentialList.willExpire.credentialList.add(verifiableCredential)
                    }
                    verifiableCredential.remind = RemindPeriodEnum.ONE_DAY
                } else if (millisecondsOffset < 7 * millisecondsDay) {
                    // 七天將到期
                    if (verifiableCredential.remind != RemindPeriodEnum.EXPIRED &&
                        verifiableCredential.remind != RemindPeriodEnum.ONE_DAY &&
                        verifiableCredential.remind != RemindPeriodEnum.SEVEN_DAY
                    ) {
                        mRemindCredentialList.willExpire.credentialList.add(verifiableCredential)
                    }
                    verifiableCredential.remind = RemindPeriodEnum.SEVEN_DAY
                } else {
                    // 正常
                    verifiableCredential.remind = RemindPeriodEnum.NORMAL
                }
            }
        }
    }

    private suspend fun updateVerifiableCredentialList() = withContext(Dispatchers.Default) {
        // 先暫存原本的資料內容，失敗時還原資料。
        val oldJson = mWalletRepository.getWallet()?.uid?.let { uid ->
            val keyName = AppConstants.PREF.WALLET_VC_LIST_PREFIX + uid
            mPref.getSharedPreferences().getString(keyName, "[]")
        }

        try {
            // 更新憑證列表
            mWalletRepository.getWallet()?.uid?.also { uid ->
                // 取得最後一次儲存成功的 602i.Response.data
                val keyName = AppConstants.PREF.WALLET_VC_LIST_PREFIX + uid
                val vcList = mPref.getSharedPreferences().getString(keyName, "[]") ?: "[]"
                // 從資料庫獲取最新的憑證列表
                val currentVCsInDb = mDatabase.verifiableCredentialDao().getAll(uid)
                // 將憑證列表轉換為 JSON 字串並儲存
                mVerifiableManager.getAllVerifiableCredentialList(currentVCsInDb, vcList)?.also { vcListJson ->
                    mPref.getSharedPreferences().edit().putString(keyName, vcListJson).commit()
                }
            }
        } catch (e: Exception) {
            // 更新失敗則不覆寫資料。
            oldJson?.also { json ->
                mWalletRepository.getWallet()?.uid?.also { uid ->
                    val keyName = AppConstants.PREF.WALLET_VC_LIST_PREFIX + uid
                    mPref.getSharedPreferences().edit().putString(keyName, json).commit()
                }
            }
        }
    }
}