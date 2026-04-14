package tw.gov.moda.digitalwallet.ui.create.wallet.pincode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.core.wallet.WalletManager
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.digitalwallet.extension.sha256
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.R
import javax.inject.Inject


/**
 * 建立PinCode頁 viewModel
 *
 * 建立PinCode
 *
 * @constructor [WalletRepository], [WalletManager]
 */
@HiltViewModel
class CreatePinCodeViewModel @Inject constructor(
    private val mWalletRepository: WalletRepository,
    private val mPref: ModaSharedPreferences,
    private val mResourceProvider: ResourceProvider,
    private val mWalletManager: WalletManager
) : BaseViewModel() {
    private val mIsShowPinCode1 = MutableLiveData(true)
    val isShowPinCode1: LiveData<Boolean> get() = mIsShowPinCode1
    private val mIsShowPinCode2 = MutableLiveData(true)
    val isShowPinCode2: LiveData<Boolean> get() = mIsShowPinCode2
    private val mLaunchLoginFragment = MutableLiveData<Boolean>()
    val launchLoginFragment: LiveData<Boolean> get() = mLaunchLoginFragment
    private val mErrorPinCode1 = MutableLiveData(-1)
    val errorPinCode1: LiveData<Int> get() = mErrorPinCode1

    /**
     * 檢查欄位規則
     * @param pin1 PINCODE 1
     * @param pin2 PINCODE 2
     */
    fun confirm(pin1: String, pin2: String) {
        mErrorPinCode1.postValue(-1)

        if (pin1.length < 4 || pin1.length > 8) {
            mErrorPinCode1.postValue(R.string.msg_pincode_input_error)
            return
        }

        if (pin1 != pin2) {
            mAlertMessage.postValue(R.string.msg_error_password_different)
            return
        }

        // 建立皮夾
        viewModelScope.launch(getExceptionHandler()) {
            mProgressBar.postValue(true)
            mWalletRepository.getCreatingWallet()?.also { wallet: Wallet ->
                // 設定Wallet自定義PINCODE
                wallet.pincode = (pin1 + wallet.keyTag).sha256()
                // 建立皮夾DID
                val initWallet = mWalletManager.init(wallet)
                // 儲存皮夾於SQLite
                val sqlWallet = mWalletManager.create(initWallet)
                // 設定已建立皮夾
                mWalletRepository.setWallet(sqlWallet)
                // 設定預設皮夾編號
                mPref.defaultWalletId = sqlWallet.uid


                mWalletRepository.setCreatingWallet(null)
                mProgressBar.postValue(false)
                mLaunchLoginFragment.postValue(true)
            } ?: error(mResourceProvider.getString(R.string.unknow_reason))
        }
    }

    fun toggleHidePinCode1() {
        val isShow = mIsShowPinCode1.value?.not() ?: false
        mIsShowPinCode1.postValue(isShow)
    }

    fun toggleHidePinCode2() {
        val isShow = mIsShowPinCode2.value?.not() ?: false
        mIsShowPinCode2.postValue(isShow)
    }

}