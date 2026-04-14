package tw.gov.moda.digitalwallet.ui.create.guideline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

/**
 * 教學導覽頁 viewModel
 *
 * 頁面控制，教學STEP1 ~ STEP3
 *
 * @constructor empty
 */
@HiltViewModel
class GuidelineViewModel @Inject constructor(
) : BaseViewModel() {
    private val mIndexPage = MutableLiveData<Int>(0)
    val indexPage: LiveData<Int> get() = mIndexPage
    private val mLaunchContractFragment = MutableLiveData<Boolean>()
    val launchContractFragment: LiveData<Boolean> get() = mLaunchContractFragment
    private val mBackStack = MutableLiveData<Boolean>()
    val backStack: LiveData<Boolean> get() = mBackStack

    /**
     * 按鈕切換頁面
     * @param position 目前頁數
     * @param offset 1: 下一頁, -1: 上一頁
     */
    fun switchPage(position: Int, offset: Int) {
        val nextIndex = position + offset
        if (nextIndex >= 3) {
            mLaunchContractFragment.postValue(true)
        } else if (nextIndex < 0) {
            mBackStack.postValue(true)
        } else {
            mIndexPage.postValue(min(2, max(0, nextIndex)))
        }
    }


    /**
     * 滑動頁面
     * @param position 目前頁數
     * @param isChangePage 是否有滑動真實頁面
     */
    fun scrollPage(position: Int, isChangePage: Boolean) {
        if (isChangePage){
            return
        }

        if (position == 2) {
            mLaunchContractFragment.postValue(true)
        } else if (position == 0) {
            mBackStack.postValue(true)
        }
    }


}