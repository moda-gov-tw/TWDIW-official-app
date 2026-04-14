package tw.gov.moda.digitalwallet.ui.verifiable.credential

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.data.element.CardStatusEnum
import tw.gov.moda.digitalwallet.data.element.PageEnum
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.extension.setOnAntiStickClickLisener
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.home.HomeFragment
import tw.gov.moda.digitalwallet.ui.home.HomeViewModel
import tw.gov.moda.digitalwallet.ui.webview.WebViewFragment
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentApplyResultBinding

@AndroidEntryPoint
class ApplyResultFragment : BaseFragment<FragmentApplyResultBinding>() {

    companion object {
        fun newInstance() = ApplyResultFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentApplyResultBinding = FragmentApplyResultBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: ApplyResultViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        binding.btnConfirm.setOnAntiStickClickLisener {
            mActivityViewModel.pageController.popToFirstPage()
            (parentFragmentManager.findFragmentByTag(HomeFragment::class.hashTag()) as? HomeFragment)?.viewModels<HomeViewModel>()?.also {
                it.value.selectTab(PageEnum.Wallet)
                it.value.needScrollToTop(true)
            }
        }
        mActivityViewModel.pageController.popBackStack(WebViewFragment::class.hashTag())
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.verificationResult.observe(viewLifecycleOwner) { isSuccessful ->
            binding.tvDatetime.isVisible = !isSuccessful
            binding.includeCard.root.isVisible = isSuccessful
            if (isSuccessful) {
                binding.tvTitle.text = getString(R.string.add_verifiable_credential_success)
                binding.imgStatus.setImageResource(R.drawable.ic_success)
            } else {
                binding.tvTitle.text = getString(R.string.failed_to_join_the_credential)
                binding.imgStatus.setImageResource(R.drawable.ic_fall)
                binding.tvHint.text = getString(R.string.please_check_your_device_and_re_join_it)
                binding.tvDatetime.text = mViewModel.createTime(System.currentTimeMillis())
            }
        }
        mViewModel.verificationCredential.observe(viewLifecycleOwner) { verifiableCredential ->
            binding.tvHint.text = getString(R.string.format_apply_completed_hint).format(verifiableCredential.display)
            binding.includeCard.tvTitle.text = verifiableCredential.display
            binding.includeCard.tvIssuer.text = verifiableCredential.issuingUnit
            binding.includeCard.tvCustom.text = verifiableCredential.previewData
            if (verifiableCredential.status == CardStatusEnum.Valid) {
                binding.includeCard.imgInvalidMask.isVisible = false
                binding.includeCard.tvInvalid.isVisible = false
            } else {
                binding.includeCard.imgInvalidMask.isVisible = true
                binding.includeCard.tvInvalid.isVisible = true
            }
            binding.includeCard.imgTrustBadge.isVisible = verifiableCredential.trustBadge
            binding.tvTitle.text = getString(R.string.add_verifiable_credential_success)
            binding.imgStatus.setImageResource(R.drawable.ic_success)
            binding.tvHint.isVisible = true
            binding.tvDatetime.isVisible = false
            binding.includeCard.root.isVisible = true
        }
        mViewModel.bitmap.observe(viewLifecycleOwner) { bitmap ->
            binding.includeCard.imgHead.setImageBitmap(bitmap)
        }
    }
}