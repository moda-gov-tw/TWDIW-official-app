package tw.gov.moda.digitalwallet.ui.create.wallet.level

import dagger.hilt.android.lifecycle.HiltViewModel
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.core.wallet.WalletManager
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import javax.inject.Inject


/**
 * 建立皮夾的類型選擇頁 viewModel
 *
 * @constructor [WalletRepository]
 */
@HiltViewModel
class CreateWalletLevelViewModel @Inject constructor(
    private val mWalletRepository: WalletRepository
) : BaseViewModel() {
    init {
        mWalletRepository.isNewWallet(false)
    }
}