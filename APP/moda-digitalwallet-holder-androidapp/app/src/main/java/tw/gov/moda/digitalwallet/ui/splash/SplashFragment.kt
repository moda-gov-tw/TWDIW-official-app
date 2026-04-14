package tw.gov.moda.digitalwallet.ui.splash

import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.extension.showMessageDialog
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentSplashBinding

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    companion object {
        fun newInstance() = SplashFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentSplashBinding = FragmentSplashBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: SplashViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.launchWelcomeFragment.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                mActivityViewModel.pageController.launchWelcomeFragment()
            }
        }
        mViewModel.launchLoginFragment.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                mActivityViewModel.pageController.launchLoginFragment()
            }
        }
        mViewModel.migrationDatabaseForEncryption.observe(viewLifecycleOwner) { isMigration ->
            if (isMigration) {
                context?.apply {
                    mViewModel.encryptRoomDatabase(this)
                }
            }
        }
        mViewModel.migrationDatabaseForRecords.observe(viewLifecycleOwner) { isMigration ->
            if (isMigration) {
                context?.apply {
                    mViewModel.migrationDatabaseForRecords()
                }
            }
        }
        mViewModel.alertRebootApplication.observe(viewLifecycleOwner) { isReboot ->
            if (isReboot) {
                context?.apply {
                    val msg = getString(R.string.msg_another_exception)
                    showMessageDialog(childFragmentManager, msg, {
                        System.exit(0)
                    })
                }
            }
        }
    }
}