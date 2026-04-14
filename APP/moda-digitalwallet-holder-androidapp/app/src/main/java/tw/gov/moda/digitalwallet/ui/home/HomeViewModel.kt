package tw.gov.moda.digitalwallet.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tw.gov.moda.digitalwallet.core.db.DigitalWalletDB
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.data.element.PageEnum
import tw.gov.moda.digitalwallet.data.model.RemindAlert
import tw.gov.moda.digitalwallet.data.model.RemindCredentialList
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import javax.inject.Inject

/**
 * 首頁 viewModel
 *
 * 管理各個分頁
 *
 * @constructor [WalletRepository], [ModaSharedPreferences]
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mWalletRepository: WalletRepository,
    private val mDatabase: DigitalWalletDB,
    private val mPref: ModaSharedPreferences
) : BaseViewModel() {
    private val mIndexPage = MutableLiveData<PageEnum>()
    val indexPage: LiveData<PageEnum> get() = mIndexPage

    // LiveData 用於觸發提醒視窗顯示
    private val mRemindCredentialListAlerts = MutableLiveData<RemindCredentialList?>()
    val remindCredentialListAlerts: LiveData<RemindCredentialList?> get() = mRemindCredentialListAlerts

    private var mHasAddedCard = false

    init {
        mWalletRepository.setLoginStatus(true)
        selectTab(PageEnum.Wallet)
        mWalletRepository.getWallet()?.also {
            mPref.defaultWalletId = it.uid
        }
    }

    fun selectTab(pageEnum: PageEnum) {
        mIndexPage.postValue(pageEnum)
        mWalletRepository.setPageEnum(pageEnum)
    }

    fun needScrollToTop(hasAdded: Boolean) {
        this.mHasAddedCard = hasAdded
    }

    fun needScrollToTop(): Boolean = this.mHasAddedCard

    fun launchRemindCredentialListAlerts(remindCredentialList: RemindCredentialList?) {
        mRemindCredentialListAlerts.postValue(remindCredentialList?.takeIf { mIndexPage.value == PageEnum.Wallet })
    }

    fun confirmRemindAlert(remindAlert: RemindAlert) {
        viewModelScope.launch(getExceptionHandler()) {
            remindAlert.credentialList.forEach { verifiableCredential ->
                mDatabase.verifiableCredentialDao().update(verifiableCredential)
            }
        }
    }

}