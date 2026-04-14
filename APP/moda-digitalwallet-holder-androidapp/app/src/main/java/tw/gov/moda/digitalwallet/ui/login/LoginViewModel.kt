package tw.gov.moda.digitalwallet.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tw.gov.moda.digitalwallet.core.keystore.ModaKeyStoreManager
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.core.wallet.WalletManager
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.digitalwallet.data.element.VerificationSourceEnum
import tw.gov.moda.digitalwallet.ui.base.LoginBaseViewModel
import tw.gov.moda.digitalwallet.util.SingleLiveEvent
import tw.gov.moda.diw.R
import javax.inject.Inject

/**
 * 登入頁 viewModel
 *
 * 選擇皮夾且進行登入驗證
 *
 * @constructor [WalletManager], [WalletRepository], [ResourceProvider], [ModaKeyStoreManager]
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val mWalletManager: WalletManager,
    override val mWalletRepository: WalletRepository,
    override val mResourceProvider: ResourceProvider,
    private val mPref: ModaSharedPreferences,
    private val mKeyStoreManager: ModaKeyStoreManager
) : LoginBaseViewModel(mWalletRepository, mResourceProvider) {
    private val mWalletNickname = MutableLiveData<String>()
    val walletNickname: LiveData<String> get() = mWalletNickname
    private val mWalletList = MutableLiveData<List<Wallet>>()
    val walletList: LiveData<List<Wallet>> get() = mWalletList
    private val mLaunchHomeFragment = SingleLiveEvent<Boolean>()
    val launchHomeFragment: LiveData<Boolean> get() = mLaunchHomeFragment
    private var mIsNewWallet = SingleLiveEvent<Boolean>()
    val isNewWallet: LiveData<Boolean> get() = mIsNewWallet

    init {
        mWalletRepository.setRequireVerifiableCredential(null)
        mWalletRepository.clearAllOfRequireVerifiableCredentials()
        mWalletRepository.setParseVerifiablePresentation(null)
        mWalletRepository.setApplyVerifiableCredential(null)
        mWalletRepository.setDecodeVerifiableCredential(null)
        initWallet()
    }

    fun initWallet() {
        viewModelScope.launch(getExceptionHandler()) {
            val defaultUID = mPref.defaultWalletId
            val defaultWallet = mWalletManager.getWallet(defaultUID)
            if (defaultWallet != null) {
                selectWallet(defaultWallet)
                mWalletList.postValue(mWalletManager.getAll())

                if (!mKeyStoreManager.hasSecretKey()) {
                    mKeyStoreManager.initSecretKey()
                }
                mIsNewWallet.value = mWalletRepository.isNewWallet()
                mWalletRepository.isNewWallet(false)
            } else {
                error(mResourceProvider.getString(R.string.system_basic_unknown_error))
            }
        }
    }

    fun selectWallet(wallet: Wallet) {
        viewModelScope.launch(getExceptionHandler()) {
            mWalletRepository.setWallet(wallet)
            mWalletNickname.postValue(wallet.nickname)
        }
    }

    fun confirm() {
        mWalletRepository.isContinueUsing(false)
        viewModelScope.launch(getExceptionHandler()) {
            requireVerification(VerificationSourceEnum.LoginPinCode)
        }
    }

    fun getContinueUsing(): Boolean = mWalletRepository.isContinueUsing()

    override fun verifySuccessful(sourceEnum: VerificationSourceEnum) {
        if (sourceEnum == VerificationSourceEnum.LoginPinCode) {
            viewModelScope.launch(getExceptionHandler()) {
                mWalletRepository.getWallet()?.also { wallet: Wallet ->
                    val isSuccessful = mWalletManager.login(wallet)
                    if (isSuccessful) {
                        mLaunchHomeFragment.postValue(true)
                    } else {
                        error(mResourceProvider.getString(R.string.system_basic_unknown_error))
                    }
                } ?: error(mResourceProvider.getString(R.string.unknow_reason))
            }

        }
    }
}