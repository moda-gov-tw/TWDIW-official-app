package tw.gov.moda.digitalwallet.ui.home.setting.wallet

import android.content.Intent
import android.provider.Settings
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.data.element.VerificationSourceEnum
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.extension.setOnAntiStickClickLisener
import tw.gov.moda.digitalwallet.ui.base.LoginBaseFragment
import tw.gov.moda.digitalwallet.ui.base.LoginBaseViewModel
import tw.gov.moda.digitalwallet.ui.dialog.MyDialog
import tw.gov.moda.digitalwallet.ui.login.pincode.LoginPinCodeFragment
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentWalletSettingBinding


/**
 * 皮夾設定頁
 */
@AndroidEntryPoint
class WalletSettingFragment : LoginBaseFragment<FragmentWalletSettingBinding>() {

    companion object {
        fun newInstance() = WalletSettingFragment()
        private const val DIALOG_DISABLE_AUTO_REFRESH_CARD = "DIALOG_DISABLE_AUTO_REFRESH_CARD"
        private const val DIALOG_TURN_OFF_BIOMETRICS = "DIALOG_TURN_OFF_BIOMETRICS"
        private const val DIALOG_TURN_ON_BIOMETRICS = "DIALOG_TURN_ON_BIOMETRICS"
    }

    override fun initViewBinding(container: ViewGroup?): FragmentWalletSettingBinding = FragmentWalletSettingBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): LoginBaseViewModel? = mViewModel
    override fun getActivityViewModel(): MainViewModel? = mActivityViewModel

    private val mViewModel: WalletSettingViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mViewModel.detectBiometricEnrolled()
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.detectBiometricEnrolled()
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
                LoginPinCodeFragment::class.hashTag()
            )
        )
        binding.layEditWalletPassword.setOnAntiStickClickLisener {
            mViewModel.editWalletPassword()
        }

        binding.imgBiometricSetting.setOnAntiStickClickLisener {
            if (binding.imgBiometricSetting.isSelected) {
                activity?.apply {
                    MyDialog.Builder(this)
                        .setTitle(getString(R.string.biometric_settings))
                        .setMessage(getString(R.string.are_you_sure_you_want_to_turn_off_biometrics))
                        .setRightButtonText(getString(R.string.confirm)) {
                            startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                        }
                        .setLeftButtonText(R.string.cancel)
                        .show(supportFragmentManager, DIALOG_TURN_OFF_BIOMETRICS)
                }
            } else {
                activity?.apply {
                    MyDialog.Builder(this)
                        .setTitle(getString(R.string.biometric_settings))
                        .setMessage(getString(R.string.then_go_to_the_device_settings_page_to_adjust))
                        .setRightButtonText(getString(R.string.confirm)) {
                            startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                        }
                        .setLeftButtonText(R.string.cancel)
                        .show(supportFragmentManager, DIALOG_TURN_ON_BIOMETRICS)
                }
            }
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.showDeviceSecure.observe(viewLifecycleOwner) { wallet ->
            if (wallet != null) {
                activity?.apply {
                    mActivityViewModel.authenticateBiometric(this, wallet)
                }
            }
        }
        mViewModel.deviceSecureEnable.observe(viewLifecycleOwner) { isEnable ->
            binding.imgBiometricSetting.isSelected = isEnable
        }
        mViewModel.alreadyExistPinCode.observe(viewLifecycleOwner) { isExisted ->
            if (isExisted) {
                binding.tvEditWalletPasswordTitle.text = getString(R.string.edit_wallet_password)
            } else {
                binding.tvEditWalletPasswordTitle.text = getString(R.string.setting_wallet_password)
            }
        }
        mViewModel.verificationSuccessful.observe(viewLifecycleOwner) { source ->
            when (source) {
                VerificationSourceEnum.ChangePinCode -> {
                    mActivityViewModel.pageController.launchChangePinCodeFragment()
                }
                else -> {}
            }
        }
        mViewModel.logout.observe(viewLifecycleOwner) { isLogout ->
            if (isLogout) {
                mActivityViewModel.pageController.logout()
            }
        }
    }
}