package tw.gov.moda.digitalwallet.ui.main.controller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PageController {

    private val mPublisher = Publisher()
    private val mObserver = Observer(mPublisher)

    class Publisher {
        val mPopBackStack = MutableLiveData<List<String>?>()
        val mPopToFirstPage = MutableLiveData<Boolean>()
        val mLaunchWelcomeFragment = MutableLiveData<Boolean>()
        val mLaunchContractFragment = MutableLiveData<Boolean>()
        val mLaunchContractDetailFragment = MutableLiveData<Boolean>()
        val mLaunchGuidelineFragment = MutableLiveData<Boolean>()
        val mLaunchCreateWalletExplanationFragment = MutableLiveData<Boolean>()
        val mLaunchCreatePinCodeFragment = MutableLiveData<Boolean>()
        val mLaunchCreateWalletNameFragment = MutableLiveData<Boolean>()
        val mLaunchCreateWalletLevelFragment = MutableLiveData<Boolean>()
        val mLaunchHomeFragment = MutableLiveData<Boolean>()
        val mLaunchLoginFragment = MutableLiveData<Boolean>()
        val mLaunchLoginPinCodeFragment = MutableLiveData<Boolean>()
        val mLaunchVerifiablePresentationFragment = MutableLiveData<Boolean>()
        val mLaunchAuthorizedCredentialFragment = MutableLiveData<Boolean>()
        val mLaunchSplashFragment = MutableLiveData<Boolean>()
        val mLaunchWalletFragment = MutableLiveData<Boolean>()
        val mLaunchVerificationResultFragment = MutableLiveData<Boolean>()
        val mLaunchShowCredentialBarCodeFragment = MutableLiveData<Boolean>()
        val mLaunchWebViewFragment = MutableLiveData<Boolean>()
        val mLaunchQuestionCenterFragment = MutableLiveData<Boolean>()
        val mLaunchContactCustomerServiceFragment = MutableLiveData<Boolean>()
        val mLaunchAppLink = MutableLiveData<Boolean>()
        val mLogout = MutableLiveData<Boolean>()
        val mLaunchCardInformationFragment = MutableLiveData<Boolean>()
        val mLaunchCardRecordFragment = MutableLiveData<Boolean>()
        val mLaunchWalletSettingFragment = MutableLiveData<Boolean>()
        val mLaunchChangePinCodeFragment = MutableLiveData<Boolean>()
        val mLaunchWalletNameFragment = MutableLiveData<Boolean>()
        val mLaunchDeleteWalletResultFragment = MutableLiveData<Boolean>()
        val mLaunchScanFragment = MutableLiveData<Boolean>()
        val mLaunchChangeCredentialFragment = MutableLiveData<Boolean>()
        val mLaunchOperateRecordFragment = MutableLiveData<Boolean>()
        val mLaunchVerifiablePresentationContractFragment = MutableLiveData<Boolean>()
        val mLaunchBarcodeScaleFragment = MutableLiveData<Boolean>()
    }

    class Observer(private val publisher: Publisher) {
        val popBackStack: LiveData<List<String>?> get() = publisher.mPopBackStack
        val popToFirstPage: LiveData<Boolean> get() = publisher.mPopToFirstPage
        val launchWelcomeFragment: LiveData<Boolean> get() = publisher.mLaunchWelcomeFragment
        val launchContractFragment: LiveData<Boolean> get() = publisher.mLaunchContractFragment
        val launchContractDetailFragment: LiveData<Boolean> get() = publisher.mLaunchContractDetailFragment
        val launchGuidelineFragment: LiveData<Boolean> get() = publisher.mLaunchGuidelineFragment
        val launchCreateWalletExplanationFragment: LiveData<Boolean> get() = publisher.mLaunchCreateWalletExplanationFragment
        val launchCreatePinCodeFragment: LiveData<Boolean> get() = publisher.mLaunchCreatePinCodeFragment
        val launchCreateWalletNameFragment: LiveData<Boolean> get() = publisher.mLaunchCreateWalletNameFragment
        val launchCreateWalletLevelFragment: LiveData<Boolean> get() = publisher.mLaunchCreateWalletLevelFragment
        val launchHomeFragment: LiveData<Boolean> get() = publisher.mLaunchHomeFragment
        val launchLoginFragment: LiveData<Boolean> get() = publisher.mLaunchLoginFragment
        val launchLoginPinCodeFragment: LiveData<Boolean> get() = publisher.mLaunchLoginPinCodeFragment
        val launchVerifiablePresentationFragment: LiveData<Boolean> get() = publisher.mLaunchVerifiablePresentationFragment
        val launchAuthorizedCredentialFragment: LiveData<Boolean> get() = publisher.mLaunchAuthorizedCredentialFragment
        val launchSplashFragment: LiveData<Boolean> get() = publisher.mLaunchSplashFragment
        val launchWalletFragment: LiveData<Boolean> get() = publisher.mLaunchWalletFragment
        val launchVerificationResultFragment: LiveData<Boolean> get() = publisher.mLaunchVerificationResultFragment
        val launchShowCredentialBarCodeFragment: LiveData<Boolean> get() = publisher.mLaunchShowCredentialBarCodeFragment
        val launchQuestionCenterFragment: LiveData<Boolean> get() = publisher.mLaunchQuestionCenterFragment
        val launchWebViewFragment: LiveData<Boolean> get() = publisher.mLaunchWebViewFragment
        val launchContactCustomerServiceFragment: LiveData<Boolean> get() = publisher.mLaunchContactCustomerServiceFragment
        val launchAppLink: LiveData<Boolean> get() = publisher.mLaunchAppLink
        val logout: LiveData<Boolean> get() = publisher.mLogout
        val launchCardInformationFragment: LiveData<Boolean> get() = publisher.mLaunchCardInformationFragment
        val launchCardRecordFragment: LiveData<Boolean> get() = publisher.mLaunchCardRecordFragment
        val launchWalletSettingFragment: LiveData<Boolean> get() = publisher.mLaunchWalletSettingFragment
        val launchChangePinCodeFragment: LiveData<Boolean> get() = publisher.mLaunchChangePinCodeFragment
        val launchChangeWalletNameFragment: LiveData<Boolean> get() = publisher.mLaunchWalletNameFragment
        val launchDeleteWalletResultFragment: LiveData<Boolean> get() = publisher.mLaunchDeleteWalletResultFragment
        val launchScanFragment: LiveData<Boolean> get() = publisher.mLaunchScanFragment
        val launchChangeCredentialFragment: LiveData<Boolean> get() = publisher.mLaunchChangeCredentialFragment
        val launchOperateRecordFragment: LiveData<Boolean> get() = publisher.mLaunchOperateRecordFragment
        val launchVerifiablePresentationContractFragment: LiveData<Boolean> get() = publisher.mLaunchVerifiablePresentationContractFragment
        val launchBarcodeScaleFragment: LiveData<Boolean> get() = publisher.mLaunchBarcodeScaleFragment
    }


    fun getLiveData(): Observer {
        return mObserver
    }


    fun launchSplashFragment(isShow: Boolean = true) {
        mPublisher.mLaunchSplashFragment.postValue(isShow)
    }

    fun launchLoginFragment(isShow: Boolean = true) {
        mPublisher.mLaunchLoginFragment.postValue(isShow)
    }

    fun launchLoginPinCodeFragment(isShow: Boolean = true) {
        mPublisher.mLaunchLoginPinCodeFragment.postValue(isShow)
    }

    fun launchCreateWalletExplanationFragment(isShow: Boolean = true) {
        mPublisher.mLaunchCreateWalletExplanationFragment.postValue(isShow)
    }

    fun launchCreatePinCodeFragment(isShow: Boolean = true) {
        mPublisher.mLaunchCreatePinCodeFragment.postValue(isShow)
    }

    fun launchCreateWalletNameFragment(isShow: Boolean = true) {
        mPublisher.mLaunchCreateWalletNameFragment.postValue(isShow)
    }

    fun launchCreateWalletLevelFragment(isShow: Boolean = true) {
        mPublisher.mLaunchCreateWalletLevelFragment.postValue(isShow)
    }

    fun launchWalletFragment(isShow: Boolean = true) {
        mPublisher.mLaunchWalletFragment.postValue(isShow)
    }

    fun launchVerifiablePresentationFragment(isShow: Boolean = true) {
        mPublisher.mLaunchVerifiablePresentationFragment.postValue(isShow)
    }

    fun launchAuthorizedCredentialFragment(isShow: Boolean = true) {
        mPublisher.mLaunchAuthorizedCredentialFragment.postValue(isShow)
    }

    fun launchWelcomeFragment(isShow: Boolean = true) {
        mPublisher.mLaunchWelcomeFragment.postValue(isShow)
    }

    fun launchContractFragment(isShow: Boolean = true) {
        mPublisher.mLaunchContractFragment.postValue(isShow)
    }

    fun launchContractDetailFragment(isShow: Boolean = true) {
        mPublisher.mLaunchContractDetailFragment.postValue(isShow)
    }

    fun launchGuidelineFragment(isShow: Boolean = true) {
        mPublisher.mLaunchGuidelineFragment.postValue(isShow)
    }

    fun launchHomeFragment(isShow: Boolean = true) {
        mPublisher.mLaunchHomeFragment.postValue(isShow)
    }

    fun launchVerificationResultFragment(isShow: Boolean = true) {
        mPublisher.mLaunchVerificationResultFragment.postValue(isShow)
    }

    fun launchShowCredentialBarCodeFragment(isShow: Boolean = true) {
        mPublisher.mLaunchShowCredentialBarCodeFragment.postValue(isShow)
    }

    fun launchCardInformationFragment(isShow: Boolean = true) {
        mPublisher.mLaunchCardInformationFragment.postValue(isShow)
    }

    fun launchCardRecordFragment(isShow: Boolean = true) {
        mPublisher.mLaunchCardRecordFragment.postValue(isShow)
    }

    fun launchWebViewFragment(isShow: Boolean = true) {
        mPublisher.mLaunchWebViewFragment.postValue(isShow)
    }

    fun launchQuestionCenterFragment(isShow: Boolean = true) {
        mPublisher.mLaunchQuestionCenterFragment.postValue(isShow)
    }

    fun launchContactCustomerServiceFragment(isShow: Boolean = true) {
        mPublisher.mLaunchContactCustomerServiceFragment.postValue(isShow)
    }

    fun launchWalletSettingFragment(isShow: Boolean = true) {
        mPublisher.mLaunchWalletSettingFragment.postValue(isShow)
    }

    fun launchChangePinCodeFragment(isShow: Boolean = true) {
        mPublisher.mLaunchChangePinCodeFragment.postValue(isShow)
    }

    fun launchChangeWalletNameFragment(isShow: Boolean = true) {
        mPublisher.mLaunchWalletNameFragment.postValue(isShow)
    }

    fun launchDeleteWalletResultFragment(isShow: Boolean = true) {
        mPublisher.mLaunchDeleteWalletResultFragment.postValue(isShow)
    }

    fun launchScanFragment(isShow: Boolean = true) {
        mPublisher.mLaunchScanFragment.postValue(isShow)
    }

    fun launchChangeCredentialFragment(isShow: Boolean = true) {
        mPublisher.mLaunchChangeCredentialFragment.postValue(isShow)
    }

    fun launchOperateRecordFragment(isShow: Boolean = true) {
        mPublisher.mLaunchOperateRecordFragment.postValue(isShow)
    }

    fun launchVerifiablePresentationContractFragment(isShow: Boolean = true) {
        mPublisher.mLaunchVerifiablePresentationContractFragment.postValue(isShow)
    }

    fun launchBarcodeScaleFragment(isShow: Boolean = true) {
        mPublisher.mLaunchBarcodeScaleFragment.postValue(isShow)
    }

    fun logout() {
        mPublisher.mLogout.postValue(true)
    }


    fun popBackStack(tag: String? = null) {
        if (tag != null) {
            popBackStack(listOf(tag))
        } else {
            mPublisher.mPopBackStack.postValue(null)
        }

    }

    fun popBackStack(tag: List<String>) {
        mPublisher.mPopBackStack.postValue(tag)
    }

    fun popToFirstPage() {
        mPublisher.mPopToFirstPage.postValue(true)
    }

    fun applink(applink: Boolean) {
        mPublisher.mLaunchAppLink.postValue(applink)
    }
}