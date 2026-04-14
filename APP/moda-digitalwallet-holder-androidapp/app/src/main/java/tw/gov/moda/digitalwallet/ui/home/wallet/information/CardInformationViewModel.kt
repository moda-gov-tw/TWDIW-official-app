package tw.gov.moda.digitalwallet.ui.home.wallet.information

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.gov.moda.digitalwallet.core.db.DigitalWalletDB
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.core.verifiable.VerifiableManager
import tw.gov.moda.digitalwallet.core.wallet.WalletManager
import tw.gov.moda.digitalwallet.data.db.OperationRecord
import tw.gov.moda.digitalwallet.data.element.OperationEnum
import tw.gov.moda.digitalwallet.data.element.VerificationSourceEnum
import tw.gov.moda.digitalwallet.data.model.Description
import tw.gov.moda.digitalwallet.data.model.VerifiableCredentialDisplay
import tw.gov.moda.digitalwallet.ui.base.LoginBaseViewModel
import tw.gov.moda.digitalwallet.util.BitmapUtil
import tw.gov.moda.diw.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CardInformationViewModel @Inject constructor(
    private val mVerifiableManager: VerifiableManager,
    private val mDatabase: DigitalWalletDB,
    override val mWalletRepository: WalletRepository,
    override val mResourceProvider: ResourceProvider,
    private val mWalletManager: WalletManager,
    private val mGson: Gson
) : LoginBaseViewModel(mWalletRepository, mResourceProvider) {

    private val mCredentialInformation = MutableLiveData<List<VerifiableCredentialDisplay>>()
    val credentialInformation: LiveData<List<VerifiableCredentialDisplay>> get() = mCredentialInformation
    private val mCardName = MutableLiveData<String>()
    val cardName: LiveData<String> get() = mCardName
    private val mIssuer = MutableLiveData<String>()
    val issuer: LiveData<String> get() = mIssuer
    private val mPopBackStack = MutableLiveData<Boolean>()
    val popBackStack: LiveData<Boolean> get() = mPopBackStack
    private val mCardImage = MutableLiveData<Bitmap>()
    val cardImage: LiveData<Bitmap> get() = mCardImage
    private val mCardIALLevel = MutableLiveData<String>()
    val cardIALLevel: LiveData<String> get() = mCardIALLevel
    private val mCardExpireDate = MutableLiveData<String>()
    val cardExpireDate: LiveData<String> get() = mCardExpireDate


    init {
        viewModelScope.launch(getExceptionHandler()) {
            mWalletRepository.getDecodeVerifiableCredential()?.also { vc ->
                mCardIALLevel.postValue("")
                val mDisplay = vc.display
                val issuingUnit = vc.issuingUnit
                val descriptionJson = vc.description
                val bitmap = withContext(Dispatchers.IO) { BitmapUtil.createBitmap(vc.imageBase64) }

                mVerifiableManager.getVerifiableCredential(vc.credential)?.also { decodeVCData ->
                    val list = ArrayList<VerifiableCredentialDisplay>()
                    decodeVCData.vc?.credentialSubject?.field?.data?.forEach { key, value ->
                        val display = vc.credentialSubject.get(key)?.displayArray?.firstOrNull()?.name ?: key
                        list.add(VerifiableCredentialDisplay(display, value ?: ""))
                    }
                    decodeVCData.exp?.let { exp ->
                        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
                        val date = Date(exp * 1000L)
                        mCardExpireDate.postValue(sdf.format(date))
                    }
                    mCredentialInformation.postValue(list)
                    mCardName.postValue(mDisplay)
                    mIssuer.postValue(issuingUnit)
                    val ialLevel = try {
                        mGson.fromJson(descriptionJson, Description::class.java)?.ial?.takeIf { it.isNotBlank() } ?: mResourceProvider.getString(R.string.no_choice)
                    } catch (e: Exception) {
                        mResourceProvider.getString(R.string.no_choice)
                    }
                    mCardIALLevel.postValue(ialLevel)
                    bitmap?.also { mCardImage.postValue(it) }
                }
            }
        }
    }

    override fun verifySuccessful(sourceEnum: VerificationSourceEnum) {
        // 刪除可驗證憑證
        if (sourceEnum == VerificationSourceEnum.DeleteVerifiableCredential) {
            viewModelScope.launch(getExceptionHandler()) {
                mWalletRepository.getDecodeVerifiableCredential()?.also { vc ->
                    mWalletManager.deleteVerifiableCredential(vc, mResourceProvider.getString(R.string.format_remove_card).format(vc.display))
                    mPopBackStack.postValue(true)
                }
            }
        }
    }
}