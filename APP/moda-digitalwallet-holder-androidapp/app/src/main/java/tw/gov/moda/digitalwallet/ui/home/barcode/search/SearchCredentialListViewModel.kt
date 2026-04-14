package tw.gov.moda.digitalwallet.ui.home.barcode.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tw.gov.moda.digitalwallet.core.db.DigitalWalletDB
import tw.gov.moda.digitalwallet.core.deeplink.DeeplinkManager
import tw.gov.moda.digitalwallet.core.identifier.IdentifierManager
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.repository.verifiable.VerifiablePresentationRepository
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.core.verifiable.VerifiableManager
import tw.gov.moda.digitalwallet.data.db.FavoriteShowCredential
import tw.gov.moda.digitalwallet.data.db.SearchRecord
import tw.gov.moda.digitalwallet.data.element.LinkOpenEnum
import tw.gov.moda.digitalwallet.data.element.PageEnum
import tw.gov.moda.digitalwallet.data.element.VerifiablePresentationEnum
import tw.gov.moda.digitalwallet.data.model.AddCredential
import tw.gov.moda.digitalwallet.data.model.dwmoda.DwModa401i
import tw.gov.moda.digitalwallet.data.model.dwverifier.DwVerifierMgr401i
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.R
import javax.inject.Inject

