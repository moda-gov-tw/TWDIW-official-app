package tw.gov.moda.digitalwallet.ui.create.contract

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.data.element.ContractEnum
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.R
import javax.inject.Inject

/**
 * 規約條款頁 viewModel
 *
 * 檢查是否同意相關條款
 *
 * @constructor [WalletRepository]
 */
@HiltViewModel
class ContractViewModel @Inject constructor(
    private val mWalletRepository: WalletRepository,
    private val mResourceProvider: ResourceProvider
) : BaseViewModel() {

    private val mIsCheckedContractOfUse = MutableLiveData<Boolean>()
    val isCheckedContractOfUse: LiveData<Boolean> get() = mIsCheckedContractOfUse
    private val mIsCheckedContractOfProfilePrivacy = MutableLiveData<Boolean>()
    val isCheckedContractOfProfilePrivacy: LiveData<Boolean> get() = mIsCheckedContractOfProfilePrivacy
    private val mEnabledConfirm = MediatorLiveData<Boolean>()
    val enabledConfirm: LiveData<Boolean> get() = mEnabledConfirm

    val contractAgreeLiveData: LiveData<Boolean> = mWalletRepository.getContractAgreeLiveData()

    private var mContractEnum = ContractEnum.Privacy

    init {
        mEnabledConfirm.apply {
            fun integration() {
                value = (mIsCheckedContractOfProfilePrivacy.value) == true && (mIsCheckedContractOfUse.value == true)
            }
            addSource(mIsCheckedContractOfUse) {
                integration()
            }
            addSource(mIsCheckedContractOfProfilePrivacy) {
                integration()
            }
        }
    }

    fun toggleContractOfUse(isChecked: Boolean) {
        mIsCheckedContractOfUse.postValue(isChecked)
    }

    fun toggleContractOfProfilePrivacy(isChecked: Boolean) {
        mIsCheckedContractOfProfilePrivacy.postValue(isChecked)
    }

    fun selectedContractEnum(contractEnum: ContractEnum) {
        mContractEnum = contractEnum
        when (contractEnum) {
            ContractEnum.Clause -> {
                mWalletRepository.setContractTitle(mResourceProvider.getString(R.string.contract_of_use))
                mWalletRepository.setContractContent("file:///android_asset/use_agreement_contract.html")
            }

            ContractEnum.Privacy -> {
                mWalletRepository.setContractTitle(mResourceProvider.getString(R.string.contract_of_privacy))
                mWalletRepository.setContractContent("file:///android_asset/privacy_contract.html")
            }
        }
    }

    fun toggleContract(isChecked: Boolean) {
        when (mContractEnum) {
            ContractEnum.Clause -> toggleContractOfUse(isChecked)
            ContractEnum.Privacy -> toggleContractOfProfilePrivacy(isChecked)
        }
    }

}