package tw.gov.moda.digitalwallet.ui.home.scan

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tw.gov.moda.digitalwallet.core.db.DigitalWalletDB
import tw.gov.moda.digitalwallet.core.deeplink.DeeplinkManager
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.repository.verifiable.VerifiablePresentationRepository
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.core.verifiable.VerifiableManager
import tw.gov.moda.digitalwallet.data.element.VerifiablePresentationEnum
import tw.gov.moda.digitalwallet.exception.IdentifierException
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.R
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * 掃描頁 viewModel
 *
 * 控制VC與VP的判斷
 *
 * @constructor [DigitalWalletDB], [VerifiableManager], [WalletRepository], [ModaSharedPreferences]
 */
@HiltViewModel
class ScanViewModel @Inject constructor(
    private val mWalletRepository: WalletRepository,
    private val mDeeplinkManager: DeeplinkManager,
    private val mVerifiablePresentationRepository: VerifiablePresentationRepository,
    private val mResourceProvider: ResourceProvider
) : BaseViewModel() {
    private val mLaunchVerifiablePresentationFragment = MutableLiveData<Boolean>()
    val launchVerifiablePresentationFragment: LiveData<Boolean> get() = mLaunchVerifiablePresentationFragment
    private val mAlertParsingQRCodeError = MutableLiveData<Boolean>()
    val alertParsingQRCodeError: LiveData<Boolean> get() = mAlertParsingQRCodeError
    private val mAlertEmptyVerifiableCredential = MutableLiveData<Boolean>()
    val alertEmptyVerifiableCredential: LiveData<Boolean> get() = mAlertEmptyVerifiableCredential
    private val mAddVerifiableCredentialSuccessful = MutableLiveData<String>()
    val addVerifiableCredentialSuccessful: LiveData<String> get() = mAddVerifiableCredentialSuccessful

    private val mIsLockedParse = AtomicBoolean(false)

    fun parseQRCode(text: String) {
        // 比較是否為鎖定狀態，是則return ，否則 set true.
        if (!mIsLockedParse.compareAndSet(false, true)) return

        mVerifiablePresentationRepository.setVerifiablePresentationEnum(VerifiablePresentationEnum.NORMAL)
        viewModelScope.launch(getExceptionHandler { _, throwable ->
            mIsLockedParse.set(false)
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
            mIsLockedParse.set(false)
        }
    }
}