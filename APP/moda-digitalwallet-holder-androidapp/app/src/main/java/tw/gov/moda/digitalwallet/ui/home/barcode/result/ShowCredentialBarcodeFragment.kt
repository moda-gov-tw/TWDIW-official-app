package tw.gov.moda.digitalwallet.ui.home.barcode.result

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.data.element.PageEnum
import tw.gov.moda.digitalwallet.extension.getColorSpannable
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.extension.setBold
import tw.gov.moda.digitalwallet.ui.barcode.BarcodeScaleFragment
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.home.HomeFragment
import tw.gov.moda.digitalwallet.ui.home.HomeViewModel
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.digitalwallet.ui.verifiable.presentation.VerifiablePresentationFragment
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentShowCredentialBarcodeBinding

@AndroidEntryPoint
class ShowCredentialBarcodeFragment : BaseFragment<FragmentShowCredentialBarcodeBinding>() {

    companion object {
        fun newInstance() = ShowCredentialBarcodeFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentShowCredentialBarcodeBinding = FragmentShowCredentialBarcodeBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: ShowCredentialBarcodeViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

    }

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        mActivityViewModel.pageController.popBackStack(listOf(VerifiablePresentationFragment::class.hashTag()))

        binding.btnCompleteScan.setOnClickListener {
            mActivityViewModel.pageController.popToFirstPage()
            (parentFragmentManager.findFragmentByTag(HomeFragment::class.hashTag()) as? HomeFragment)?.viewModels<HomeViewModel>()?.also {
                it.value.selectTab(PageEnum.ShowCredential)
            }
        }
        binding.btnRetryGenerateBarcode.setOnClickListener {
            mViewModel.retry()
            mActivityViewModel.pageController.popToFirstPage()
            (parentFragmentManager.findFragmentByTag(HomeFragment::class.hashTag()) as? HomeFragment)?.viewModels<HomeViewModel>()?.also {
                it.value.selectTab(PageEnum.ShowCredential)
            }
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.barcodeTitle.observe(viewLifecycleOwner) { title ->
            binding.tvBarcodeTitle.text = title
        }
        mViewModel.barcodeImage.observe(viewLifecycleOwner) { bitmap ->
            binding.imgBarcode.setImageBitmap(bitmap)
            binding.imgBarcode.setOnClickListener { mActivityViewModel.pageController.launchBarcodeScaleFragment() }
        }
        mViewModel.timeOfCountdownTimer.observe(viewLifecycleOwner) { timer ->
            context?.apply {
                val message = getString(R.string.format_qrcode_countdown_to_invalid).format(timer)
                val stringSpannable = message.getColorSpannable(ContextCompat.getColor(this, R.color.secondary_06), timer).setBold(timer)
                binding.tvCountdownTimer.setText(stringSpannable)
            }
        }
        mViewModel.isValidBarcode.observe(viewLifecycleOwner) { isValid ->
            binding.imgBarcode.isVisible = isValid
            binding.tvCountdownTimer.isVisible = isValid
            binding.imgFailure.isVisible = !isValid
            binding.tvInvalidHint.isVisible = !isValid
            binding.tvInvalidTitle.isVisible = !isValid
            if (!isValid){
                mActivityViewModel.pageController.popBackStack(BarcodeScaleFragment::class.hashTag())
            }
        }
    }
}