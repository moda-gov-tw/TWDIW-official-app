package tw.gov.moda.digitalwallet.ui.verifiable.credential

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.data.element.CardStatusEnum
import tw.gov.moda.digitalwallet.data.element.PageEnum
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.extension.setOnAntiStickClickLisener
import tw.gov.moda.digitalwallet.ui.base.BaseBottomSheetDialogFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.home.HomeFragment
import tw.gov.moda.digitalwallet.ui.home.HomeViewModel
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.BottomSheetApplyCompletedBinding


@AndroidEntryPoint
class ApplyCompletedBottomSheetDialogFragment : BaseBottomSheetDialogFragment<BottomSheetApplyCompletedBinding>() {
    companion object {
        fun newInstance() = ApplyCompletedBottomSheetDialogFragment()
    }

    override fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?): BottomSheetApplyCompletedBinding {
        return BottomSheetApplyCompletedBinding.inflate(inflater, container, false)
    }

    override fun getViewModel(): BaseViewModel? = mViewModel


    private val mViewModel: ApplyCompletedViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setOnDismissListener {
            mActivityViewModel.pageController.popToFirstPage()
            (parentFragmentManager.findFragmentByTag(HomeFragment::class.hashTag()) as? HomeFragment)?.viewModels<HomeViewModel>()?.also {
                it.value.needScrollToTop(true)
                it.value.selectTab(PageEnum.Wallet)
            }
        }

    }

    override fun initView() {
        super.initView()
        binding.btnClose.setOnClickListener {
            dismissDialog()
        }

        binding.btnConfirm.setOnAntiStickClickLisener {
            dismissDialog()
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.verificationCredential.observe(viewLifecycleOwner) { verifiableCredential ->
            binding.tvTitle.text = getString(R.string.add_verifiable_credential_success)
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
        }
        mViewModel.bitmap.observe(viewLifecycleOwner) { bitmap ->
            binding.includeCard.imgHead.setImageBitmap(bitmap)
        }
    }

    private fun dismissDialog() {
        dismiss()
        mActivityViewModel.pageController.popToFirstPage()
        (parentFragmentManager.findFragmentByTag(HomeFragment::class.hashTag()) as? HomeFragment)?.viewModels<HomeViewModel>()?.also {
            it.value.needScrollToTop(true)
            it.value.selectTab(PageEnum.Wallet)
        }
    }
}






