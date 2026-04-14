package tw.gov.moda.digitalwallet.ui.home.setting.delete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class DeleteWalletResultViewModel @Inject constructor(
    private val mWalletRepository: WalletRepository
) : BaseViewModel() {
    private val mWalletName = MutableLiveData<String>()
    val walletName: LiveData<String> get() = mWalletName


    init {
        mWalletRepository.getWallet()?.also { wallet: Wallet ->
            mWalletName.postValue(wallet.nickname)
        }
        mWalletRepository.setWallet(null)
    }
}