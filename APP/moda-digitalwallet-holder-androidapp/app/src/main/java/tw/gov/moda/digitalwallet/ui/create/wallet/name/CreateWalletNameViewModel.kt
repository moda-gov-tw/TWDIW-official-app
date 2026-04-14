package tw.gov.moda.digitalwallet.ui.create.wallet.name

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
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.util.SingleLiveEvent
import tw.gov.moda.diw.R
import java.security.SecureRandom
import java.util.UUID
import javax.inject.Inject


/**
 * 皮夾命名頁 viewModel
 *
 * 建立皮夾名稱頁面
 *
 * @constructor [WalletRepository], [ResourceProvider], [ModaSharedPreferences], [WalletManager]
 */
@HiltViewModel
class CreateWalletNameViewModel @Inject constructor(
    private val mWalletRepository: WalletRepository,
    private val mResourceProvider: ResourceProvider,
    private val mPref: ModaSharedPreferences,
    private val mWalletManager: WalletManager
) : BaseViewModel() {
    private val mWalletName = MutableLiveData<String>()
    val walletName: LiveData<String> get() = mWalletName
    private val mShowDeviceSecure = SingleLiveEvent<Boolean>()
    val showDeviceSecure: LiveData<Boolean> get() = mShowDeviceSecure
    private val mIntentDeviceSecure = SingleLiveEvent<Boolean>()
    val intentDeviceSecure: LiveData<Boolean> get() = mIntentDeviceSecure
    private val mLaunchLoginFragment = SingleLiveEvent<Boolean>()
    val launchLoginFragment: LiveData<Boolean> get() = mLaunchLoginFragment
    private val mLaunchCreatePinCodeFragment = SingleLiveEvent<Boolean>()
    val launchCreatePinCodeFragment: LiveData<Boolean> get() = mLaunchCreatePinCodeFragment
    private val mLaunchCreateExplanationFragment = SingleLiveEvent<Boolean>()
    val launchCreateExplanationFragment: LiveData<Boolean> get() = mLaunchCreateExplanationFragment
    private var mErrorWalletNameRepeat = SingleLiveEvent<Boolean>()
    val errorWalletNameRepeat: LiveData<Boolean> get() = mErrorWalletNameRepeat
    private val mErrorWalletName = MutableLiveData(-1)
    val errorWalletName: LiveData<Int> get() = mErrorWalletName
    private val mNotFirstWallet = MutableLiveData<Boolean>()
    val notFirstWallet: LiveData<Boolean> get() = mNotFirstWallet

    init {
        viewModelScope.launch(getExceptionHandler()) {
            var count = mWalletManager.getCount()
            mNotFirstWallet.postValue(count > 0)
            val nameDefault = mResourceProvider.getString(R.string.format_my_number_of_wallet).format(++count)
            mWalletName.postValue(nameDefault)
        }
    }

    /**
     * 檢查欄位規則
     */
    fun confirm(name: String) {
        mErrorWalletName.value = -1

        if (name.length == 0) {
            mErrorWalletName.postValue(R.string.msg_nickname_rule_hint)
            return
        }

        // 檢查名稱規則
        val regex = "^[A-Za-z0-9\\u4e00-\\u9fa5]+$".toRegex()
        if (!name.matches(regex)) {
            mErrorWalletName.postValue(R.string.msg_nickname_rule_hint)
            return
        }

        viewModelScope.launch(getExceptionHandler()) {
            // 檢查名稱是否重複
            if (mWalletManager.detectNicknameRepeat(name)) {
                mErrorWalletNameRepeat.value = true
                return@launch
            }


            // 建立錢包
            val uuid = UUID.nameUUIDFromBytes(ByteArray(20).apply { SecureRandom().nextBytes(this) }).toString()
            val wallet = Wallet(nickname = name, keyTag = uuid)
            mWalletRepository.setCreatingWallet(wallet)

            if (mPref.isDisplayedCreateWalletExplanation) {
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
            } else {
                // 顯示登入設定說明頁面
                mLaunchCreateExplanationFragment.postValue(true)
            }
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