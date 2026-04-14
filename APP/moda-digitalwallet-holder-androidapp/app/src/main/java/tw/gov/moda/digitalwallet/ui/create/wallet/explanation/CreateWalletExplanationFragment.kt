package tw.gov.moda.digitalwallet.ui.create.wallet.explanation

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.extension.setOnAntiStickClickLisener
import tw.gov.moda.digitalwallet.ui.base.BaseActivity
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.create.wallet.name.CreateWalletNameFragment
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentCreateWalletExplanationBinding

/**
 * 建立皮夾的登入說明頁
 */
@AndroidEntryPoint
class CreateWalletExplanationFragment : BaseFragment<FragmentCreateWalletExplanationBinding>() {

    companion object {
        fun newInstance() = CreateWalletExplanationFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentCreateWalletExplanationBinding = FragmentCreateWalletExplanationBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: CreateWalletExplanationViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()


    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        setToolbar(getString(R.string.login_setting), { imageView ->
            imageView.isVisible = true
            imageView.setImageResource(R.drawable.ic_arrow_left_2_fill)
            imageView.setOnClickListener {
                mActivityViewModel.pageController.popToFirstPage()
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
            mViewModel.confirm()
        }
        (activity as? BaseActivity<*>)?.setObscuredDetector(binding.btnConfirm)
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.launchCreatePinCodeFragment.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                mActivityViewModel.pageController.launchCreatePinCodeFragment()
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
        mActivityViewModel.registerSuccess.observe(viewLifecycleOwner) {
            mViewModel.createWallet()
        }

        mViewModel.launchLoginFragment.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                mActivityViewModel.pageController.launchLoginFragment()
            }
        }
    }
}