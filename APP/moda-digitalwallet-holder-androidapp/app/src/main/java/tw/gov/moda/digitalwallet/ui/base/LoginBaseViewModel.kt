package tw.gov.moda.digitalwallet.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.digitalwallet.data.element.VerificationSourceEnum

abstract class LoginBaseViewModel(
    open val mWalletRepository: WalletRepository,
    open val mResourceProvider: ResourceProvider
) : BaseViewModel() {
    private val mShowDeviceSecure = MutableLiveData<Wallet?>()
    val showDeviceSecure: LiveData<Wallet?> get() = mShowDeviceSecure
    private val mIntentDeviceSecure = MutableLiveData<Boolean>()
    val intentDeviceSecure: LiveData<Boolean> get() = mIntentDeviceSecure
    protected val mAlertDeviceSecureDisabled = MutableLiveData<Boolean>()
    val alertDeviceSecureDisabled: LiveData<Boolean> get() = mAlertDeviceSecureDisabled
    protected val mLaunchLoginPinCodeFragment = MutableLiveData<Boolean>()
    val launchLoginPinCodeFragment: LiveData<Boolean> get() = mLaunchLoginPinCodeFragment

    abstract fun verifySuccessful(sourceEnum: VerificationSourceEnum)

    private var mRequireVerificationTime = System.currentTimeMillis()

    fun verifyFail(sourceEnum: VerificationSourceEnum) {
        viewModelScope.launch(getExceptionHandler()) {
            mWalletRepository.getWallet()?.also { wallet ->
                if (wallet.pincode.isBlank()) {
                    // 無法使用 DeviceSecure
                    mAlertDeviceSecureDisabled.postValue(true)
                } else {
                    mLaunchLoginPinCodeFragment.postValue(true)
                }
            }
        }
    }

    fun getVerificationSource(): VerificationSourceEnum = mWalletRepository.getVerificationSource()

    fun requireVerification(sourceEnum: VerificationSourceEnum) {
        // 防止確認按鈕連點觸發
        if (System.currentTimeMillis() - mRequireVerificationTime < 200L) {
            return
        }
        mRequireVerificationTime = System.currentTimeMillis()

        // 初始 LiveData Value
        mShowDeviceSecure.value = null
        mIntentDeviceSecure.value = false
        mAlertDeviceSecureDisabled.value = false
        mLaunchLoginPinCodeFragment.value = false

        // 驗證
        mWalletRepository.setVerificationSource(sourceEnum)
        viewModelScope.launch(getExceptionHandler()) {
            mProgressBar.postValue(true)
            mWalletRepository.getWallet()?.also { wallet: Wallet ->
                if (mResourceProvider.isDeviceSecure()) {
                    if (mResourceProvider.isAboveOrEqualAPI30()) {
                        mShowDeviceSecure.postValue(wallet)
                    } else {
                        mWalletRepository.setCreatingWallet(null)
                        mIntentDeviceSecure.postValue(true)
                    }
                } else {
                    if (wallet.pincode.isBlank()) {
                        // 無法使用 DeviceSecure
                        mAlertDeviceSecureDisabled.postValue(true)
                    } else {
                        mLaunchLoginPinCodeFragment.postValue(true)
                    }
                }
            }
            mProgressBar.postValue(false)
        }
    }
}