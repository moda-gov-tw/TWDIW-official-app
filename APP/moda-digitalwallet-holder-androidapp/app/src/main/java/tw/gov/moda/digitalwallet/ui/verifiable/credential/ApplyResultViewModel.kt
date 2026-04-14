package tw.gov.moda.digitalwallet.ui.verifiable.credential

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.data.db.VerifiableCredential
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.util.BitmapUtil
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ApplyResultViewModel @Inject constructor(
    private val mWalletRepository: WalletRepository
) : BaseViewModel() {
    private val mVerificationResult = MutableLiveData<Boolean>()
    val verificationResult: LiveData<Boolean> get() = mVerificationResult
    private val mVerificationCredential = MutableLiveData<VerifiableCredential>()
    val verificationCredential: LiveData<VerifiableCredential> get() = mVerificationCredential
    private val mBitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> get() = mBitmap

    init {
        viewModelScope.launch {
            if (mWalletRepository.getApplyVerifiableCredential() == null) {
                mVerificationResult.postValue(false)
            } else {
                mWalletRepository.getApplyVerifiableCredential()?.also {
                    val bitmap = withContext(Dispatchers.IO) {
                        BitmapUtil.createBitmap(it.imageBase64)
                    }
                    if (bitmap != null) {
                        val compressBitmap = withContext(Dispatchers.Default) {
                            BitmapUtil.compress(bitmap, 500 * 1024)
                        }
                        mBitmap.postValue(compressBitmap)
                    }
                    mVerificationResult.postValue(true)
                    mVerificationCredential.postValue(it)
                }
            }
        }
    }

    fun createTime(date: Long): String {
        val sdf = SimpleDateFormat("yyyy / MM / dd HH:mm", Locale.getDefault())
        return sdf.format(date)
    }
}