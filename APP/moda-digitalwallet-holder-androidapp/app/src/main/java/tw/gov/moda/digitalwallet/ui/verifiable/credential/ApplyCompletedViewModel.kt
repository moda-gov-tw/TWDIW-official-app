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
import javax.inject.Inject

@HiltViewModel
class ApplyCompletedViewModel @Inject constructor(
    private val mWalletRepository: WalletRepository
) : BaseViewModel() {
    private val mVerificationCredential = MutableLiveData<VerifiableCredential>()
    val verificationCredential: LiveData<VerifiableCredential> get() = mVerificationCredential
    private val mBitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> get() = mBitmap

    init {
        viewModelScope.launch {
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
                mVerificationCredential.postValue(it)
            }
        }
    }
}