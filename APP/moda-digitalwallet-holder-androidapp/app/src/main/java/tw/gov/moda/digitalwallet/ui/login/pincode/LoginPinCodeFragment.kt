package tw.gov.moda.digitalwallet.ui.login.pincode

import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.data.element.VerificationSourceEnum
import tw.gov.moda.digitalwallet.extension.filterByRegex
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.extension.setError
import tw.gov.moda.digitalwallet.extension.setOnAntiStickClickLisener
import tw.gov.moda.digitalwallet.extension.transformationMethodByChar
import tw.gov.moda.digitalwallet.ui.base.BaseActivity
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.create.wallet.explanation.CreateWalletExplanationFragment
import tw.gov.moda.digitalwallet.ui.create.wallet.name.CreateWalletNameFragment
import tw.gov.moda.digitalwallet.ui.create.wallet.pincode.CreatePinCodeFragment
import tw.gov.moda.digitalwallet.ui.create.welcome.WelcomeFragment
import tw.gov.moda.digitalwallet.ui.dialog.MyDialog
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.digitalwallet.ui.splash.SplashFragment
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentLoginPinCodeBinding

/**
 * PinCode登入頁
 */
@AndroidEntryPoint
class LoginPinCodeFragment : BaseFragment<FragmentLoginPinCodeBinding>(), View.OnClickListener {

    companion object {
        fun newInstance() = LoginPinCodeFragment()
        private const val DIALOG_PINCODE_LOCKED = "DIALOG_PINCODE_LOCKED"

    }

    override fun initViewBinding(container: ViewGroup?): FragmentLoginPinCodeBinding = FragmentLoginPinCodeBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: LoginPinCodeViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mActivityViewModel.pageController.popBackStack(
                listOf(
                    CreateWalletNameFragment::class.hashTag(),
                    CreatePinCodeFragment::class.hashTag()
                )
            )
        }
    }

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        setToolbar(getString(R.string.wallet_setting), { imageView ->
            imageView.isVisible = true
            imageView.setImageResource(R.drawable.selector_button_arrow2_left)
            imageView.setOnClickListener {
                mActivityViewModel.pageController.popBackStack()
            }
        })
        mActivityViewModel.pageController.popBackStack(
            listOf(
                SplashFragment::class.hashTag(),
                WelcomeFragment::class.hashTag(),
                CreateWalletNameFragment::class.hashTag(),
                CreateWalletExplanationFragment::class.hashTag(),
                CreatePinCodeFragment::class.hashTag()
            )
        )

        binding.tvPin0.setOnClickListener(this)
        binding.tvPin1.setOnClickListener(this)
        binding.tvPin2.setOnClickListener(this)
        binding.tvPin3.setOnClickListener(this)
        binding.tvPin4.setOnClickListener(this)
        binding.tvPin5.setOnClickListener(this)
        binding.tvPin6.setOnClickListener(this)
        binding.tvPin7.setOnClickListener(this)
        binding.tvPin8.setOnClickListener(this)
        binding.tvPin9.setOnClickListener(this)
        binding.tvPinBack.setOnClickListener(this)
        binding.imgPinHidden.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)


        binding.btnConfirm.setOnAntiStickClickLisener { mViewModel.confirm() }

        (activity as? BaseActivity<*>)?.setObscuredDetector(binding.btnConfirm)
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.loginType.observe(viewLifecycleOwner) { source ->
            when (source) {
                VerificationSourceEnum.LoginPinCode -> {
                    binding.layToolbar.isVisible = false
                    binding.imgTitle.isVisible = true
                    binding.imgBackground.isVisible = true
                    binding.btnConfirm.text = getString(R.string.login)
                }

                else -> {
                    binding.layToolbar.isVisible = true
                    binding.imgTitle.isVisible = false
                    binding.imgBackground.isVisible = false
                    binding.btnConfirm.text = getString(R.string.confirm)
                }
            }
        }
        mViewModel.title.observe(viewLifecycleOwner) { title ->
            binding.root.findViewById<TextView>(R.id.tv_title).text = title
        }
        mViewModel.isShowPinCode.observe(viewLifecycleOwner) { isShow ->
            // 控制PIN CODE輸入規則
            if (isShow) {
                binding.imgPinHidden.setImageResource(R.drawable.ic_eye_on)
                binding.etPincode.setInputType(InputType.TYPE_CLASS_NUMBER)
                binding.etPincode.transformationMethod = null
            } else {
                binding.imgPinHidden.setImageResource(R.drawable.ic_eye_off)
                binding.etPincode.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD)
                binding.etPincode.transformationMethodByChar('*')
            }
            val regex = Regex("[^0-9]")
            binding.etPincode.filterByRegex(regex, maxLength = 8)
            binding.etPincode.setSelection(binding.etPincode.length())
        }
        mViewModel.inputPinCode.observe(viewLifecycleOwner) { input ->
            binding.etPincode.setText(input)
            binding.btnConfirm.isEnabled = input.isNotEmpty()
            binding.etPincode.setError(false)
            binding.tvPasswordError.isVisible = false
        }
        mViewModel.inputPinCodeError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                binding.tvPasswordError.isVisible = true
                binding.etPincode.setError(true)
            }
        }
        mViewModel.alertPinCodeLocked.observe(viewLifecycleOwner) { countError ->
            activity?.apply {
                MyDialog.Builder(this)
                    .setTitle(getString(R.string.wallet_password_error))
                    .setMessage(getString(R.string.password_error_over_than_five_times_message))
                    .setRightButtonText(R.string.confirm)
                    .show(supportFragmentManager, DIALOG_PINCODE_LOCKED)
            }
        }
        mViewModel.pinCodeAuthSuccess.observe(viewLifecycleOwner) { isSuccessful ->
            activity?.apply {
                if (isSuccessful) {
                    if (mViewModel.loginType.value == VerificationSourceEnum.LoginPinCode) {
                        mActivityViewModel.pageController.popToFirstPage()
                    }
                    mActivityViewModel.setPinCodeAuthSuccess(isSuccessful)
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.tvPin0 -> mViewModel.input("0")
            binding.tvPin1 -> mViewModel.input("1")
            binding.tvPin2 -> mViewModel.input("2")
            binding.tvPin3 -> mViewModel.input("3")
            binding.tvPin4 -> mViewModel.input("4")
            binding.tvPin5 -> mViewModel.input("5")
            binding.tvPin6 -> mViewModel.input("6")
            binding.tvPin7 -> mViewModel.input("7")
            binding.tvPin8 -> mViewModel.input("8")
            binding.tvPin9 -> mViewModel.input("9")
            binding.tvPinBack -> mViewModel.input("-1")
            binding.imgPinHidden -> mViewModel.toggleHidePinCode()
            binding.btnCancel -> mActivityViewModel.pageController.popBackStack()
        }
    }
}