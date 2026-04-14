package tw.gov.moda.digitalwallet.ui.verifiable.authorized

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import tw.gov.moda.digitalwallet.core.repository.verifiable.VerifiablePresentationRepository
import tw.gov.moda.digitalwallet.data.model.AuthorizedCredential
import tw.gov.moda.digitalwallet.data.model.VCName
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class AuthorizedCredentialViewModel @Inject constructor(
    private val mVerifiablePresentationRepository: VerifiablePresentationRepository
): BaseViewModel() {

    private val mAuthorizedCredentialList = MutableLiveData<List<AuthorizedCredential>>()
    val authorizedCredentialList: LiveData<List<AuthorizedCredential>> get() = mAuthorizedCredentialList

    fun getData() {
        val list = mutableListOf<AuthorizedCredential>()
        mVerifiablePresentationRepository.getAuthorizedCredentialList()?.forEach { item ->
            if (list.any { it.card == item.card }) {
                return@forEach
            }
            // 代表有VC資料就不用顯示
            if (item.verifiableCredential != null) {
                return@forEach
            }
            val data = if (item.name.isNotBlank() && item.name.startsWith("{")) {
                val type = object : TypeToken<VCName>() {}.type
                val data = Gson().fromJson(item.name, type) as? VCName
                AuthorizedCredential(
                    card = item.card,
                    orgName = data?.orgTwName ?: "",
                    vcName = data?.vcName ?: "",
                    issuerUrl = data?.issuerUrl ?: ""
                )
            } else {
                AuthorizedCredential(
                    card = item.card,
                    orgName = "",
                    vcName = item.card,
                    issuerUrl = ""
                )
            }
            list.add(data)
        }
        mAuthorizedCredentialList.postValue(list)
    }
}