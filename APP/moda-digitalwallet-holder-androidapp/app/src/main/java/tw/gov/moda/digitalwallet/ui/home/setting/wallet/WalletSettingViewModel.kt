package tw.gov.moda.digitalwallet.ui.home.setting.wallet

import android.hardware.biometrics.BiometricManager
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tw.gov.moda.digitalwallet.core.biometric.ModaBiometricManager
import tw.gov.moda.digitalwallet.core.db.DigitalWalletDB
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.core.wallet.WalletManager
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.digitalwallet.data.element.VerificationSourceEnum
import tw.gov.moda.digitalwallet.ui.base.LoginBaseViewModel
import tw.gov.moda.digitalwallet.util.SingleLiveEvent
import javax.inject.Inject

/**
 * 皮夾設定頁 viewModel
 */
@HiltViewModel
class WalletSettingViewModel @Inject constructor(
    private val mWalletManager: WalletManager,
    override val mWalletRepository: WalletRepository,
    override val mResourceProvider: ResourceProvider,
    private val mDatabase: DigitalWalletDB,
    private val mBiometricManager: ModaBiometricManager
) : LoginBaseViewModel(mWalletRepository, mResourceProvider) {

    private val mDeviceSecureEnable = MutableLiveData<Boolean>()
    val deviceSecureEnable: LiveData<Boolean> get() = mDeviceSecureEnable
    private val mAlreadyExistPinCode = SingleLiveEvent<Boolean>()
    val alreadyExistPinCode: LiveData<Boolean> get() = mAlreadyExistPinCode
    private val mVerificationSuccessful = SingleLiveEvent<VerificationSourceEnum>()
    val verificationSuccessful: LiveData<VerificationSourceEnum> get() = mVerificationSuccessful
    private val mLogout = SingleLiveEvent<Boolean>()
    val logout: LiveData<Boolean> get() = mLogout

    private var mBiometricStateBefore: Boolean? = null

    init {
        mWalletRepository.getWallet()?.also {
            mAlreadyExistPinCode.postValue(it.pincode.isNotBlank())
        }
        mBiometricStateBefore = getBiometricState()
        detectBiometricEnrolled()
    }

    fun detectBiometricEnrolled() {
        val deviceSecureEnable = getBiometricState()
        mDeviceSecureEnable.postValue(deviceSecureEnable)
        if (mBiometricStateBefore != deviceSecureEnable) {
            mLogout.postValue(true)
        }
    }

    fun editWalletPassword() {
        requireVerification(VerificationSourceEnum.ChangePinCode)
    }

    override fun verifySuccessful(sourceEnum: VerificationSourceEnum) {
        mVerificationSuccessful.postValue(sourceEnum)
    }

    private fun getBiometricState(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            mResourceProvider.isDeviceSecure()
        } else {
            mBiometricManager.canEnableBiometric() != BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
        }
    }
}