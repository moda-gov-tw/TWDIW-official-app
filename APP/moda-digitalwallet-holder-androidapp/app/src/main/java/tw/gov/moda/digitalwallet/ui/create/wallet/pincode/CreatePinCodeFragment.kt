package tw.gov.moda.digitalwallet.ui.create.wallet.pincode

import android.text.InputType
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import tw.gov.moda.digitalwallet.ui.create.wallet.level.CreateWalletLevelFragment
import tw.gov.moda.digitalwallet.ui.create.wallet.name.CreateWalletNameFragment
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentCreatePinCodeBinding

/**
 * 建立PinCode頁
 */
@AndroidEntryPoint
class CreatePinCodeFragment : BaseFragment<FragmentCreatePinCodeBinding>() {

    companion object {
        fun newInstance() = CreatePinCodeFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentCreatePinCodeBinding = FragmentCreatePinCodeBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: CreatePinCodeViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()


    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)

        setToolbar(getString(R.string.login_setting), { imageView ->
            imageView.isVisible = true
            imageView.setImageResource(R.drawable.ic_arrow_left_2_fill)
            imageView.setOnClickListener {
                mActivityViewModel.pageController.popBackStack()
            }
        })

        mActivityViewModel.pageController.popBackStack(
            listOf(
                CreateWalletNameFragment::class.hashTag()
            )
        )

        binding.btnCancel.setOnClickListener {
            mActivityViewModel.pageController.popToFirstPage()
        }
        binding.btnConfirm.setOnAntiStickClickLisener {
            mViewModel.confirm(binding.etPincode1.text.toString(), binding.etPincode2.text.toString())
        }
        binding.imgPincode1.setOnClickListener { mViewModel.toggleHidePinCode1() }
        binding.imgPincode2.setOnClickListener { mViewModel.toggleHidePinCode2() }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.launchLoginFragment.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                mActivityViewModel.pageController.popBackStack(listOf(CreateWalletLevelFragment::class.hashTag()))
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
    }
}