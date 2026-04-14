package tw.gov.moda.digitalwallet.ui.verifiable.presentation.contract

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import tw.gov.moda.digitalwallet.core.repository.verifiable.VerifiablePresentationRepository
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class VerifiablePresentationContractViewModel @Inject constructor(
    private val mVerifiablePresentationRepository: VerifiablePresentationRepository,
) : BaseViewModel() {
    private val mTitle = MutableLiveData<String>()
    val title: LiveData<String> get() = mTitle
    private val mContent = MutableLiveData<String>()
    val content: LiveData<String> get() = mContent

    init {
        mProgressBarWhite.postValue(true)
        mTitle.postValue(mVerifiablePresentationRepository.getContractTitle() ?: "")
        mContent.postValue(mVerifiablePresentationRepository.getContractContent())
    }
}