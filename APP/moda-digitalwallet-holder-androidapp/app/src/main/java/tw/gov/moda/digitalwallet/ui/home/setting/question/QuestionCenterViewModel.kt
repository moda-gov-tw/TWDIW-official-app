package tw.gov.moda.digitalwallet.ui.home.setting.question

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.core.network.NetworkManager
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.R
import javax.inject.Inject

/**
 * 問題中心頁 viewModel
 *
 * @constructor [ModaSharedPreferences]
 */
@HiltViewModel
class QuestionCenterViewModel @Inject constructor(
    private val mPref: ModaSharedPreferences,
    private val mResourceProvider: ResourceProvider,
    private val mNetworkManager: NetworkManager
) : BaseViewModel() {

    private val mShowNetworkErrorAlert = MutableLiveData<Boolean>()
    val showNetworkErrorAlert: LiveData<Boolean> get() = mShowNetworkErrorAlert

    private val mLaunchCommonQuestion = MutableLiveData<Boolean>()
    val launchCommonQuestion: LiveData<Boolean> get() = mLaunchCommonQuestion

    fun commonQuestion() {
        if (mNetworkManager.isNetworkAvailable().not()) {
            mShowNetworkErrorAlert.postValue(true)
            return
        }
        mPref.openURLTitle = mResourceProvider.getString(R.string.digital_wallet_system)
        mPref.openURL = AppConstants.Net.COMMON_QUESTION
        launchCommonQuestion(true)
    }

    fun reportQuestion() {
        mPref.openURLTitle = mResourceProvider.getString(R.string.title_digital_identity_wallet_report)
        mPref.openURL = AppConstants.Net.REPORT_QUESTION
    }

    fun dismissNetworkErrorDialog() {
        mShowNetworkErrorAlert.postValue(false)
    }

    fun launchCommonQuestion(isShow: Boolean) {
        mLaunchCommonQuestion.postValue(isShow)
    }
}