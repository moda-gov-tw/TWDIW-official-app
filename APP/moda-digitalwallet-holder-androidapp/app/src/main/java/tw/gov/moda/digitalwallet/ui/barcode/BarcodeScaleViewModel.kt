package tw.gov.moda.digitalwallet.ui.barcode

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import tw.gov.moda.digitalwallet.core.repository.barcode.BarcodeRepository
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.R
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

@HiltViewModel
class BarcodeScaleViewModel @Inject constructor(
    private val mRepository: BarcodeRepository
) : BaseViewModel() {
    data class BarcodeScaleModel(
        val title: String,
        val hint: String,
        val barcode: Bitmap
    )

    private val mDataModel = MutableLiveData<BarcodeScaleModel>()
    val dataModel: LiveData<BarcodeScaleModel> get() = mDataModel

    init {
        mRepository.getBarcodeScaleModel()?.also {
            mDataModel.postValue(it)
        } ?: mAlertMessage.postValue(R.string.barcode_already_invalid)
    }
}