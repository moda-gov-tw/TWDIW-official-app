package tw.gov.moda.digitalwallet.ui.create.contract

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.data.element.ContractEnum
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.create.guideline.GuidelineFragment
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentContractBinding


/**
 * 規約條款頁
 */
@AndroidEntryPoint
class ContractFragment : BaseFragment<FragmentContractBinding>() {

    companion object {
        fun newInstance() = ContractFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentContractBinding = FragmentContractBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: ContractViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        setToolbar(getString(R.string.digital_wallet_related_regulations), { imageView ->
            imageView.isVisible = true
            imageView.setImageResource(R.drawable.ic_arrow_left_2_fill)
            imageView.setOnClickListener {
                mActivityViewModel.pageController.popBackStack()
            }
        })

        mActivityViewModel.pageController.popBackStack(
            listOf(
                GuidelineFragment::class.hashTag()
            )
        )

        binding.chkContractOfUse.setOnCheckedChangeListener { _, isChecked ->
            mViewModel.toggleContractOfUse(isChecked)
        }

        binding.layViewContractOfUse.setOnClickListener {
            mViewModel.selectedContractEnum(ContractEnum.Clause)
            mActivityViewModel.pageController.launchContractDetailFragment()
        }

        binding.chkContractOfProfilePrivacy.setOnCheckedChangeListener { _, isChecked ->
            mViewModel.toggleContractOfProfilePrivacy(isChecked)
        }

        binding.layViewContractOfProfilePrivacy.setOnClickListener {
            mViewModel.selectedContractEnum(ContractEnum.Privacy)
            mActivityViewModel.pageController.launchContractDetailFragment()
        }

        binding.btnConfirm.setOnClickListener {
            mActivityViewModel.pageController.launchCreateWalletExplanationFragment()
        }

        binding.btnCancel.setOnClickListener {
            mActivityViewModel.pageController.popToFirstPage()
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.isCheckedContractOfUse.observe(viewLifecycleOwner) { isChecked ->
            if (isChecked != binding.chkContractOfUse.isChecked) {
                binding.chkContractOfUse.isChecked = isChecked
            }
        }
        mViewModel.isCheckedContractOfProfilePrivacy.observe(viewLifecycleOwner) { isChecked ->
            if (isChecked != binding.chkContractOfProfilePrivacy.isChecked) {
                binding.chkContractOfProfilePrivacy.isChecked = isChecked
            }
        }
        mViewModel.enabledConfirm.observe(viewLifecycleOwner) { enable ->
            binding.btnConfirm.isEnabled = enable
        }
        mViewModel.contractAgreeLiveData.observe(viewLifecycleOwner) { isChecked ->
            mViewModel.toggleContract(isChecked)
        }
    }
}