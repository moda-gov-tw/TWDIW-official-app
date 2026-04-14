package tw.gov.moda.digitalwallet.ui.main

import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.core.biometric.ModaBiometricManager
import tw.gov.moda.digitalwallet.core.db.DigitalWalletDB
import tw.gov.moda.digitalwallet.core.deeplink.DeeplinkManager
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.repository.verifiable.VerifiablePresentationRepository
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.core.verifiable.VerifiableManager
import tw.gov.moda.digitalwallet.core.wallet.WalletManager
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.digitalwallet.data.element.AutoLogoutEnum
import tw.gov.moda.digitalwallet.data.element.VerifiablePresentationEnum
import tw.gov.moda.digitalwallet.exception.IdentifierException
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.main.controller.DeeplinkController
import tw.gov.moda.digitalwallet.ui.main.controller.PageController
import tw.gov.moda.digitalwallet.util.SingleLiveEvent
import tw.gov.moda.diw.R
import javax.crypto.Cipher
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    val mPref: ModaSharedPreferences,
    val mWalletManager: WalletManager,
    val mDatabase: DigitalWalletDB,
    val mResourceProvider: ResourceProvider,
    val mWalletRepository: WalletRepository,
    val mBiometricManager: ModaBiometricManager,
    val mVerifiableManager: VerifiableManager,
    val mDeeplinkController: DeeplinkController,
    val mDeeplinkManager: DeeplinkManager,
    val mVerifiablePresentationRepository: VerifiablePresentationRepository
) : BaseViewModel() {
    private var mRegisterSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean> get() = mRegisterSuccess
    private var mAuthenticationSuccess = MutableLiveData<Boolean>()
    val authenticationSuccess: LiveData<Boolean> get() = mAuthenticationSuccess
    private var mPinCodeAuthSuccess = MutableLiveData<Boolean>()
    val pinCodeAuthSuccess: LiveData<Boolean> get() = mPinCodeAuthSuccess
    private var mAuthenticationFail = MutableLiveData<Boolean>()
    val authenticationFail: LiveData<Boolean> get() = mAuthenticationFail
    private val mAlertBiometricLocked = SingleLiveEvent<Boolean>()
    val alertBiometricLocked: LiveData<Boolean> get() = mAlertBiometricLocked
    private val mAlertBiometricHasChanged = SingleLiveEvent<Boolean>()
    val alertBiometricHasChanged: LiveData<Boolean> get() = mAlertBiometricHasChanged
    private val mIntentDeviceSecure = SingleLiveEvent<Boolean>()
    val intentDeviceSecure: LiveData<Boolean> get() = mIntentDeviceSecure
    private val mAlertAutoLogout = SingleLiveEvent<Boolean>()
    val alertAutoLogout: LiveData<Boolean> get() = mAlertAutoLogout
    private val mScanStatus = MutableLiveData<Boolean>()
    val scanStatus: LiveData<Boolean> get() = mScanStatus

    val pageController = PageController()
    val deeplinkController: DeeplinkController
        get() = mDeeplinkController


    private val mTimeoutThread = HandlerThread(AppConstants.Common.AUTO_LOGOUT_THREAD)
    private val mTimeoutHandler: Handler
    private var mTimeoutLimit = -1L


    init {
        mTimeoutThread.start()
        mTimeoutHandler = Handler(mTimeoutThread.looper)
        mTimeoutHandler.post(object : Runnable {
            override fun run() {
                if (mTimeoutLimit > 0L && mWalletRepository.isLogin()) {
                    if (System.currentTimeMillis() >= mTimeoutLimit) {
                        // 自動登出
                        mAlertAutoLogout.postValue(true)
                        mTimeoutLimit = -1
                    }
                }
                mTimeoutHandler.postDelayed(this, 1_000)
            }
        })
        mWalletRepository.setAppLink(null)
    }


    private var mInsets: WindowInsetsCompat? = null

    fun setLoginStatus(login: Boolean) {
        if (login) {
            mWalletRepository.setLoginStatus(true)
        } else {
            mWalletRepository.setLoginStatus(false)
            clearLogin()
        }
    }

    fun updateInsets(insets: WindowInsetsCompat) {
        mInsets = insets
    }

    fun getInsets(): WindowInsetsCompat? = mInsets

    fun applink(uri: Uri?) {
        if (uri != null) {
            mWalletRepository.setAppLink(uri)
            if (mWalletRepository.isLogin()) {
                detectAppLink()
            }
        } else {
            pageController.applink(false)
        }
    }

    fun authenticateIntentDeviceSecure(isShow: Boolean = true) {
        mIntentDeviceSecure.postValue(isShow)
    }

    fun setIntentDeviceSecureResult(isSuccessful: Boolean) {
        viewModelScope.launch(getExceptionHandler()) {
            if (isSuccessful) {
                mWalletRepository.getCreatingWallet()?.also { wallet ->
                    mWalletManager.update(wallet)
                    mWalletRepository.setWallet(wallet)
                    mRegisterSuccess.value = true
                } ?: run {
                    mWalletRepository.getWallet()?.also { wallet ->
                        mAuthenticationSuccess.value = true
                    }
                }
            } else {
                mAuthenticationFail.value = true
            }
        }
    }


    fun registerBiometric(activity: FragmentActivity) {
        viewModelScope.launch(getExceptionHandler()) {
            mBiometricManager.register(activity, object : ModaBiometricManager.OnBiometricCallback {
                override fun onSuccess(cipher: Cipher?) {
                    viewModelScope.launch(getExceptionHandler()) {
                        mWalletRepository.getCreatingWallet()?.also { wallet ->
                            mWalletManager.update(wallet)
                            mWalletRepository.setWallet(wallet)
                            mRegisterSuccess.value = true
                        }
                    }
                }

                override fun onFail() {

                }

                override fun onReset() {
                    mAlertBiometricHasChanged.postValue(true)
                }

                override fun onLock() {
                    mAlertBiometricLocked.postValue(true)
                }
            })
        }
    }

    fun authenticateBiometric(activity: FragmentActivity, wallet: Wallet) {
        viewModelScope.launch(getExceptionHandler()) {
            mBiometricManager.authenticate(activity, ByteArray(12), object : ModaBiometricManager.OnBiometricCallback {
                override fun onSuccess(cipher: Cipher?) {
                    viewModelScope.launch(getExceptionHandler()) {
                        mAuthenticationSuccess.value = true
                    }
                }

                override fun onFail() {

                }

                override fun onReset() {
                    mAlertBiometricHasChanged.postValue(true)
                }

                override fun onLock() {
                    mAlertBiometricLocked.postValue(true)
                }
            })
        }
    }


    fun updateAutoLogoutTime() {
        val autoLogoutEnum = mWalletRepository.getWallet()?.autoLogout ?: AutoLogoutEnum.NEVER
        mTimeoutLimit = when (autoLogoutEnum) {
            AutoLogoutEnum.THREE -> System.currentTimeMillis() + (3 * 60 * 1000L)
            AutoLogoutEnum.FIVE -> System.currentTimeMillis() + (5 * 60 * 1000L)
            AutoLogoutEnum.TEN -> System.currentTimeMillis() + (10 * 60 * 1000L)
            AutoLogoutEnum.FIFTEEN -> System.currentTimeMillis() + (15 * 60 * 1000L)
            AutoLogoutEnum.NEVER -> -1
        }
    }

    fun continueUsing() {
        mWalletRepository.isContinueUsing(true)
        pageController.logout()
    }

    fun setPinCodeAuthSuccess(isSuccessful: Boolean) {
        mPinCodeAuthSuccess.postValue(isSuccessful)
    }

    fun setAuthenticationSuccess(isSuccessful: Boolean) {
        mAuthenticationSuccess.postValue(isSuccessful)
    }

    fun setAuthenticationFail(isSuccessful: Boolean) {
        mAuthenticationFail.postValue(isSuccessful)
    }

    fun detectAppLink() {
        mWalletRepository.getAppLink()?.also {
            mVerifiablePresentationRepository.setVerifiablePresentationEnum(VerifiablePresentationEnum.NORMAL)
            parseQRCode(it.toString())
            mWalletRepository.setAppLink(null)
        }
    }

    fun parseQRCode(text: String) {
        viewModelScope.launch(getExceptionHandler { _, throwable ->
            mProgressBar.postValue(false)
            if (throwable is IdentifierException) {
                mAlertSDKMessage.postValue("[${throwable.code}]：" + throwable.message)
            } else if (throwable is JsonSyntaxException) {
                mAlertSDKMessage.postValue(mResourceProvider.getString(R.string.format_qrcode_gson_fail))
            } else {
                mException.postValue(throwable)
            }
        }) {
            mDeeplinkManager.parseDeeplink(text)
        }
    }

    fun setScanEnable(isEnable: Boolean) {
        mScanStatus.postValue(isEnable)
    }

    fun getWebViewUrl(): String {
        return mPref.openURL ?: ""
    }

    private fun clearLogin() {
        mWalletRepository.setWallet(null)
        mWalletRepository.setAppLink(null)
        mWalletRepository.setRequireVerifiableCredential(null)
        mWalletRepository.clearAllOfRequireVerifiableCredentials()
        mWalletRepository.setParseVerifiablePresentation(null)
        mWalletRepository.setApplyVerifiableCredential(null)
        mWalletRepository.setDecodeVerifiableCredential(null)
    }
}