package tw.gov.moda.digitalwallet.ui.home.wallet.record

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.gov.moda.digitalwallet.core.db.DigitalWalletDB
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.verifiable.VerifiableManager
import tw.gov.moda.digitalwallet.data.db.CombinedCredentialOperationRecord
import tw.gov.moda.digitalwallet.data.db.VerifiableCredential
import tw.gov.moda.digitalwallet.data.element.OrderEnum
import tw.gov.moda.digitalwallet.data.model.VerifiableCredentialDisplay
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.util.BitmapUtil
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

/**
 * 卡片紀錄頁 viewModel
 *
 * 記錄卡片歷程
 *
 * @constructor [VerifiableManager], [DigitalWalletDB], [WalletRepository]
 */
@HiltViewModel
class CardRecordViewModel @Inject constructor(
    private val mVerifiableManager: VerifiableManager,
    private val mDatabase: DigitalWalletDB,
    private val mWalletRepository: WalletRepository
) : BaseViewModel() {

    private val mOrderType = MutableLiveData<OrderEnum>(OrderEnum.DESC)
    val orderType: LiveData<OrderEnum> get() = mOrderType
    private val mCredentialInformation = MutableLiveData<List<VerifiableCredentialDisplay>>()
    val credentialInformation: LiveData<List<VerifiableCredentialDisplay>> get() = mCredentialInformation
    private val mCardName = MutableLiveData<String>()
    val cardName: LiveData<String> get() = mCardName
    private val mUpdateDateTime = MutableLiveData<String>()
    val updateDateTime: LiveData<String> get() = mUpdateDateTime
    private val mIssuer = MutableLiveData<String>()
    val issuer: LiveData<String> get() = mIssuer
    private val mCardImage = MutableLiveData<Bitmap>()
    val cardImage: LiveData<Bitmap> get() = mCardImage
    private val mCardIssuedWebUrl = MutableLiveData<String>()
    val cardIssuedWebUrl: LiveData<String> get() = mCardIssuedWebUrl
    private val mCombinedCredentialOperationRecordList = MutableLiveData<List<CombinedCredentialOperationRecord>>()
    val combinedCredentialOperationRecordList: LiveData<List<CombinedCredentialOperationRecord>> get() = mCombinedCredentialOperationRecordList

    private var mVerifiableCredential: VerifiableCredential? = null

    init {
        viewModelScope.launch(getExceptionHandler()) {
            mWalletRepository.getDecodeVerifiableCredential()?.also { vc ->
                mVerifiableCredential = vc
                val mDisplay = vc.display
                val issuingUnit = vc.issuingUnit
                val description = vc.description
                val bitmap = withContext(Dispatchers.IO) { BitmapUtil.createBitmap(vc.imageBase64) }

                mVerifiableManager.getVerifiableCredential(vc.credential)?.also { decodeVCData ->
                    val list = ArrayList<VerifiableCredentialDisplay>()
                    decodeVCData.vc?.credentialSubject?.field?.data?.forEach { key, value ->
                        val display = vc.credentialSubject.get(key)?.displayArray?.firstOrNull()?.name ?: key
                        list.add(VerifiableCredentialDisplay(display, value ?: ""))
                    }
                    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                    mUpdateDateTime.postValue(sdf.format(vc.updateDatetime))
                    mCredentialInformation.postValue(list)
                    mCardName.postValue(mDisplay)
                    mIssuer.postValue(issuingUnit)
                    mCardIssuedWebUrl.postValue(description)
                    bitmap?.also { mCardImage.postValue(it) }
                }
            }

            refreshRecords(OrderEnum.DESC)
        }
    }

    fun refreshRecords(orderType: OrderEnum) {
        viewModelScope.launch(getExceptionHandler()) {
            mOrderType.value = orderType

            mVerifiableCredential?.also { vc ->
                val list = mDatabase.combinedRecordDao().getAllCombinedCredentialRecords(vc.uid).let { if (orderType == OrderEnum.DESC) it.reversed() else it }
                mCombinedCredentialOperationRecordList.postValue(list)
            }
        }
    }
}