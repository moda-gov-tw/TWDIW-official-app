package tw.gov.moda.digitalwallet.ui.home.setting.change.pincode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tw.gov.moda.digitalwallet.core.db.DigitalWalletDB
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.core.wallet.WalletManager
import tw.gov.moda.digitalwallet.data.db.OperationRecord
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.digitalwallet.data.element.OperationEnum
import tw.gov.moda.digitalwallet.extension.sha256
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.R
import javax.inject.Inject


/**
 * 更改PinCode頁 viewModel
 *
 * 更改新的PinCode
 *
 * @constructor [WalletRepository], [WalletManager]
 */
@HiltViewModel
class ChangePinCodeViewModel @Inject constructor(
    private val mWalletRepository: WalletRepository,
    private val mWalletManager: WalletManager,
    private val mResourceProvider: ResourceProvider,
    private val mDatabase: DigitalWalletDB,
) : BaseViewModel() {
    private val mIsShowPinCode1 = MutableLiveData(true)
    val isShowPinCode1: LiveData<Boolean> get() = mIsShowPinCode1
    private val mIsShowPinCode2 = MutableLiveData(true)
    val isShowPinCode2: LiveData<Boolean> get() = mIsShowPinCode2
    private val mLaunchLoginFragment = MutableLiveData<Boolean>()
    val launchLoginFragment: LiveData<Boolean> get() = mLaunchLoginFragment
    private val mErrorPinCode1 = MutableLiveData(-1)
    val errorPinCode1: LiveData<Int> get() = mErrorPinCode1
    private val mAlreadyExistPinCode = MutableLiveData<Boolean>()
    val alreadyExistPinCode: LiveData<Boolean> get() = mAlreadyExistPinCode
    private val mEnabledConfirm = MediatorLiveData<Boolean>()
    val enabledConfirm: LiveData<Boolean> get() = mEnabledConfirm

    private val mIsNullEditTextPinCode1 = MutableLiveData<Boolean>()
    private val mIsNullEditTextPinCode2 = MutableLiveData<Boolean>()

    init {
        mWalletRepository.getWallet()?.also {
            mAlreadyExistPinCode.postValue(it.pincode.isNotBlank())
        }
        mEnabledConfirm.apply {
            fun integration() {
                value = (mIsNullEditTextPinCode1.value) == false || (mIsNullEditTextPinCode2.value == false)
            }
            addSource(mIsNullEditTextPinCode1) {
                integration()
            }
            addSource(mIsNullEditTextPinCode2) {
                integration()
            }
        }
    }


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

        // 變更密碼
        viewModelScope.launch(getExceptionHandler()) {
            mWalletRepository.getWallet()?.also { wallet: Wallet ->
                // 變更密碼
                wallet.pincode = (pin1 + wallet.keyTag).sha256()
                mWalletManager.update(wallet)
                mWalletRepository.setWallet(wallet)

                // 異動紀錄：紀錄變更皮夾密碼
                val operationRecord = OperationRecord(
                    walletId = wallet.uid,
                    vcId = null,
                    text = mResourceProvider.getString(R.string.format_change_pincode).format(wallet.nickname),
                    status = OperationEnum.EDIT_WALLET_PINCODE,
                    datetime = System.currentTimeMillis()
                )
                mDatabase.operationRecordDao().insert(operationRecord)
            }


            mLaunchLoginFragment.postValue(true)
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

    fun toggleEditTextPinCode1(isNull: Boolean) {
        mIsNullEditTextPinCode1.value = isNull
    }

    fun toggleEditTextPinCode2(isNull: Boolean) {
        mIsNullEditTextPinCode2.value = isNull
    }

}