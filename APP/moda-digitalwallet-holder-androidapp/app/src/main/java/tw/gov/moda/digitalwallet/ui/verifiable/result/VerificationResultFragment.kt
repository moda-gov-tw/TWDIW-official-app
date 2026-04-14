package tw.gov.moda.digitalwallet.ui.verifiable.result

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.data.element.PageEnum
import tw.gov.moda.digitalwallet.extension.addItemDecoration
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.extension.setOnAntiStickClickLisener
import tw.gov.moda.digitalwallet.ui.adapter.VerificationResultAdapter
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.home.HomeFragment
import tw.gov.moda.digitalwallet.ui.home.HomeViewModel
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.digitalwallet.ui.verifiable.presentation.VerifiablePresentationFragment
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentVerificationResultBinding

@AndroidEntryPoint
class VerificationResultFragment : BaseFragment<FragmentVerificationResultBinding>() {

    companion object {
        fun newInstance() = VerificationResultFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentVerificationResultBinding = FragmentVerificationResultBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: VerificationResultViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

    }

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        mActivityViewModel.pageController.popBackStack(listOf(VerifiablePresentationFragment::class.hashTag()))

        binding.btnConfirm.setOnAntiStickClickLisener {
            mActivityViewModel.pageController.popToFirstPage()
            (parentFragmentManager.findFragmentByTag(HomeFragment::class.hashTag()) as? HomeFragment)?.viewModels<HomeViewModel>()?.also {
                it.value.selectTab(PageEnum.Wallet)
            }
        }

        context?.apply {
            binding.recyclerView.addItemDecoration(R.drawable.shape_line_gray02)
            binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.recyclerView.adapter = VerificationResultAdapter()
        }

    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.verificationResult.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                binding.tvTitle.text = getString(R.string.data_authorized)
                binding.imgStatus.setImageResource(R.drawable.ic_success)
                binding.tvHint.isVisible = true
                binding.recyclerView.isVisible = true
            } else {
                binding.tvTitle.text = getString(R.string.verification_fall)
                binding.imgStatus.setImageResource(R.drawable.ic_fall)
                binding.tvHint.isVisible = true
                binding.recyclerView.isVisible = false
            }
        }
        mViewModel.verifiableCredentialDisplay.observe(viewLifecycleOwner) { list ->
            (binding.recyclerView.adapter as? VerificationResultAdapter)?.submitList(list)
        }
        mViewModel.verifiableCredentialHint.observe(viewLifecycleOwner) { text ->
            binding.tvHint.text = text
        }
    }
}