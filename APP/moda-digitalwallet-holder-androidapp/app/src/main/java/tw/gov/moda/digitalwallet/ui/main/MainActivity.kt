package tw.gov.moda.digitalwallet.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.moda.pkcsnfckit.TokenSDKManager
import dagger.hilt.android.AndroidEntryPoint
import io.flutter.embedding.engine.FlutterEngine
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import tw.gov.moda.digitalwallet.AppApplication
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.exception.SDKException
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.extension.showMessageDialog
import tw.gov.moda.digitalwallet.extension.showSDKMessageDialog
import tw.gov.moda.digitalwallet.ui.barcode.BarcodeScaleFragment
import tw.gov.moda.digitalwallet.ui.base.BaseActivity
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.create.contract.ContractFragment
import tw.gov.moda.digitalwallet.ui.create.contract.detail.ContractDetailFragment
import tw.gov.moda.digitalwallet.ui.create.guideline.GuidelineFragment
import tw.gov.moda.digitalwallet.ui.create.wallet.explanation.CreateWalletExplanationFragment
import tw.gov.moda.digitalwallet.ui.create.wallet.level.CreateWalletLevelFragment
import tw.gov.moda.digitalwallet.ui.create.wallet.name.CreateWalletNameFragment
import tw.gov.moda.digitalwallet.ui.create.wallet.pincode.CreatePinCodeFragment
import tw.gov.moda.digitalwallet.ui.create.welcome.WelcomeFragment
import tw.gov.moda.digitalwallet.ui.dialog.MyDialog
import tw.gov.moda.digitalwallet.ui.home.HomeFragment
import tw.gov.moda.digitalwallet.ui.home.barcode.result.ShowCredentialBarcodeFragment
import tw.gov.moda.digitalwallet.ui.home.scan.ScanFragment
import tw.gov.moda.digitalwallet.ui.home.setting.change.name.ChangeWalletNameFragment
import tw.gov.moda.digitalwallet.ui.home.setting.change.pincode.ChangePinCodeFragment
import tw.gov.moda.digitalwallet.ui.home.setting.contact.ContactCustomerServiceFragment
import tw.gov.moda.digitalwallet.ui.home.setting.delete.DeleteWalletResultFragment
import tw.gov.moda.digitalwallet.ui.home.setting.question.QuestionCenterFragment
import tw.gov.moda.digitalwallet.ui.home.setting.wallet.WalletSettingFragment
import tw.gov.moda.digitalwallet.ui.home.wallet.WalletFragment
import tw.gov.moda.digitalwallet.ui.home.wallet.information.CardInformationFragment
import tw.gov.moda.digitalwallet.ui.home.wallet.record.CardRecordFragment
import tw.gov.moda.digitalwallet.ui.link.AppLinkActivity
import tw.gov.moda.digitalwallet.ui.login.LoginFragment
import tw.gov.moda.digitalwallet.ui.login.pincode.LoginPinCodeFragment
import tw.gov.moda.digitalwallet.ui.record.OperateRecordFragment
import tw.gov.moda.digitalwallet.ui.splash.SplashFragment
import tw.gov.moda.digitalwallet.ui.verifiable.authorized.AuthorizedCredentialFragment
import tw.gov.moda.digitalwallet.ui.verifiable.credential.ApplyCompletedBottomSheetDialogFragment
import tw.gov.moda.digitalwallet.ui.verifiable.credential.ApplyResultFragment
import tw.gov.moda.digitalwallet.ui.verifiable.presentation.VerifiablePresentationFragment
import tw.gov.moda.digitalwallet.ui.verifiable.presentation.change.ChangeCredentialFragment
import tw.gov.moda.digitalwallet.ui.verifiable.presentation.contract.VerifiablePresentationContractFragment
import tw.gov.moda.digitalwallet.ui.verifiable.result.VerificationResultFragment
import tw.gov.moda.digitalwallet.ui.webview.WebViewFragment
import tw.gov.moda.diw.BuildConfig
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.ActivityMainBinding
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    companion object {
        private const val DIALOG_BIOMETRIC_LOCKED = "DIALOG_BIOMETRIC_LOCKED"
        private const val DIALOG_AUTO_LOGOUT = "DIALOG_AUTO_LOGOUT"
        private const val DIALOG_BIOMETRIC_HAS_CHANGED = "DIALOG_BIOMETRIC_HAS_CHANGED"
        private const val DIALOG_OPEN_BROWSER = "DIALOG_OPEN_BROWSER"
    }

    @Inject
    lateinit var mFlutterEngine: FlutterEngine

    @Inject
    lateinit var mPref: ModaSharedPreferences

    val mViewModel: MainViewModel by viewModels()

    override fun initViewBinding(): ActivityMainBinding =
        ActivityMainBinding.inflate(layoutInflater)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mLaunchIntentDeviceSecure = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), { result ->
        if (result.resultCode != Activity.RESULT_CANCELED) {
            mViewModel.setIntentDeviceSecureResult(result.resultCode == RESULT_OK)
        }
    })

    private var mTokenSDKManager: TokenSDKManager? = null


    private fun initOnBackPressedCallback() = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            mViewModel.pageController.popBackStack()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        enableEdgeToEdge()
        onBackPressedDispatcher.addCallback(this, initOnBackPressedCallback())
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { _, insets ->
            mViewModel.updateInsets(insets)
            insets
        }

        // 建立NFC與Flutter 的 Channel
        mTokenSDKManager = TokenSDKManager(mFlutterEngine, this)

        // 檢查Google Play Services
        detectGooglePlayServices()

        try {
            ProviderInstaller.installIfNeeded(this)
        } catch (e: GooglePlayServicesRepairableException) {
            GoogleApiAvailability.getInstance()
                .showErrorNotification(this, e.connectionStatusCode)

        } catch (e: GooglePlayServicesNotAvailableException) {
            // Google Play Services 不可修復
            showMessageDialog(supportFragmentManager, getString(R.string.msg_can_not_update_google_play_services)) {
                finish()
            }
        }


        mViewModel.pageController.launchSplashFragment()
        detectIntentData(intent)

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        detectIntentData(intent)
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.alertAutoLogout.observe(this) { isAlert ->
            if (isAlert && !isFinishing) {
                if (supportFragmentManager.findFragmentByTag(DIALOG_AUTO_LOGOUT) == null) {
                    MyDialog.Builder(this)
                        .setTitle(getString(R.string.whether_continue_using))
                        .setMessage(getString(R.string.message_over_setting_logout_time))
                        .setRightButtonText(getString(R.string.continue_using)) {
                            mViewModel.continueUsing()
                        }
                        .setLeftButtonText(getString(R.string.logout_wallet)) {
                            mViewModel.setLoginStatus(false)
                            mViewModel.pageController.logout()
                        }
                        .show(supportFragmentManager, DIALOG_AUTO_LOGOUT)
                }
            }
        }
        mViewModel.alertBiometricLocked.observe(this) { isAlert ->
            if (isAlert && !isFinishing) {
                MyDialog.Builder(this)
                    .setTitle(R.string.security_warning)
                    .setMessage(R.string.msg_biometric_locked_content)
                    .setRightButtonText(R.string.confirm)
                    .show(supportFragmentManager, DIALOG_BIOMETRIC_LOCKED)
            }
        }
        mViewModel.alertBiometricHasChanged.observe(this) { isShow ->
            if (isShow && !isFinishing) {
                MyDialog.Builder(this)
                    .setTitle(R.string.security_warning)
                    .setMessage(R.string.msg_biometric_has_been_new_biometric)
                    .setRightButtonText(R.string.confirm)
                    .show(supportFragmentManager, DIALOG_BIOMETRIC_HAS_CHANGED)
            }
        }
        mViewModel.exception.observe(this) { exception ->
            if (exception is SDKException) {
                // SDK 錯誤
                showMessageDialog(supportFragmentManager, exception.msg, getExceptionAlertAction())
            } else {
                // 其他
                val msg = getString(R.string.msg_another_exception)
                showMessageDialog(supportFragmentManager, msg, getExceptionAlertAction())
            }
        }
        mViewModel.alertSDKMessage.observe(this) { message ->
            if (message != null && message.isNotBlank()) {
                mProgressDialog.dismiss()
                showSDKMessageDialog(supportFragmentManager, message, getAlertMessageAction())
            }
        }
        mViewModel.intentDeviceSecure.observe(this) { isShow ->
            if (isShow) {
                val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                val intent = keyguardManager.createConfirmDeviceCredentialIntent(getString(R.string.app_name), null)
                mLaunchIntentDeviceSecure.launch(intent)
                mViewModel.authenticateIntentDeviceSecure(false)
            }
        }
        mViewModel.pageController.getLiveData().popBackStack.observe(this) { fragmentTagList ->
            popFragment(fragmentTagList)
        }
        mViewModel.pageController.getLiveData().popToFirstPage.observe(this) { isPopToFirstFragment ->
            if (isPopToFirstFragment) {
                popToFirstFragment()
            }
        }
        mViewModel.pageController.getLiveData().launchWelcomeFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = WelcomeFragment.newInstance()
                changeFragment(fragment, WelcomeFragment::class.hashTag())
                mViewModel.pageController.launchWelcomeFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchContractFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = ContractFragment.newInstance()
                changeFragment(fragment, ContractFragment::class.hashTag())
                mViewModel.pageController.launchContractFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchContractDetailFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = ContractDetailFragment.newInstance()
                changeFragment(fragment, ContractDetailFragment::class.hashTag())
                mViewModel.pageController.launchContractDetailFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchGuidelineFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = GuidelineFragment.newInstance()
                changeFragment(fragment, GuidelineFragment::class.hashTag())
                mViewModel.pageController.launchGuidelineFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchCreateWalletExplanationFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = CreateWalletExplanationFragment.newInstance()
                changeFragment(fragment, CreateWalletExplanationFragment::class.hashTag())
                mViewModel.pageController.launchCreateWalletExplanationFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchCreatePinCodeFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = CreatePinCodeFragment.newInstance()
                changeFragment(fragment, CreatePinCodeFragment::class.hashTag())
                mViewModel.pageController.launchCreatePinCodeFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchCreateWalletNameFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = CreateWalletNameFragment.newInstance()
                changeFragment(fragment, CreateWalletNameFragment::class.hashTag())
                mViewModel.pageController.launchCreateWalletNameFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchCreateWalletLevelFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = CreateWalletLevelFragment.newInstance()
                changeFragment(fragment, CreateWalletLevelFragment::class.hashTag())
                mViewModel.pageController.launchCreateWalletLevelFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchHomeFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = HomeFragment.newInstance()
                changeFragment(fragment, HomeFragment::class.hashTag())
                mViewModel.pageController.launchHomeFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchLoginFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = LoginFragment.newInstance()
                changeFragment(fragment, LoginFragment::class.hashTag())
                mViewModel.pageController.launchLoginFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchLoginPinCodeFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = LoginPinCodeFragment.newInstance()
                changeFragment(fragment, LoginPinCodeFragment::class.hashTag())
                mViewModel.pageController.launchLoginPinCodeFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchCardInformationFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = CardInformationFragment.newInstance()
                changeFragment(fragment, CardInformationFragment::class.hashTag())
                mViewModel.pageController.launchCardInformationFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchCardRecordFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = CardRecordFragment.newInstance()
                changeFragment(fragment, CardRecordFragment::class.hashTag())
                mViewModel.pageController.launchCardRecordFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchVerifiablePresentationFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = VerifiablePresentationFragment.newInstance()
                changeFragment(fragment, VerifiablePresentationFragment::class.hashTag())
                mViewModel.pageController.launchVerifiablePresentationFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchAuthorizedCredentialFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = AuthorizedCredentialFragment.newInstance()
                changeFragment(fragment, AuthorizedCredentialFragment::class.hashTag())
                mViewModel.pageController.launchAuthorizedCredentialFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchSplashFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = SplashFragment.newInstance()
                changeFragment(fragment, SplashFragment::class.hashTag())
                mViewModel.pageController.launchSplashFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchWalletFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = WalletFragment.newInstance()
                changeFragment(fragment, WalletFragment::class.hashTag())
                mViewModel.pageController.launchWalletFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchVerificationResultFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = VerificationResultFragment.newInstance()
                changeFragment(fragment, VerificationResultFragment::class.hashTag())
                mViewModel.pageController.launchVerificationResultFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchShowCredentialBarCodeFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = ShowCredentialBarcodeFragment.newInstance()
                changeFragment(fragment, ShowCredentialBarcodeFragment::class.hashTag())
                mViewModel.pageController.launchShowCredentialBarCodeFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchWebViewFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = WebViewFragment.newInstance()
                changeFragment(fragment, WebViewFragment::class.hashTag())
                mViewModel.pageController.launchWebViewFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchContactCustomerServiceFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = ContactCustomerServiceFragment.newInstance()
                changeFragment(fragment, ContactCustomerServiceFragment::class.hashTag())
                mViewModel.pageController.launchContactCustomerServiceFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchQuestionCenterFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = QuestionCenterFragment.newInstance()
                changeFragment(fragment, QuestionCenterFragment::class.hashTag())
                mViewModel.pageController.launchQuestionCenterFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchChangePinCodeFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = ChangePinCodeFragment.newInstance()
                changeFragment(fragment, ChangePinCodeFragment::class.hashTag())
                mViewModel.pageController.launchChangePinCodeFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchChangeWalletNameFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = ChangeWalletNameFragment.newInstance()
                changeFragment(fragment, ChangeWalletNameFragment::class.hashTag())
                mViewModel.pageController.launchChangeWalletNameFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchDeleteWalletResultFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = DeleteWalletResultFragment.newInstance()
                changeFragment(fragment, DeleteWalletResultFragment::class.hashTag())
                mViewModel.pageController.launchDeleteWalletResultFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchWalletSettingFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = WalletSettingFragment.newInstance()
                changeFragment(fragment, WalletSettingFragment::class.hashTag())
                mViewModel.pageController.launchWalletSettingFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchAppLink.observe(this) { isShow ->
            if (isShow) {
                val fragment = HomeFragment.newInstance()
                changeFragment(fragment, HomeFragment::class.hashTag(), true)
                mViewModel.applink(null)
            }
        }
        mViewModel.pageController.getLiveData().launchScanFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = ScanFragment.newInstance()
                changeFragment(fragment, ScanFragment::class.hashTag())
                mViewModel.pageController.launchScanFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().launchChangeCredentialFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = ChangeCredentialFragment.newInstance()
                changeFragment(fragment, ChangeCredentialFragment::class.hashTag())
                mViewModel.pageController.launchChangeCredentialFragment(false)
            }
        }
        mViewModel.deeplinkController.getLiveData().addVerifiableCredentialSuccessfulFullPage.observe(this) { name ->
            if (name != null) {
                mProgressDialog.dismiss()
                val fragment = ApplyResultFragment.newInstance()
                changeFragment(fragment, ApplyResultFragment::class.hashTag())
            }
            mViewModel.deeplinkController.addVerifiableCredentialSuccessfulFullPage(null)
        }
        mViewModel.pageController.getLiveData().launchOperateRecordFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = OperateRecordFragment.newInstance()
                changeFragment(fragment, OperateRecordFragment::class.hashTag())
            }
            mViewModel.pageController.launchOperateRecordFragment(false)
        }
        mViewModel.pageController.getLiveData().launchVerifiablePresentationContractFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = VerifiablePresentationContractFragment.newInstance()
                changeFragment(fragment, VerifiablePresentationContractFragment::class.hashTag())
            }
            mViewModel.pageController.launchVerifiablePresentationContractFragment(false)
        }
        mViewModel.pageController.getLiveData().launchBarcodeScaleFragment.observe(this) { isShow ->
            if (isShow) {
                val fragment = BarcodeScaleFragment.newInstance()
                changeFragment(fragment, BarcodeScaleFragment::class.hashTag())
                mViewModel.pageController.launchBarcodeScaleFragment(false)
            }
        }
        mViewModel.pageController.getLiveData().logout.observe(this) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            val fragmentList: List<Fragment> = supportFragmentManager.fragments
            for (existFragment in fragmentList) {
                transaction.remove(existFragment)
            }
            transaction.commit()
            mViewModel.pageController.launchSplashFragment()
        }
        mViewModel.deeplinkController.getLiveData().addVerifiableCredentialSuccessful.observe(this) { name ->
            if (name != null) {
                this.apply {
                    if (name.isNotBlank() && !isFinishing) {
                        ApplyCompletedBottomSheetDialogFragment.newInstance()
                            .show(supportFragmentManager, ApplyCompletedBottomSheetDialogFragment::class.hashTag())
                    }
                }
                mViewModel.deeplinkController.addVerifiableCredentialSuccessful(null)
            }
        }
        mViewModel.deeplinkController.getLiveData().progressBar.observe(this) { isShow ->
            if (isShow && this.isFinishing.not()) {
                mProgressDialog.show()
            } else {
                mProgressDialog.dismiss()
            }
        }
        mViewModel.deeplinkController.getLiveData().launchVerifiablePresentationFragment.observe(this) { isShow ->
            if (isShow) {
                mViewModel.pageController.launchVerifiablePresentationFragment()
                mViewModel.deeplinkController.launchVerifiablePresentationFragment(false)
            }
        }
        mViewModel.deeplinkController.getLiveData().launchWebViewFragment.observe(this) { isShow ->
            if (isShow) {
                mViewModel.pageController.launchWebViewFragment(true)
                mViewModel.deeplinkController.launchWebViewFragment(false)
            }
        }
        mViewModel.deeplinkController.getLiveData().alertOpenBrowser.observe(this) { name ->
            if (!name.isNullOrBlank() && !isFinishing) {
                MyDialog.Builder(this)
                    .setTitle(getString(R.string.reminder_message))
                    .setMessage(String.format(getString(R.string.you_will_leave_your_digital_voucher_wallet), name))
                    .setRightButtonText(getString(R.string.confirm)) {
                        mViewModel.deeplinkController.launchWebBrowser(mViewModel.getWebViewUrl())
                    }
                    .setLeftButtonText(R.string.cancel)
                    .show(supportFragmentManager, DIALOG_OPEN_BROWSER)
                mViewModel.deeplinkController.alertOpenBrowser(null)
            }
        }
        mViewModel.deeplinkController.getLiveData().alertParsingDeeplinkError.observe(this) { isShow ->
            if (isShow && !isFinishing) {
                mProgressDialog.dismiss()
                showMessageDialog(supportFragmentManager, getString(R.string.msg_qrcode_parsing_failed)) {
                    mViewModel.setScanEnable(true)
                }
                mViewModel.deeplinkController.alertParsingDeeplinkError(false)
            }
        }
        mViewModel.deeplinkController.getLiveData().alertEmptyVerifiableCredential.observe(this) { isShow ->
            if (isShow && !isFinishing) {
                mProgressDialog.dismiss()
                showMessageDialog(supportFragmentManager, getString(R.string.msg_qrcode_parsing_failed_by_vc)) {
                    mViewModel.setScanEnable(true)
                }
                mViewModel.deeplinkController.alertParsingDeeplinkError(false)
            }
        }
        mViewModel.deeplinkController.getLiveData().launchWebBrowser.observe(this) { url ->
            if (url.isNotEmpty()) {
                mViewModel.setScanEnable(true)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }
    }


    private fun changeFragment(fragment: Fragment, tag: String, isReload: Boolean = false) {
        switchLightStatusBar(fragment)
        if (isReload) {
            supportFragmentManager.findFragmentByTag(tag)?.also { existFragment ->
                supportFragmentManager.beginTransaction()
                    .hide(existFragment)
                    .commit()
            }
        }

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        val fragmentList: List<Fragment> = supportFragmentManager.fragments
        for (existFragment in fragmentList) {
            transaction.hide(existFragment)
        }
        val existFragment: Fragment? = supportFragmentManager.findFragmentByTag(tag)
        if (existFragment != null) {
            transaction.show(existFragment)
        } else {
            transaction.add(binding.root.getId(), fragment, tag)
        }
        transaction.commit()
    }

    private fun detectGooglePlayServices() {
        MainScope().launch {
            try {
                ProviderInstaller.installIfNeededAsync(
                    this@MainActivity,
                    object : ProviderInstaller.ProviderInstallListener {
                        override fun onProviderInstallFailed(
                            errorCode: Int,
                            recoveryIntent: Intent?
                        ) {
                            if (GoogleApiAvailability.getInstance()
                                    .isUserResolvableError(errorCode)
                            ) {
                                // Google Play Services 可更新
                                showMessageDialog(supportFragmentManager, getString(R.string.msg_need_to_update_google_play_services)) {

                                }
                            } else {
                                // Google Play Services 不可修復
                                showMessageDialog(supportFragmentManager, getString(R.string.msg_can_not_update_google_play_services)) {
                                    finish()
                                }
                            }
                        }

                        override fun onProviderInstalled() {
                        }
                    })
            } catch (e: Exception) {
                // Google Play Services 不可修復
                showMessageDialog(supportFragmentManager, getString(R.string.msg_can_not_update_google_play_services)) {
                    finish()
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
    }

    private fun popToFirstFragment() {
        supportFragmentManager.fragments.firstOrNull()?.also {
            val fragmentTag = it::class.hashTag()
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            supportFragmentManager.fragments.forEach { popFragment ->
                if (popFragment != null && popFragment::class.hashTag() != fragmentTag) {
                    transaction.remove(popFragment)
                }
            }
            switchLightStatusBar(it)
            transaction.show(it)
            transaction.commit()
        }
    }

    private fun popFragment(fragmentTagList: List<String>?) {
        Log.d(this::class.simpleName, "popFragment: $fragmentTagList")
        if (fragmentTagList != null) {
            fragmentTagList.map { supportFragmentManager.findFragmentByTag(it) }.also { fragments ->

                val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                fragments.forEach { popFragment ->
                    if (popFragment != null) {
                        transaction.remove(popFragment)
                    }
                }
                val lastFragment = supportFragmentManager.fragments.lastOrNull { !fragmentTagList.contains(it::class.hashTag()) }
                if (lastFragment != null) {
                    switchLightStatusBar(lastFragment)
                    transaction.show(lastFragment)
                    transaction.commit()
                } else {
                    transaction.commit()
                    mViewModel.pageController.logout()
                    finish()
                }
            }
        } else {
            val lastFragment = supportFragmentManager.fragments.lastOrNull()
            if (lastFragment != null) {
                popFragment(listOf(lastFragment::class.hashTag()))
            } else {
                mViewModel.pageController.logout()
                finish()
            }
        }
    }

    private fun detectIntentData(intentData: Intent) {
        // 判斷 Intent 資料
        when (intentData.action) {
            AppLinkActivity.INTENT_ACTION -> {
                mViewModel.applink(intentData.data)
            }
        }
    }

    private fun switchLightStatusBar(fragment: Fragment) {
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = fragment !is ScanFragment
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = fragment !is ScanFragment
    }

    private fun getExceptionAlertAction(): (() -> Unit)? {
        return {
            mViewModel.setLoginStatus(false)
            finish()
        }
    }

    private fun getAlertMessageAction(): (() -> Unit)? {
        return null
    }
}