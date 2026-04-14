package tw.gov.moda.digitalwallet.ui.main.controller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DeeplinkController {
    private val mPublisher = Publisher()
    private val mObserver = Observer(mPublisher)

    class Publisher {
        val mAlertParsingDeeplinkError = MutableLiveData<Boolean>()
        val mAlertEmptyVerifiableCredential = MutableLiveData<Boolean>()
        val mLaunchVerifiablePresentationFragment = MutableLiveData<Boolean>()
        val mAddVerifiableCredentialSuccessful = MutableLiveData<String?>()
        val mAddVerifiableCredentialSuccessfulFullPage = MutableLiveData<String?>()
        val mLaunchWebBrowser = MutableLiveData<String>()
        val mProgressBar = MutableLiveData<Boolean>()
        val mLaunchWebViewFragment = MutableLiveData<Boolean>()
        val mAlertOpenBrowser = MutableLiveData<String?>()
    }

    class Observer(private val publisher: Publisher) {
        val alertParsingDeeplinkError: LiveData<Boolean> get() = publisher.mAlertParsingDeeplinkError
        val alertEmptyVerifiableCredential: LiveData<Boolean> get() = publisher.mAlertEmptyVerifiableCredential
        val launchVerifiablePresentationFragment: LiveData<Boolean> get() = publisher.mLaunchVerifiablePresentationFragment
        val addVerifiableCredentialSuccessful: LiveData<String?> get() = publisher.mAddVerifiableCredentialSuccessful
        val addVerifiableCredentialSuccessfulFullPage: LiveData<String?> get() = publisher.mAddVerifiableCredentialSuccessfulFullPage
        val launchWebBrowser: LiveData<String> get() = publisher.mLaunchWebBrowser
        val progressBar: LiveData<Boolean> get() = publisher.mProgressBar
        val launchWebViewFragment: LiveData<Boolean> get() = publisher.mLaunchWebViewFragment
        val alertOpenBrowser: LiveData<String?> get() = publisher.mAlertOpenBrowser
    }

    fun getLiveData(): Observer {
        return mObserver
    }

    fun alertParsingDeeplinkError(isShow: Boolean = true) {
        mPublisher.mAlertParsingDeeplinkError.postValue(isShow)
    }

    fun alertEmptyVerifiableCredential(isShow: Boolean = true) {
        mPublisher.mAlertEmptyVerifiableCredential.postValue(isShow)
    }

    fun launchVerifiablePresentationFragment(isShow: Boolean = true) {
        mPublisher.mLaunchVerifiablePresentationFragment.postValue(isShow)
    }

    fun addVerifiableCredentialSuccessful(text: String? = null) {
        mPublisher.mAddVerifiableCredentialSuccessful.postValue(text)
    }

    fun addVerifiableCredentialSuccessfulFullPage(text: String? = null) {
        mPublisher.mAddVerifiableCredentialSuccessfulFullPage.postValue(text)
    }

    fun progressBar(isShow: Boolean = true) {
        mPublisher.mProgressBar.postValue(isShow)
    }

    fun launchWebBrowser(url: String) {
        mPublisher.mLaunchWebBrowser.postValue(url)
    }

    fun launchWebViewFragment(isShow: Boolean = true) {
        mPublisher.mLaunchWebViewFragment.postValue(isShow)
    }

    fun alertOpenBrowser(text: String? = null) {
        mPublisher.mAlertOpenBrowser.postValue(text)
    }

}