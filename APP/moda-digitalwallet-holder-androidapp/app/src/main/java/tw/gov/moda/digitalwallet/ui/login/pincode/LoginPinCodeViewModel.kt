package tw.gov.moda.digitalwallet.ui.login.pincode

import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.wallet.WalletManager
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.digitalwallet.data.element.VerificationSourceEnum
import tw.gov.moda.digitalwallet.data.model.Purpose
import tw.gov.moda.digitalwallet.data.model.RequestToken
import tw.gov.moda.digitalwallet.extension.sha256
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import javax.inject.Inject

/**
 * PinCode登入頁 viewModel
 *
 * 進行PinCode登入驗證
 *
 * @constructor [WalletManager], [WalletRepository]
 */
@HiltViewModel
class LoginPinCodeViewModel @Inject constructor(
    private val mWalletRepository: WalletRepository,
    private val mGson: Gson
) : BaseViewModel() {
    private val mLoginType = MutableLiveData<VerificationSourceEnum>()
    val loginType: LiveData<VerificationSourceEnum> get() = mLoginType
    private val mTitle = MutableLiveData<String>()
    val title: LiveData<String> get() = mTitle
    private val mInputPinCode = MutableLiveData<String>()
    val inputPinCode: LiveData<String> get() = mInputPinCode
    private val mIsShowPinCode = MutableLiveData(false)
    val isShowPinCode: LiveData<Boolean> get() = mIsShowPinCode
    private val mInputPinCodeError = MutableLiveData<Boolean>()
    val inputPinCodeError: LiveData<Boolean> get() = mInputPinCodeError
    private val mAlertPinCodeLocked = MutableLiveData<Boolean>()
    val alertPinCodeLocked: LiveData<Boolean> get() = mAlertPinCodeLocked
    private var mPinCodeAuthSuccess = MutableLiveData<Boolean>()
    val pinCodeAuthSuccess: LiveData<Boolean> get() = mPinCodeAuthSuccess

    private var mCountPinCodeError = 0

    init {
        mLoginType.postValue(mWalletRepository.getVerificationSource())
        if (mWalletRepository.getVerificationSource() == VerificationSourceEnum.VerifiablePresentation) {
            // 顯示驗證目的
            mWalletRepository.getParseVerifiablePresentation()?.data?.also { parseVPData ->
                parseVPData.requestToken?.split(".")?.getOrNull(1)?.let { Base64.decode(it, Base64.URL_SAFE) }?.let { String(it, Charsets.UTF_8) }?.also { json ->
                    mGson.fromJson(json, RequestToken::class.java)?.also { requestToken ->
                        requestToken.presentationDefinition?.purpose?.let { mGson.fromJson(it, Purpose::class.java) }?.also { purpose: Purpose ->
                            mTitle.postValue(purpose.scenario ?: "")
                        }
                    }
                }
            }
        }
    }

    fun input(input: String) {
        if (input == "-1") {
            mInputPinCode.value = (mInputPinCode.value ?: "").takeIf { it.isNotEmpty() }?.dropLast(1) ?: mInputPinCode.value
        } else {
            mInputPinCode.value = (mInputPinCode.value ?: "") + input
        }
    }

    fun toggleHidePinCode() {
        val isShow = mIsShowPinCode.value?.not() ?: false
        mIsShowPinCode.postValue(isShow)
    }

    fun confirm() {
        viewModelScope.launch(getExceptionHandler()) {
            mWalletRepository.getWallet()?.also { wallet: Wallet ->
                // 判斷皮夾是否已被鎖定
                if (wallet.pincode == AppConstants.Common.LOCKED_PIN_CODE) {
                    mAlertPinCodeLocked.postValue(true)
                    return@launch
                }

                // 驗證皮夾 PIN CODE
                val verifyCode = (mInputPinCode.value + wallet.keyTag).sha256()

                if (verifyCode == wallet.pincode) {
                    // 驗證成功
                    mPinCodeAuthSuccess.value = true
                } else {
                    mCountPinCodeError = mCountPinCodeError + 1
                    if (mCountPinCodeError >= 5) {
                        // 皮夾數入錯誤超過5次被鎖定
                        mAlertPinCodeLocked.postValue(true)
                        mInputPinCodeError.postValue(true)
                    } else {
                        // 驗證失敗
                        mInputPinCodeError.postValue(true)
                    }
                }
            }
        }
    }
}