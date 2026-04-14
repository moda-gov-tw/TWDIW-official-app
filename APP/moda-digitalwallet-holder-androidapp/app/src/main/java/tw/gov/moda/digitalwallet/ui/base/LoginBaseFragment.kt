package tw.gov.moda.digitalwallet.ui.base

import androidx.viewbinding.ViewBinding
import tw.gov.moda.digitalwallet.ui.dialog.MyDialog
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R

abstract class LoginBaseFragment<VB : ViewBinding> : BaseFragment<VB>() {
    companion object {
        private const val DIALOG_DEVICE_SECURE_DISABLED = "DIALOG_DEVICE_SECURE_DISABLED"
    }

    abstract override fun getViewModel(): LoginBaseViewModel?

    abstract fun getActivityViewModel(): MainViewModel?

    override fun subscribeObservers() {
        super.subscribeObservers()
        getActivityViewModel()?.authenticationSuccess?.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful && activity != null) {
                getViewModel()?.also { viewModel ->
                    viewModel.verifySuccessful(viewModel.getVerificationSource())
                    getActivityViewModel()?.setAuthenticationSuccess(false)
                }
            }
        }
        getActivityViewModel()?.authenticationFail?.observe(viewLifecycleOwner) { isFail ->
            if (isFail && activity != null) {
                getViewModel()?.also { viewModel ->
                    viewModel.verifyFail(viewModel.getVerificationSource())
                    getActivityViewModel()?.setAuthenticationFail(false)
                }
            }
        }

        getActivityViewModel()?.pinCodeAuthSuccess?.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                getViewModel()?.also { viewModel ->
                    viewModel.verifySuccessful(viewModel.getVerificationSource())
                    getActivityViewModel()?.setPinCodeAuthSuccess(false)
                }
            }
        }

        getViewModel()?.showDeviceSecure?.observe(viewLifecycleOwner) { wallet ->
            if (wallet != null) {
                activity?.apply {
                    getActivityViewModel()?.authenticateBiometric(this, wallet)
                }
            }
        }


        getViewModel()?.alertDeviceSecureDisabled?.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                activity?.apply {
                    val view = layoutInflater.inflate(R.layout.dialog_device_secure_disabled, null)
                    MyDialog.Builder(this)
                        .setCustomView(view)
                        .setRightButtonText(R.string.confirm)
                        .show(supportFragmentManager, DIALOG_DEVICE_SECURE_DISABLED)
                }
            }
        }

        getViewModel()?.launchLoginPinCodeFragment?.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                activity?.apply {
                    getActivityViewModel()?.pageController?.launchLoginPinCodeFragment()
                }
            }
        }

        getViewModel()?.intentDeviceSecure?.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                activity?.apply {
                    getActivityViewModel()?.authenticateIntentDeviceSecure(true)
                }
            }
        }
    }


}