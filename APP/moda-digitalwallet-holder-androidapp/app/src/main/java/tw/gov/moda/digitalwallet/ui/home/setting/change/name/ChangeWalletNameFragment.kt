package tw.gov.moda.digitalwallet.ui.home.setting.change.name

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.data.element.PageEnum
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.extension.setError
import tw.gov.moda.digitalwallet.extension.setOnAntiStickClickLisener
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.dialog.MyDialog
import tw.gov.moda.digitalwallet.ui.home.HomeFragment
import tw.gov.moda.digitalwallet.ui.home.HomeViewModel
import tw.gov.moda.digitalwallet.ui.login.pincode.LoginPinCodeFragment
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentChangeWalletNameBinding

/**
 * 更改皮夾名稱頁
 */
@AndroidEntryPoint
class ChangeWalletNameFragment : BaseFragment<FragmentChangeWalletNameBinding>() {

    companion object {
        fun newInstance() = ChangeWalletNameFragment()

        private const val DIALOG_ERROR_WALLET_NAME_REPEAT = "DIALOG_ERROR_WALLET_NAME_REPEAT"
        private const val DIALOG_ERROR_WALLET_NAME_RULE = "DIALOG_ERROR_WALLET_NAME_RULE"
    }

    override fun initViewBinding(container: ViewGroup?): FragmentChangeWalletNameBinding = FragmentChangeWalletNameBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: ChangeWalletNameViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()


    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        setToolbar(getString(R.string.edit_wallet_name), { imageView ->
            imageView.isVisible = true
            imageView.setImageResource(R.drawable.ic_arrow_left_2_fill)
            imageView.setOnClickListener {
                mActivityViewModel.pageController.popBackStack()
            }
        })
        mActivityViewModel.pageController.popBackStack(
            listOf(
                LoginPinCodeFragment::class.hashTag()
            )
        )
        binding.etWalletName.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            activity?.apply {
                if (hasFocus) {
                    binding.tvWalletNameHintOrError.text = getString(R.string.hint_wallet_name_rule)
                    binding.tvWalletNameHintOrError.setTextColor(ContextCompat.getColor(this, R.color.gray_14))
                    binding.etWalletName.setError(false)
                }
            }
        }
        binding.btnCancel.setOnClickListener {
            mActivityViewModel.pageController.popBackStack()
        }
        binding.btnConfirm.setOnAntiStickClickLisener {
            binding.etWalletName.clearFocus()
            mViewModel.confirm(binding.etWalletName.text.toString())
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.originalWalletName.observe(viewLifecycleOwner) { name ->
            binding.tvOriginalWalletName.setText(name)
        }
        mViewModel.errorWalletNameRepeat.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                activity?.apply {
                    MyDialog.Builder(this)
                        .setTitle(getString(R.string.wallet_name_repeat))
                        .setMessage(getString(R.string.wallet_name_repeat_changing_another))
                        .setRightButtonText(R.string.confirm) {
                            binding.tvWalletNameHintOrError.text = getString(R.string.warning_wallet_name_rule)
                            binding.tvWalletNameHintOrError.setTextColor(ContextCompat.getColor(this, R.color.red_02))
                            binding.etWalletName.setError(true)
                        }
                        .show(childFragmentManager, DIALOG_ERROR_WALLET_NAME_REPEAT)
                }
            }
        }
        mViewModel.errorWalletNameRule.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                activity?.apply {
                    MyDialog.Builder(this)
                        .setTitle(getString(R.string.wallet_name_error))
                        .setMessage(getString(R.string.wallet_name_rule_changing_another))
                        .setRightButtonText(R.string.confirm) {
                            binding.tvWalletNameHintOrError.text = getString(R.string.msg_nickname_rule_hint)
                            binding.tvWalletNameHintOrError.setTextColor(ContextCompat.getColor(this, R.color.red_02))
                            binding.etWalletName.setError(true)
                        }
                        .show(childFragmentManager, DIALOG_ERROR_WALLET_NAME_RULE)
                }
            }
        }
        // 皮夾名稱錯誤訊息
        mViewModel.errorWalletName.observe(viewLifecycleOwner) { resId ->
            context?.apply {
                if (resId == -1) {
                    binding.tvWalletNameHintOrError.text = getString(R.string.hint_wallet_name_rule)
                    binding.tvWalletNameHintOrError.setTextColor(ContextCompat.getColor(this, R.color.gray_14))
                    binding.etWalletName.setError(false)
                } else {
                    binding.tvWalletNameHintOrError.text = getString(resId)
                    binding.tvWalletNameHintOrError.setTextColor(ContextCompat.getColor(this, R.color.red_01))
                    binding.etWalletName.setError(true)
                }
            }
        }

        mViewModel.launchWalletFragment.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                mActivityViewModel.pageController.popToFirstPage()
                (parentFragmentManager.findFragmentByTag(HomeFragment::class.hashTag()) as? HomeFragment)?.viewModels<HomeViewModel>()?.also {
                    it.value.selectTab(PageEnum.Wallet)
                }
            }
        }
    }
}