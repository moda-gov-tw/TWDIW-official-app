package tw.gov.moda.digitalwallet.ui.home.setting.change.name

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.wallet.WalletManager
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.util.SingleLiveEvent
import tw.gov.moda.diw.R
import javax.inject.Inject


/**
 * 更改皮夾名稱頁 viewModel
 *
 * 更改新的皮夾名稱
 *
 * @constructor [WalletRepository], [WalletManager]
 */
@HiltViewModel
class ChangeWalletNameViewModel @Inject constructor(
    private val mWalletRepository: WalletRepository,
    private val mWalletManager: WalletManager
) : BaseViewModel() {
    private val mOriginalWalletName = MutableLiveData<String>()
    val originalWalletName: LiveData<String> get() = mOriginalWalletName
    private val mErrorWalletName = MutableLiveData(-1)
    val errorWalletName: LiveData<Int> get() = mErrorWalletName
    private var mErrorWalletNameRepeat = SingleLiveEvent<Boolean>()
    val errorWalletNameRepeat: LiveData<Boolean> get() = mErrorWalletNameRepeat
    private var mErrorWalletNameRule = SingleLiveEvent<Boolean>()
    val errorWalletNameRule: LiveData<Boolean> get() = mErrorWalletNameRule
    private val mLaunchWalletFragment = SingleLiveEvent<Boolean>()
    val launchWalletFragment: LiveData<Boolean> get() = mLaunchWalletFragment

    init {
        mOriginalWalletName.postValue(mWalletRepository.getWallet()?.nickname ?: "")
    }

    /**
     * 檢查欄位規則
     * @param name 新的名稱
     */
    fun confirm(name: String) {
        mErrorWalletName.value = -1

        if (name.length == 0) {
            mErrorWalletName.postValue(R.string.msg_nickname_rule_hint)
            return
        }

        viewModelScope.launch(getExceptionHandler()) {
            // 檢查名稱規則
            val regex = "^[A-Za-z0-9\\u4e00-\\u9fa5]+$".toRegex()
            if (!name.matches(regex)) {
                mErrorWalletNameRule.value = true

                return@launch
            }
            // 檢查名稱是否重複
            if (mWalletManager.detectNicknameRepeat(name)) {
                mErrorWalletNameRepeat.value = true
                return@launch
            }
            mProgressBar.postValue(true)
            mWalletRepository.getWallet()?.also { wallet: Wallet ->
                wallet.nickname = name
                mWalletManager.update(wallet)
                mWalletRepository.setWallet(wallet)
                mLaunchWalletFragment.postValue(true)
            }
            mProgressBar.postValue(false)
        }
    }

}