package tw.gov.moda.digitalwallet.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import tw.gov.moda.digitalwallet.exception.IdentifierException
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel() {
    protected val mAlertMessage = MutableLiveData<Int>()
    val alertMessage: LiveData<Int> get() = mAlertMessage
    protected val mAlertSDKMessage = MutableLiveData<String>()
    val alertSDKMessage: LiveData<String> get() = mAlertSDKMessage
    protected val mException = MutableLiveData<Throwable>()
    val exception: LiveData<Throwable> get() = mException
    protected val mProgressBar = MediatorLiveData<Boolean>()
    val progressBar: LiveData<Boolean> get() = mProgressBar
    protected val mProgressBarWhite = MediatorLiveData<Boolean>()
    val progressBarWhite: LiveData<Boolean> get() = mProgressBarWhite

    internal fun getExceptionHandler(
        error: ((CoroutineContext, Throwable) -> Unit)? = null
    ): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { context, throwable ->
            error?.invoke(context, throwable) ?: run {
                mProgressBar.postValue(false)
                mProgressBarWhite.postValue(false)
                if (throwable is IdentifierException) {
                    mAlertSDKMessage.postValue("[${throwable.code}]：" + throwable.message)
                } else {
                    mException.postValue(throwable)
                }
            }
        }
    }
}