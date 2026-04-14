package tw.gov.moda.digitalwallet.ui.create.wallet.explanation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.core.wallet.WalletManager
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.R
import java.security.SecureRandom
import java.util.UUID
import javax.inject.Inject


/**
 * 建立皮夾的登入說明頁 viewModel
 *
 * @constructor [WalletRepository], [ResourceProvider], [ModaSharedPreferences], [WalletManager]
 */
@HiltViewModel
class CreateWalletExplanationViewModel @Inject constructor(
    private val mWalletRepository: WalletRepository,
    private val mResourceProvider: ResourceProvider,
    private val mPref: ModaSharedPreferences,
    private val mWalletManager: WalletManager
) : BaseViewModel() {

    private val mShowDeviceSecure = MutableLiveData<Boolean>()
    val showDeviceSecure: LiveData<Boolean> get() = mShowDeviceSecure
    private val mIntentDeviceSecure = MutableLiveData<Boolean>()
    val intentDeviceSecure: LiveData<Boolean> get() = mIntentDeviceSecure
    private val mLaunchCreatePinCodeFragment = MutableLiveData<Boolean>()
    val launchCreatePinCodeFragment: LiveData<Boolean> get() = mLaunchCreatePinCodeFragment
    private val mLaunchLoginFragment = MutableLiveData<Boolean>()
    val launchLoginFragment: LiveData<Boolean> get() = mLaunchLoginFragment

    fun confirm() {
        mPref.isDisplayedCreateWalletExplanation = true
        if (mResourceProvider.isDeviceSecure()) {
            // 生物辨識 / PINCODE / 圖形鎖定
            if (mResourceProvider.isAboveOrEqualAPI30()) {
                mShowDeviceSecure.postValue(true)
            } else {
                mIntentDeviceSecure.postValue(true)
            }
        } else {
            // 自定義 PINCODE
            mLaunchCreatePinCodeFragment.postValue(true)
        }

        viewModelScope.launch(getExceptionHandler()) {
            // 建立錢包
            val uuid = UUID.nameUUIDFromBytes(ByteArray(20).apply { SecureRandom().nextBytes(this) }).toString()
            val wallet = Wallet(nickname = mResourceProvider.getString(R.string.my_wallet), keyTag = uuid)
            mWalletRepository.setCreatingWallet(wallet)
        }
    }

    fun createWallet() {

        viewModelScope.launch(getExceptionHandler()) {
            mProgressBar.postValue(true)
            mWalletRepository.getCreatingWallet()?.also { wallet: Wallet ->
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
}