package tw.gov.moda.digitalwallet.ui.home.barcode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.gov.moda.digitalwallet.core.db.DigitalWalletDB
import tw.gov.moda.digitalwallet.core.deeplink.DeeplinkManager
import tw.gov.moda.digitalwallet.core.identifier.IdentifierManager
import tw.gov.moda.digitalwallet.core.repository.verifiable.VerifiablePresentationRepository
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.verifiable.VerifiableManager
import tw.gov.moda.digitalwallet.data.db.FavoriteShowCredential
import tw.gov.moda.digitalwallet.data.element.VerifiablePresentationEnum
import tw.gov.moda.digitalwallet.data.model.dwmoda.DwModa401i
import tw.gov.moda.digitalwallet.data.model.dwverifier.DwVerifierMgr401i
import tw.gov.moda.digitalwallet.exception.IdentifierException
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.R
import javax.inject.Inject

/**
 * 出示憑證 viewModel
 *
 * @constructor [DigitalWalletDB],[WalletRepository],[VerifiableManager],[VerifiablePresentationRepository]
 */
@HiltViewModel
class ShowCredentialViewModel @Inject constructor(
    private val mDatabase: DigitalWalletDB,
    private val mWalletRepository: WalletRepository,
    private val mVerifiableManager: VerifiableManager,
    private val mVerifiablePresentationRepository: VerifiablePresentationRepository,
    private val mIdentifierManager: IdentifierManager,
    private val mDeeplinkManager: DeeplinkManager
) : BaseViewModel() {
    private val mIsEmptyFavoriteList = MutableLiveData<Boolean>()
    val isEmptyFavoriteList: LiveData<Boolean> get() = mIsEmptyFavoriteList
    private val mIsEmptyQuickAuthorizationList = MutableLiveData<Boolean>()
    val isEmptyQuickAuthorizationList: LiveData<Boolean> get() = mIsEmptyQuickAuthorizationList
    private val mQuickAuthorizationList = MutableLiveData<List<DwModa401i.Response.VPItem>>()
    val quickAuthorizationList: LiveData<List<DwModa401i.Response.VPItem>> get() = mQuickAuthorizationList
    private val mFilteredFavoriteList = MutableLiveData<List<DwModa401i.Response.VPItem>>()
    val filteredFavoriteList: LiveData<List<DwModa401i.Response.VPItem>> get() = mFilteredFavoriteList
    private val mCloseSwipeRefreshLayout = MutableLiveData<Boolean>()
    val closeSwipeRefreshLayout: LiveData<Boolean> get() = mCloseSwipeRefreshLayout

    private val mShowCredentialList = ArrayList<DwModa401i.Response.VPItem>()

    fun refreshShowCredentialList() {
        viewModelScope.launch(getExceptionHandler { _, throwable ->
            if (throwable is IdentifierException) {
                mAlertSDKMessage.postValue("[${throwable.code}]：" + throwable.message)
            } else {
                mException.postValue(throwable)
            }
            mProgressBarWhite.postValue(false)
        }) {
            if (mShowCredentialList.isEmpty()) {
                getShowCredentialList()
            } else {
                showList()
            }

            // 判斷是否重新產生條碼
            if (mVerifiablePresentationRepository.isRetryGenerateBarcode() && mVerifiablePresentationRepository.getSelectedShowCredential() != null) {
                mVerifiablePresentationRepository.getSelectedShowCredential()?.also { credential ->
                    mVerifiablePresentationRepository.isRetryGenerateBarcode(false)
                    mVerifiablePresentationRepository.setSelectedShowCredential(null)
                    selectShowCredential(credential)
                }
            }
        }
    }

    fun toggleFavorite(item: DwModa401i.Response.VPItem) {
        viewModelScope.launch(getExceptionHandler()) {
            val favoriteList = mFilteredFavoriteList.value ?: emptyList()
            val isFavorite = favoriteList.any { it.vpUid == item.vpUid }
            withContext(Dispatchers.IO) {
                if (isFavorite) {
                    item.vpUid?.let { mDatabase.favoriteShowCredentialDao().deleteByVpUid(it) }
                } else {
                    val favoriteShowCredential = FavoriteShowCredential(
                        walletId = mWalletRepository.getWallet()?.uid ?: -1,
                        vpUid = item.vpUid,
                        name = item.name,
                        verifierModuleUrl = item.verifierModuleUrl,
                        logoUrl = item.logoUrl
                    )
                    mDatabase.favoriteShowCredentialDao().insert(favoriteShowCredential)
                }
            }
            showList()
        }
    }

    fun selectShowCredential(credential: DwModa401i.Response.VPItem) {
        viewModelScope.launch(getExceptionHandler()) {
            mProgressBar.postValue(true)
            mVerifiablePresentationRepository.setResponseOfDwVerifierMgr401i(null)

            // step1. 取得自主揭露的deeplink與transactionid
            val request = DwVerifierMgr401i.Request(credential.vpUid ?: "")
            val type = object : TypeToken<DwVerifierMgr401i.Response>() {}
            val response = mIdentifierManager.dwsdkModa101i(credential.verifierModuleUrl + DwVerifierMgr401i.URL_PATH + credential.vpUid, DwVerifierMgr401i.METHOD, request, type)
            response?.takeIf { it.code == "0" }?.data?.also {
                // 顯示自主揭露
                mVerifiablePresentationRepository.setSelectedShowCredential(credential)
                mVerifiablePresentationRepository.setVerifiablePresentationEnum(VerifiablePresentationEnum.BARCODE)
                mVerifiablePresentationRepository.setResponseOfDwVerifierMgr401i(it)
                mDeeplinkManager.parseDeeplink(it.deepLink)
                mProgressBar.postValue(false)
            } ?: run {
                mProgressBar.postValue(false)
                mAlertMessage.postValue(R.string.unknown_error_reason)
            }
        }
    }

    fun getShowCredentialList(page: Int = 0, size: Int = 10) {
        viewModelScope.launch(getExceptionHandler { _, throwable ->
            if (throwable is IdentifierException) {
                mAlertSDKMessage.postValue("[${throwable.code}]：" + throwable.message)
            } else {
                mException.postValue(throwable)
            }
            mProgressBarWhite.postValue(false)
            mCloseSwipeRefreshLayout.postValue(true)
        }) {
            mProgressBarWhite.postValue(true)
            val response = mVerifiableManager.getShowCredentialList(page, size, "")?.data
            if (page > 0) {
                response?.vpItems?.also { list ->
                    mShowCredentialList.addAll(list)
                }
            } else {
                response?.vpItems?.also { list ->
                    mShowCredentialList.clear()
                    mShowCredentialList.addAll(list)
                }
            }
            val currentPage = response?.currentPage ?: 0
            val totalPages = response?.totalPages ?: 0
            if (currentPage + 1 < totalPages) {
                getShowCredentialList(currentPage + 1, size)
            } else {
                mVerifiablePresentationRepository.setShowCredentialList(mShowCredentialList)
                showList()
            }
        }
    }

    private suspend fun showList() {
        val favoriteList = mDatabase.favoriteShowCredentialDao().getAll(mWalletRepository.getWallet()?.uid ?: -1L)
        val favoriteShowCredentials = ArrayList<DwModa401i.Response.VPItem>()
        val normalShowCredentials = ArrayList<DwModa401i.Response.VPItem>()
        //篩選我的最愛清單
        mShowCredentialList.forEach { credential ->
            val isFavorite = favoriteList.any { it.vpUid == credential.vpUid }
            if (isFavorite) {
                // 我的最愛清單
                favoriteShowCredentials.add(credential)
            } else {
                // 我的一般清單
                normalShowCredentials.add(credential)
            }
        }
        // 刷新我的最愛清單的UI
        val sortedFavoriteShowCredentials = mVerifiableManager.sortShowCredentialList(favoriteShowCredentials)
        mFilteredFavoriteList.postValue(sortedFavoriteShowCredentials)

        //刷新快速授權清單的UI
        val sortedNormalShowCredentials = mVerifiableManager.sortShowCredentialList(normalShowCredentials)
        mQuickAuthorizationList.postValue(sortedNormalShowCredentials)

        //根據我的最愛是否空值顯示不同畫面
        mIsEmptyFavoriteList.value = favoriteShowCredentials.isEmpty()

        //根據一般清單是否空值顯示不同畫面
        mIsEmptyQuickAuthorizationList.value = normalShowCredentials.isEmpty()

        mProgressBarWhite.postValue(false)
    }
}