@HiltViewModel
class SearchCredentialListViewModel @Inject constructor(
    private val mWalletRepository: WalletRepository,
    private val mDatabase: DigitalWalletDB,
    private val mVerifiablePresentationRepository: VerifiablePresentationRepository,
    private val mVerifiableManager: VerifiableManager,
    private val mIdentifierManager: IdentifierManager,
    private val mDeeplinkManager: DeeplinkManager,
    private val mPref: ModaSharedPreferences,
    private val mResourceProvider: ResourceProvider
) : BaseViewModel() {
    private val mSearchRecordList = MutableLiveData<List<SearchRecord>>()
    val searchRecordList: LiveData<List<SearchRecord>> get() = mSearchRecordList
    private val mSearchResultFromShowList = MutableLiveData<List<DwModa401i.Response.VPItem>>()
    val searchResultFromShowList: LiveData<List<DwModa401i.Response.VPItem>> get() = mSearchResultFromShowList
    private val mSearchResultFromAddList = MutableLiveData<List<AddCredential.VCItem>>()
    val searchResultFromAddList: LiveData<List<AddCredential.VCItem>> get() = mSearchResultFromAddList
    private val mFavoriteList = MutableLiveData<List<DwModa401i.Response.VPItem>>()
    val favoriteList: LiveData<List<DwModa401i.Response.VPItem>> get() = mFavoriteList
    private val mShowNoResultText = MutableLiveData<Boolean>()
    val showNoResultText: LiveData<Boolean> get() = mShowNoResultText
    private val mBackToFragmentByPag = MutableLiveData<PageEnum>()
    val backToFragmentByPag: LiveData<PageEnum> get() = mBackToFragmentByPag
    private val mSwitchAdapterByPage = MutableLiveData<PageEnum>()
    val switchAdapterByPage: LiveData<PageEnum> get() = mSwitchAdapterByPage
    private val mOpenBrowser = MutableLiveData<String>()
    val openBrowser: LiveData<String> get() = mOpenBrowser
    private val mOpenWebView = MutableLiveData<Boolean>()
    val openWebView: LiveData<Boolean> get() = mOpenWebView
    private val mShowOpenBrowserAlert = MutableLiveData<String?>()
    val showOpenBrowserAlert: LiveData<String?> get() = mShowOpenBrowserAlert

    private var mSearchKeyword = ""

    fun searchResult(keyword: String) {
        mSearchKeyword = keyword.trim()
        viewModelScope.launch(getExceptionHandler()) {
            if (getPageEnum() == PageEnum.AddCredential) {
                val credentials = mutableListOf<AddCredential.VCItem>()
                mWalletRepository.getAddCredentialList()?.forEach { item ->
                    // 判斷是否有相符的搜尋字
                    if (mSearchKeyword.isNotBlank() && item.name?.contains(mSearchKeyword) == true) {
                        credentials.add(item)
                    }
                }

                // 是否搜尋不到任何結果
                mShowNoResultText.value = credentials.isEmpty()

                // 顯示搜尋結果
                mSearchResultFromAddList.value = credentials
            } else {
                val storedFavoriteShowCredentials = mDatabase.favoriteShowCredentialDao().getAll(mWalletRepository.getWallet()?.uid ?: 0L)
                val favoriteShowCredentials = mutableListOf<DwModa401i.Response.VPItem>()
                val normalCredentials = mutableListOf<DwModa401i.Response.VPItem>()
                mFavoriteList.postValue(storedFavoriteShowCredentials.map {
                    DwModa401i.Response.VPItem(
                        it.vpUid,
                        it.name,
                        it.verifierModuleUrl,
                        it.logoUrl
                    )
                })
                mVerifiablePresentationRepository.getShowCredentialList()?.forEach { item ->
                    // 判斷是否有相符的搜尋字
                    if (mSearchKeyword.isNotBlank() && item.name?.contains(mSearchKeyword) == true) {
                        // 判斷是否為我的最愛清單
                        if (storedFavoriteShowCredentials.any { it.vpUid == item.vpUid }) {
                            favoriteShowCredentials.add(item)
                        } else {
                            normalCredentials.add(item)
                        }
                    }
                }

                // 是否搜尋不到任何結果
                mShowNoResultText.value = favoriteShowCredentials.isEmpty() && normalCredentials.isEmpty()

                // 顯示搜尋結果
                val sortedList = mVerifiableManager.sortShowCredentialList(favoriteShowCredentials) + mVerifiableManager.sortShowCredentialList(normalCredentials)
                mSearchResultFromShowList.value = sortedList
            }
        }
    }

    fun saveRecord(keyword: String) {
        viewModelScope.launch(getExceptionHandler()) {
            mDatabase.searchRecordDao().apply {
                insert(
                    SearchRecord(
                        walletId = mWalletRepository.getWallet()?.uid ?: -1L,
                        keyword = keyword,
                        sourceType = getPageEnum()
                    )
                )
                if (countAllByType(getPageEnum()) > 5) {
                    deleteOlderByType(getPageEnum())
                }
            }
            refreshSearchRecord()
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

    fun refreshSearchRecord() {
        viewModelScope.launch(getExceptionHandler()) {
            mSearchRecordList.postValue(mDatabase.searchRecordDao().getAllByType(mWalletRepository.getWallet()?.uid ?: 0L, getPageEnum()))
        }
    }

    fun deleteSearchRecord(item: SearchRecord) {
        viewModelScope.launch(getExceptionHandler()) {
            mDatabase.searchRecordDao().delete(item)
            refreshSearchRecord()
        }
    }

    fun toggleFavorite(item: DwModa401i.Response.VPItem) {
        viewModelScope.launch(getExceptionHandler()) {
            val favoriteList = mFavoriteList.value ?: emptyList()
            val isFavorite = favoriteList.any { it.vpUid == item.vpUid }
            if (isFavorite) {
                mDatabase.favoriteShowCredentialDao().deleteByVpUid(item.vpUid ?: "")
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
            searchResult(mSearchKeyword)
        }
    }

    fun goBack() {
        mBackToFragmentByPag.value = getPageEnum()
    }

    fun checkBrowserType(type: Int?, url: String?, name: String?) {
        //根據type決定外開還是內嵌
        mPref.openURL = url
        if (type == LinkOpenEnum.BROWSER.type) {
            mShowOpenBrowserAlert.postValue(name)
        } else if (type == LinkOpenEnum.WEBVIEW.type) {
            mPref.openURLTitle = String.format(mResourceProvider.getString(R.string.online_application), name)
            mOpenWebView.postValue(true)
        }
    }

    fun openBrowser() {
        mOpenBrowser.postValue(mPref.openURL)
    }

    fun switchResultAdapter() {
        mSwitchAdapterByPage.value = getPageEnum()
    }

    private fun getPageEnum(): PageEnum {
        return mWalletRepository.getPageEnum()
    }
}