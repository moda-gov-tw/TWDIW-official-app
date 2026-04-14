package tw.gov.moda.digitalwallet.ui.barcode

import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.ui.adapter.GuidelineAdapter
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.create.guideline.GuidelineViewModel
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.databinding.FragmentBarcodeScaleBinding
import tw.gov.moda.diw.databinding.FragmentGuidelineBinding


@AndroidEntryPoint
class BarcodeScaleFragment : BaseFragment<FragmentBarcodeScaleBinding>() {

    companion object {
        fun newInstance() = BarcodeScaleFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentBarcodeScaleBinding = FragmentBarcodeScaleBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: BarcodeScaleViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    private var originalBrightness: Float? = null

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        setOriginalBrightness(!hidden)
    }

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        binding.imgClose.setOnClickListener { mActivityViewModel.pageController.popBackStack() }
        binding.btnConfirm.setOnClickListener { mActivityViewModel.pageController.popBackStack() }
    }

    override fun onResume() {
        super.onResume()
        setOriginalBrightness(true)
    }

    override fun onPause() {
        setOriginalBrightness(false)
        super.onPause()
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.dataModel.observe(viewLifecycleOwner) { data ->
            binding.tvTitle.text = data.title
            binding.tvHint.text = data.hint
            binding.imgBarcode.setImageBitmap(data.barcode)
        }
    }

    private fun setOriginalBrightness(isLight: Boolean) {
        activity?.window?.let { window ->
            if (isLight) {
                val lp = window.attributes
                if (originalBrightness == null) {
                    // 儲存原本亮度設定
                    originalBrightness = lp.screenBrightness
                }

                // 將亮度調到最大
                lp.screenBrightness = 1.0f
                window.attributes = lp
            } else {
                originalBrightness?.let { brightness ->
                    val lp = window.attributes
                    lp.screenBrightness = brightness
                    window.attributes = lp
                }
            }
        }
    }
}