package tw.gov.moda.digitalwallet.ui.create.welcome

import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.extension.setOnAntiStickClickLisener
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.login.LoginFragment
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.digitalwallet.ui.splash.SplashFragment
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentWelcomeBinding

/**
 * 歡迎頁
 */
@AndroidEntryPoint
class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>() {

    companion object {
        fun newInstance() = WelcomeFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentWelcomeBinding = FragmentWelcomeBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: WelcomeViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mViewModel.reset()
        }
    }

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        mActivityViewModel.pageController.popBackStack(
            listOf(
                SplashFragment::class.hashTag(),
                LoginFragment::class.hashTag()
            )
        )

        mViewModel.detectHardwareSecurity()
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.isSkipGuideline.observe(viewLifecycleOwner) { isSkip ->
            if (isSkip) {
                binding.btnConfirm.text = getString(R.string.create_the_first_wallet)
                binding.btnConfirm.setOnAntiStickClickLisener {
                    mActivityViewModel.pageController.launchCreateWalletNameFragment()
                }
            } else {
                binding.btnConfirm.text = getString(R.string.start_using_the_digital_wallet)
                binding.btnConfirm.setOnAntiStickClickLisener {
                    mActivityViewModel.pageController.launchGuidelineFragment()
                }
            }
        }
    }


}