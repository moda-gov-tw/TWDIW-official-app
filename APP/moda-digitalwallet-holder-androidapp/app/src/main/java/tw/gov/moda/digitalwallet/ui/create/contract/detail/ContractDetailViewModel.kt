package tw.gov.moda.digitalwallet.ui.create.contract.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.data.element.ContractEnum
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.R
import javax.inject.Inject

/**
 * 規約條款內容頁 viewModel
 *
 * 檢查是否同意相關條款
 *
 * @constructor [WalletRepository]
 */
@HiltViewModel
class ContractDetailViewModel @Inject constructor(
    private val mWalletRepository: WalletRepository
) : BaseViewModel() {
    private val mTitle = MutableLiveData<String>()
    val title: LiveData<String> get() = mTitle
    private val mContent = MutableLiveData<String>()
    val content: LiveData<String> get() = mContent

    init {
        mProgressBarWhite.postValue(true)
        mTitle.postValue(mWalletRepository.getContractTitle())
        mContent.postValue(mWalletRepository.getContractContent())
    }

    /**
     * 確認同意或者不同意
     *
     * @param isAgree 是否同意
     */
    fun confirmForAgrees(isAgree: Boolean) {
        mWalletRepository.getContractAgreeLiveData().postValue(isAgree)
    }
}