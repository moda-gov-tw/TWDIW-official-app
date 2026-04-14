package tw.gov.moda.digitalwallet.ui.verifiable.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.data.model.VerifiableCredentialDisplay
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.R
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class VerificationResultViewModel @Inject constructor(
    private val mWalletRepository: WalletRepository,
    private val mResourceProvider: ResourceProvider,
) : BaseViewModel() {

    private val mVerificationResult = MutableLiveData<Boolean>()
    val verificationResult: LiveData<Boolean> get() = mVerificationResult
    private val mVerifiableCredentialDisplay = MutableLiveData<List<VerifiableCredentialDisplay>>()
    val verifiableCredentialDisplay: LiveData<List<VerifiableCredentialDisplay>> get() = mVerifiableCredentialDisplay
    private val mVerifiableCredentialHint = MutableLiveData<String>()
    val verifiableCredentialHint: LiveData<String> get() = mVerifiableCredentialHint

    init {
        mVerificationResult.postValue(mWalletRepository.getVerifiablePresentationResult())

        if (mWalletRepository.getVerifiablePresentationResult().not()) {
            val stringBuilder = StringBuilder()

            val time = createTime(System.currentTimeMillis())
            stringBuilder.append(time)
            stringBuilder.append("\n")
            stringBuilder.append(mResourceProvider.getString(R.string.authorized_fail))
            mVerifiableCredentialHint.postValue(stringBuilder.toString())
        } else {
            val list = mutableListOf<VerifiableCredentialDisplay>()
            mWalletRepository.getVerifiablePresentationResultData()?.let { resultData ->
                val stringBuilder = StringBuilder()
                resultData.datetime?.also {
                    stringBuilder.append(createTime(it))
                    stringBuilder.append("\n")
                }
                resultData.unit?.also {
                    stringBuilder.append(mResourceProvider.getString(R.string.authorized_to_purpose).format(it))
                }
                mVerifiableCredentialHint.postValue(stringBuilder.toString())
                // 顯示VC資料
                resultData.resultData?.forEach { group ->
                    val requireCardList = StringBuilder()
                    // 如果Group沒選卡就不顯示
                    if (group.cardList.isEmpty()) {
                        return@forEach
                    }
                    group.cardList.forEachIndexed { index, card ->
                        val requireFields = StringBuilder()
                        requireFields.append(card.title + "：")
                        card.fields.forEach { field ->
                            requireFields.append(field.title)
                            requireFields.append("、")
                        }
                        requireCardList.append((requireFields.toString().takeIf { it.last().toString() == "、" }?.dropLast(1) ?: requireFields.toString()))
                        if (index != group.cardList.lastIndex) {
                            requireCardList.append("\n")
                        }
                    }
                    val item = VerifiableCredentialDisplay(
                        title = group.title,
                        value = requireCardList.toString()
                    )
                    list.add(item)
                }
                // 顯示客製化欄位
                resultData.customData?.forEach { customData ->
                    val item = VerifiableCredentialDisplay(
                        title = customData.cname,
                        value = customData.value
                    )
                    list.add(item)
                }
            }
            mVerifiableCredentialDisplay.postValue(list)
        }
    }

    private fun createTime(date: Long): String {
        val sdf = SimpleDateFormat("yyyy / MM / dd HH:mm", Locale.getDefault())
        return sdf.format(date)
    }
}