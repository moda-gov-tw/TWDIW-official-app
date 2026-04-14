package tw.gov.moda.digitalwallet.ui.home.setting.change.pincode

import android.text.InputType
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.extension.filterByRegex
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.extension.setError
import tw.gov.moda.digitalwallet.extension.setOnAntiStickClickLisener
import tw.gov.moda.digitalwallet.extension.transformationMethodByChar
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.home.HomeFragment
import tw.gov.moda.digitalwallet.ui.login.pincode.LoginPinCodeFragment
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentChangePinCodeBinding

/**
 * 更改PinCode頁
 */
@AndroidEntryPoint
class ChangePinCodeFragment : BaseFragment<FragmentChangePinCodeBinding>() {

    companion object {
        fun newInstance() = ChangePinCodeFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentChangePinCodeBinding = FragmentChangePinCodeBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: ChangePinCodeViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()


    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        setToolbar(getString(R.string.setting_wallet_password), { imageView ->
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
        binding.btnCancel.setOnClickListener {
            mActivityViewModel.pageController.popBackStack()
        }
        binding.btnConfirm.setOnAntiStickClickLisener {
            mViewModel.confirm(binding.etPincode1.text.toString(), binding.etPincode2.text.toString())
        }
        binding.imgPincode1.setOnClickListener { mViewModel.toggleHidePinCode1() }
        binding.imgPincode2.setOnClickListener { mViewModel.toggleHidePinCode2() }
        binding.etPincode1.doOnTextChanged { text, _, _, _ ->
            mViewModel.toggleEditTextPinCode1(text.isNullOrBlank())
        }
        binding.etPincode2.doOnTextChanged { text, _, _, _ ->
            mViewModel.toggleEditTextPinCode2(text.isNullOrBlank())
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.alreadyExistPinCode.observe(viewLifecycleOwner) { isExisted ->
            if (isExisted) {
                binding.root.findViewById<TextView>(R.id.tv_title).text = getString(R.string.edit_wallet_password)
                binding.tvPincode1Title.text = getString(R.string.setting_new_wallet_password)
                binding.tvPincode2Title.text = getString(R.string.confirm_new_wallet_password)
                binding.tvHint.text = getString(R.string.hint_change_new_pin_code)
            } else {
                binding.root.findViewById<TextView>(R.id.tv_title).text = getString(R.string.setting_wallet_password)
                binding.tvPincode1Title.text = getString(R.string.setting_wallet_password)
                binding.tvPincode2Title.text = getString(R.string.confirm_wallet_password)
                binding.tvHint.text = getString(R.string.hint_setting_new_pin_code)
            }
        }
        mViewModel.launchLoginFragment.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                mActivityViewModel.pageController.popToFirstPage()
                mActivityViewModel.pageController.popBackStack(listOf(HomeFragment::class.hashTag()))
                mActivityViewModel.pageController.launchLoginFragment()
            }
        }
        // 控制PIN CODE輸入規則
        mViewModel.isShowPinCode1.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                binding.imgPincode1.setImageResource(R.drawable.ic_eye_on)
                binding.etPincode1.setInputType(InputType.TYPE_CLASS_NUMBER)
                binding.etPincode1.transformationMethod = null
            } else {
                binding.imgPincode1.setImageResource(R.drawable.ic_eye_off)
                binding.etPincode1.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD)
                binding.etPincode1.transformationMethodByChar('*')
            }
            // 定義正則表達式，允許數字、字母與特定符號
            val regex = Regex("[^0-9]")
            binding.etPincode1.filterByRegex(regex, maxLength = 8)
            binding.etPincode1.setSelection(binding.etPincode1.length())

        }
        // 控制再次PIN CODE輸入規則
        mViewModel.isShowPinCode2.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                binding.imgPincode2.setImageResource(R.drawable.ic_eye_on)
                binding.etPincode2.setInputType(InputType.TYPE_CLASS_NUMBER)
                binding.etPincode2.transformationMethod = null
            } else {
                binding.imgPincode2.setImageResource(R.drawable.ic_eye_off)
                binding.etPincode2.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD)
                binding.etPincode2.transformationMethodByChar('*')
            }
            // 定義正則表達式，允許數字、字母與特定符號
            val regex = Regex("[^0-9]")
            binding.etPincode2.filterByRegex(regex, maxLength = 8)
            binding.etPincode2.setSelection(binding.etPincode2.length())

        }
        // PinCode誤訊息
        mViewModel.errorPinCode1.observe(viewLifecycleOwner) { resId ->
            context?.apply {
                if (resId == -1) {
                    binding.tvPincode1HintOrError.text = getString(R.string.msg_pincode_input_rule)
                    binding.tvPincode1HintOrError.setTextColor(ContextCompat.getColor(this, R.color.gray_14))
                    binding.etPincode1.setError(false)
                } else {
                    binding.tvPincode1HintOrError.text = getString(resId)
                    binding.tvPincode1HintOrError.setTextColor(ContextCompat.getColor(this, R.color.red_01))
                    binding.etPincode1.setError(true)
                }
            }
        }
        mViewModel.enabledConfirm.observe(viewLifecycleOwner) { enabled ->
            binding.btnConfirm.isEnabled = enabled
        }
    }
}