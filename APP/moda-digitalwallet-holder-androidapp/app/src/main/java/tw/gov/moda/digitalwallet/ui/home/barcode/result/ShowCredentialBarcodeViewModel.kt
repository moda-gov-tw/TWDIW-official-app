package tw.gov.moda.digitalwallet.ui.home.barcode.result

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.CountDownTimer
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tw.gov.moda.digitalwallet.core.repository.barcode.BarcodeRepository
import tw.gov.moda.digitalwallet.core.repository.verifiable.VerifiablePresentationRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.ui.barcode.BarcodeScaleViewModel
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.R
import javax.inject.Inject

@HiltViewModel
class ShowCredentialBarcodeViewModel @Inject constructor(
    private val mVerifiablePresentationRepository: VerifiablePresentationRepository,
    private val mBarcodeRepository: BarcodeRepository,
    private val mResourceProvider: ResourceProvider
) : BaseViewModel() {
    private val mBarcodeTitle = MutableLiveData<String>()
    val barcodeTitle: LiveData<String> get() = mBarcodeTitle
    private val mBarcodeImage = MutableLiveData<Bitmap>()
    val barcodeImage: LiveData<Bitmap> get() = mBarcodeImage
    private val mTimeOfCountdownTimer = MutableLiveData<String>()
    val timeOfCountdownTimer: LiveData<String> get() = mTimeOfCountdownTimer
    private val mIsValidBarcode = MutableLiveData<Boolean>()
    val isValidBarcode: LiveData<Boolean> get() = mIsValidBarcode

    private var mCountDownTimer: CountDownTimer? = null

    init {
        viewModelScope.launch(getExceptionHandler()) {
            mBarcodeTitle.postValue(mVerifiablePresentationRepository.getSelectedShowCredential()?.name ?: "")

            val base64Str = mVerifiablePresentationRepository.getResponseOfDwVerifierMgr402i()?.qrcode ?: ""
            val cleanBase64 = base64Str.substringAfter("base64,")

            base64ToScaledBitmap(cleanBase64)?.also { bitmap ->
                mBarcodeImage.postValue(bitmap)

                val dataModel = BarcodeScaleViewModel.BarcodeScaleModel(
                    title = mResourceProvider.getString(R.string.please_show_barcode_to_scan),
                    hint = mVerifiablePresentationRepository.getSelectedShowCredential()?.name ?: "",
                    barcode = bitmap
                )
                mBarcodeRepository.setBarcodeScaleModel(dataModel)
            } ?: run {
                mBarcodeRepository.setBarcodeScaleModel(null)
            }

            refreshCountdown()
        }
    }

    fun retry() {
        mCountDownTimer?.cancel()
        mVerifiablePresentationRepository.isRetryGenerateBarcode(true)
    }


    /**
     * 計算倒數時間
     */
    fun refreshCountdown() {
        mCountDownTimer?.cancel()
        val seconds = mVerifiablePresentationRepository.getResponseOfDwVerifierMgr402i()?.totptimeout?.toLongOrNull() ?: 60L
        mIsValidBarcode.postValue(true)
        mCountDownTimer = object : CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                val timeFormatted = String.format("%02d：%02d", minutes, seconds)
                mTimeOfCountdownTimer.postValue(timeFormatted)
            }

            override fun onFinish() {
                mIsValidBarcode.postValue(false)
            }
        }.start()
    }

    fun base64ToScaledBitmap(base64Str: String, maxWidth: Int = 1080, maxHeight: Int = 1920): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)

            // Step 1: 取得圖片尺寸
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size, options)

            // Step 2: 計算縮放比例
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false

            // Step 3: 解碼並縮放
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size, options)
        } catch (e: Exception) {
            null
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

}