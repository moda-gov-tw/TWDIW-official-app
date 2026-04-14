package tw.gov.moda.digitalwallet.ui.home.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import tw.gov.moda.digitalwallet.core.network.NetworkManager
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.BuildConfig
import tw.gov.moda.diw.R
import javax.inject.Inject

/**
 * 皮夾設定頁 viewModel
 *
 * @constructor [ModaSharedPreferences]
 */
@HiltViewModel
class SettingViewModel @Inject constructor(
    private val mPref: ModaSharedPreferences,
    private val mResourceProvider: ResourceProvider,
    private val mNetworkManager: NetworkManager
) : BaseViewModel() {
    private val mLaunchWebViewFragment = MutableLiveData<Boolean>()
    val launchWebViewFragment: LiveData<Boolean> get() = mLaunchWebViewFragment
    private val mShowNetworkErrorAlert = MutableLiveData<Boolean>()
    val showNetworkErrorAlert: LiveData<Boolean> get() = mShowNetworkErrorAlert

    private val mVersionName = MutableLiveData<String>()
    val versionName: LiveData<String> get() = mVersionName

    init {
        mVersionName.postValue("版本資訊 " + (if (BuildConfig.DEBUG) BuildConfig.FLAVOR.uppercase() else "") + BuildConfig.VERSION_NAME)
    }

    fun openURL(url: String) {
        if (mNetworkManager.isNetworkAvailable().not()) {
            mShowNetworkErrorAlert.postValue(true)
            return
        }
        mPref.openURL = url
        mPref.openURLTitle = mResourceProvider.getString(R.string.digital_wallet_system)
        mLaunchWebViewFragment.postValue(true)
    }

    fun dismissNetworkErrorDialog() {
        mShowNetworkErrorAlert.postValue(false)
    }
}