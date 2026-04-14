package tw.gov.moda.digitalwallet.ui.home.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tw.gov.moda.digitalwallet.core.network.NetworkManager
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.core.verifiable.VerifiableManager
import tw.gov.moda.digitalwallet.data.element.LinkOpenEnum
import tw.gov.moda.digitalwallet.data.model.AddCredential
import tw.gov.moda.digitalwallet.exception.IdentifierException
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.R
import javax.inject.Inject

/**
 * 加入憑證 viewModel
 */
@HiltViewModel
class AddCredentialViewModel @Inject constructor(
    private val mVerifiableManager: VerifiableManager,
    private val mPref: ModaSharedPreferences,
    private val mWalletRepository: WalletRepository,
    private val mResourceProvider: ResourceProvider,
    private val mNetworkManager: NetworkManager
) : BaseViewModel() {
    private val mAddCredentialList = MutableLiveData<List<AddCredential.VCItem>>()
    val addCredentialList: LiveData<List<AddCredential.VCItem>> get() = mAddCredentialList
    private val mOpenBrowser = MutableLiveData<String>()
    val openBrowser: LiveData<String> get() = mOpenBrowser
    private val mOpenWebView = MutableLiveData<Boolean>()
    val openWebView: LiveData<Boolean> get() = mOpenWebView
    private val mShowOpenBrowserAlert = MutableLiveData<String>()
    val showOpenBrowserAlert: LiveData<String> get() = mShowOpenBrowserAlert
    private val mShowNetworkErrorAlert = MutableLiveData<Boolean>()
    val showNetworkErrorAlert: LiveData<Boolean> get() = mShowNetworkErrorAlert
    private val mCloseSwipeRefreshLayout = MutableLiveData<Boolean>()
    val closeSwipeRefreshLayout: LiveData<Boolean> get() = mCloseSwipeRefreshLayout

    private var mList = ArrayList<AddCredential.VCItem>()

    fun checkBrowserType(type: Int?, url: String?, name: String?) {
        //根據type決定外開還是內嵌
        mPref.openURL = url
        if (type == LinkOpenEnum.BROWSER.type) {
            mShowOpenBrowserAlert.postValue(name ?: "")
        } else if (type == LinkOpenEnum.WEBVIEW.type) {
            // 檢查網路是否正常
            if (mNetworkManager.isNetworkAvailable().not()) {
                mShowNetworkErrorAlert.postValue(true)
                return
            }
            mPref.openURLTitle = String.format(mResourceProvider.getString(R.string.online_application), name ?: "")
            mOpenWebView.postValue(true)
        }
    }

    fun openBrowser() {
        mOpenBrowser.postValue(mPref.openURL)
    }

    fun refreshAddCredentialList() {
        if (mList.isEmpty()) {
            getAddCredentialList()
        } else {
            mAddCredentialList.postValue(mList)
        }
    }

    fun dismissNetworkErrorDialog() {
        mShowNetworkErrorAlert.postValue(false)
    }

    fun getAddCredentialList(page: Int = 0, size: Int = 10) {
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
            val response = mVerifiableManager.getAddCredentialList(page, size, "")
            if (page > 0) {
                response.vcItems?.also { list ->
                    mList.addAll(list)
                }
            } else {
                response.vcItems?.also { list ->
                    mList.clear()
                    mList.addAll(list)
                }
            }
            val currentPage = response.currentPage ?: 0
            val totalPages = response.totalPages ?: 0
            if (currentPage + 1 < totalPages) {
                getAddCredentialList(currentPage + 1, size)
            } else {
                mAddCredentialList.postValue(mList)
                mWalletRepository.setAddCredentialList(mList)
            }
            mProgressBarWhite.postValue(false)
        }
    }
}