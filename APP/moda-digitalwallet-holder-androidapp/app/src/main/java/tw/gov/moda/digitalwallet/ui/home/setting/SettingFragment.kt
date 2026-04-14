package tw.gov.moda.digitalwallet.ui.home.setting

import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.extension.showNetworkErrorDialog
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.dialog.MyDialog
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentSettingBinding


/**
 * 皮夾設定頁
 */
@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>() {

    companion object {
        fun newInstance() = SettingFragment()

        private const val DIALOG_LOGOUT = "DIALOG_LOGOUT"
    }

    override fun initViewBinding(container: ViewGroup?): FragmentSettingBinding = FragmentSettingBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: SettingViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun initView() {
        super.initView()
        binding.layWalletSetting.setOnClickListener {
            mActivityViewModel.pageController.launchWalletSettingFragment()
        }

        binding.layProblemCenter.setOnClickListener {
            mActivityViewModel.pageController.launchQuestionCenterFragment()
        }

        binding.layLogOut.setOnClickListener {
            activity?.takeIf { !it.isFinishing }?.apply {
                MyDialog.Builder(this)
                    .setTitle(R.string.log_out)
                    .setMessage(getString(R.string.msg_log_out))
                    .setLeftButtonText(R.string.cancel)
                    .setRightButtonText(R.string.confirm) {
                        mActivityViewModel.pageController.logout()
                    }
                    .show(parentFragmentManager, DIALOG_LOGOUT)
            }
        }

        binding.layDigitalWalletImageWebsite.setOnClickListener {
            mViewModel.openURL(AppConstants.Net.IMAGE_WEB_URL)
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.versionName.observe(viewLifecycleOwner) { text ->
            binding.tvVersionName.text = text
        }
        mViewModel.launchWebViewFragment.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                mActivityViewModel.pageController.launchWebViewFragment()
            }
        }
        mViewModel.showNetworkErrorAlert.observe(viewLifecycleOwner) { isShow ->
            activity?.apply {
                if (isShow && !isFinishing) {
                    showNetworkErrorDialog(supportFragmentManager) {
                        mViewModel.dismissNetworkErrorDialog()
                    }
                }
            }
        }
    }


}