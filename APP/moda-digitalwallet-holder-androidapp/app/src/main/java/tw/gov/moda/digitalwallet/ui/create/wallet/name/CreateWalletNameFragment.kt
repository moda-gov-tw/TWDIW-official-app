package tw.gov.moda.digitalwallet.ui.create.wallet.name

import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.extension.setError
import tw.gov.moda.digitalwallet.extension.setOnAntiStickClickLisener
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.create.contract.ContractFragment
import tw.gov.moda.digitalwallet.ui.create.wallet.level.CreateWalletLevelFragment
import tw.gov.moda.digitalwallet.ui.dialog.MyDialog
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentCreateWalletNameBinding

/**
 * 皮夾命名頁
 */
@AndroidEntryPoint
class CreateWalletNameFragment : BaseFragment<FragmentCreateWalletNameBinding>() {

    companion object {
        fun newInstance() = CreateWalletNameFragment()

        private const val DIALOG_ERROR_WALLET_NAME_REPEAT = "DIALOG_ERROR_WALLET_NAME_REPEAT"
    }

    override fun initViewBinding(container: ViewGroup?): FragmentCreateWalletNameBinding = FragmentCreateWalletNameBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: CreateWalletNameViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()


    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        setToolbar(getString(R.string.wallet_naming), { imageView ->
            imageView.isVisible = true
            imageView.setImageResource(R.drawable.ic_arrow_left_2_fill)
            imageView.setOnClickListener {
                mActivityViewModel.pageController.popToFirstPage()
            }
        })

        mActivityViewModel.pageController.popBackStack(
            listOf(
                ContractFragment::class.hashTag())
        )

        binding.btnCancel.setOnClickListener {
            mActivityViewModel.pageController.popBackStack()
        }
        binding.btnConfirm.setOnAntiStickClickLisener {
            mViewModel.confirm(binding.etNickname.text.toString())
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.walletName.observe(viewLifecycleOwner) { name ->
            binding.etNickname.setText(name)
        }
        mViewModel.launchCreatePinCodeFragment.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                mActivityViewModel.pageController.launchCreatePinCodeFragment()
            }

        }
        mViewModel.launchCreateExplanationFragment.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                mActivityViewModel.pageController.launchCreateWalletExplanationFragment()
            }

        }
        mViewModel.showDeviceSecure.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                activity?.apply {
                    mActivityViewModel.registerBiometric(this)
                }
            }
        }
        mViewModel.intentDeviceSecure.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                activity?.apply {
                    mActivityViewModel.authenticateIntentDeviceSecure()
                }
            }
        }
        mActivityViewModel.registerSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                mViewModel.createWallet()
            }
        }
        mViewModel.launchLoginFragment.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                mActivityViewModel.pageController.popBackStack(listOf(CreateWalletLevelFragment::class.hashTag()))
                mActivityViewModel.pageController.launchLoginFragment()
            }
        }
        mViewModel.errorWalletNameRepeat.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                activity?.apply {
                    MyDialog.Builder(this)
                        .setTitle(getString(R.string.wallet_name_already_be_used))
                        .setMessage(getString(R.string.wallet_name_already_be_used_and_changing_another))
                        .setRightButtonText(R.string.confirm)
                        .show(childFragmentManager, DIALOG_ERROR_WALLET_NAME_REPEAT)
                }
            }
        }
        mViewModel.errorWalletName.observe(viewLifecycleOwner) { resId ->
            context?.apply {
                if (resId == -1) {
                    binding.tvWalletNameHintOrError.text = getString(R.string.hint_wallet_name_rule)
                    binding.tvWalletNameHintOrError.setTextColor(ContextCompat.getColor(this, R.color.gray_14))
                    binding.etNickname.setError(false)
                } else {
                    binding.tvWalletNameHintOrError.text = getString(resId)
                    binding.tvWalletNameHintOrError.setTextColor(ContextCompat.getColor(this, R.color.red_01))
                    binding.etNickname.setError(true)
                }
            }
        }
        mViewModel.notFirstWallet.observe(viewLifecycleOwner) { isNotFirst ->
            if (isNotFirst) {
                binding.root.findViewById<TextView>(R.id.tv_title).text = getString(R.string.wallet_setting)
            }
        }
    }
}