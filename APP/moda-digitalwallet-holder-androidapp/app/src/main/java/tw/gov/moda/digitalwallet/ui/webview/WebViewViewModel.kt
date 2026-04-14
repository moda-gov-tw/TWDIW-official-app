package tw.gov.moda.digitalwallet.ui.webview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.core.deeplink.DeeplinkManager
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.data.model.PostMessage
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.BuildConfig
import javax.inject.Inject

/**
 * WebView 的 viewModel
 *
 * @constructor [ModaSharedPreferences]
 */
@HiltViewModel
class WebViewViewModel @Inject constructor(
    private val mPref: ModaSharedPreferences,
    private val mDeeplinkManager: DeeplinkManager,
    private val mGson: Gson,
    private val mWalletRepository: WalletRepository
) : BaseViewModel() {
    private val mWebViewURL = MutableLiveData<String>()
    val webViewURL: LiveData<String> get() = mWebViewURL
    private val mTitle = MutableLiveData<String>()
    val title: LiveData<String> get() = mTitle
    private val mShowUrl = MutableLiveData<Boolean>()
    val showUrl: LiveData<Boolean> get() = mShowUrl
    private val mSwitchLeftIcon = MutableLiveData<Boolean>()
    val switchLeftIcon: LiveData<Boolean> get() = mSwitchLeftIcon

    init {
        val isTargetUrl = (mPref.openURL == BuildConfig.OFFICIAL_IMAGE_URL || mPref.openURL == AppConstants.Net.COMMON_QUESTION)
        mShowUrl.postValue(isTargetUrl)
        mSwitchLeftIcon.postValue(isTargetUrl)
        mWebViewURL.postValue(mPref.openURL)
        mTitle.postValue(mPref.openURLTitle)
    }

    fun parseQRCode(text: String) {
        viewModelScope.launch(getExceptionHandler()) {
            val data = mGson.fromJson(text, PostMessage::class.java).data
            mWalletRepository.setIsAddVerifiableCredentialResultFullPage(data.type == "webview")
            mDeeplinkManager.parseDeeplink(data.deeplink)
        }
    }
}