package tw.gov.moda.digitalwallet.ui.create.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import tw.gov.moda.digitalwallet.core.keystore.ModaKeyStoreManager
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.data.element.ContractEnum
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import javax.inject.Inject

/**
 * 歡迎頁 viewModel
 *
 * 檢測設備是否擁有StrongBox的HSM。
 * 判斷是否需要顯示導覽流程。
 *
 * @constructor 注入金鑰管理器[ModaKeyStoreManager],[WalletRepository],[ModaSharedPreferences]
 */
@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val mKeyStoreManager: ModaKeyStoreManager,
    private val mWalletRepository: WalletRepository,
    private val mPref: ModaSharedPreferences
) : BaseViewModel() {
    private val mIsSkipGuideline = MutableLiveData<Boolean>()
    val isSkipGuideline: LiveData<Boolean> get() = mIsSkipGuideline

    init {
        reset()
    }

    /**
     * 檢查金鑰庫是否支援StrongBox
     */
    fun detectHardwareSecurity() {
        mKeyStoreManager.detectHardwareSecurity()
    }

    /**
     * 1. 重新設定WalletRepository的livedata
     * 2. 判斷是否需要跳過導覽頁
     */
    fun reset() {
        mWalletRepository.getContractAgreeLiveData().postValue(false)
        mIsSkipGuideline.postValue(mPref.skipGuideline)
    }
}