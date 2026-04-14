package tw.gov.moda.digitalwallet.ui.webview

import android.content.Intent
import android.net.http.SslError
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.extension.showNetworkErrorDialog
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.BuildConfig
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentQuestionWebViewBinding


/**
 * WebView
 */
@AndroidEntryPoint
class WebViewFragment : BaseFragment<FragmentQuestionWebViewBinding>() {

    companion object {
        fun newInstance() = WebViewFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentQuestionWebViewBinding = FragmentQuestionWebViewBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: WebViewViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        binding.imgLeft.setOnClickListener {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            } else {
                mActivityViewModel.pageController.popBackStack()
            }
        }
        binding.imgRight.setOnClickListener {
            mActivityViewModel.pageController.popBackStack()
        }


        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (request?.url?.scheme?.startsWith("http") == true) {
                    return false
                } else if (request != null) {
                    activity?.apply {
                        val intent = Intent(Intent.ACTION_VIEW, request.url)
                        startActivity(intent)
                        return true
                    }
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (this@WebViewFragment.isVisible && activity?.isFinishing == false && mProgressDialogWhite?.isShowing == true) {
                    mProgressDialogWhite?.dismiss()
                }
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                showWebErrorMessage()
                super.onReceivedSslError(view, handler, error)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                showWebErrorMessage()
                super.onReceivedError(view, request, error)
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                if (request?.url != null && request.url.toString() == mViewModel.webViewURL.value) {
                    showWebErrorMessage()
                }
                super.onReceivedHttpError(view, request, errorResponse)
            }
        }

        binding.webView.settings.apply {
            javaScriptEnabled = true
            userAgentString = "ModaDigitalWalletApp/" + BuildConfig.VERSION_NAME.substringBefore("-") + "Android"
        }

        context?.apply {
            binding.webView.addJavascriptInterface(WebViewInterface(), AppConstants.Net.SCRIPT_NAME)
        }

        if (this@WebViewFragment.isVisible && activity?.isFinishing == false) {
            mProgressDialogWhite?.show()
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.webViewURL.observe(viewLifecycleOwner) { url ->
            binding.webView.loadUrl(url)
            binding.tvUrl.text = url
        }
        mViewModel.title.observe(viewLifecycleOwner) { title ->
            binding.layToolbar.findViewById<TextView>(R.id.tv_title).text = title
        }
        mViewModel.showUrl.observe(viewLifecycleOwner) { isShow ->
            binding.tvUrl.isVisible = isShow
        }
        mViewModel.switchLeftIcon.observe(viewLifecycleOwner) { isSwitch ->
            if (isSwitch) {
                binding.imgLeft.setImageResource(R.drawable.ic_arrow2_down_default)
                binding.imgRight.visibility = View.VISIBLE
            } else {
                binding.imgLeft.setImageResource(R.drawable.ic_arrow_left_2_fill)
                binding.imgRight.visibility = View.INVISIBLE
            }
        }
    }

    private fun showWebErrorMessage() {
        activity?.apply {
            if (!isFinishing) {
                showNetworkErrorDialog(supportFragmentManager, action = {
                    mActivityViewModel.pageController.popBackStack()
                })
            }
        }
    }

    inner class WebViewInterface {
        @JavascriptInterface
        fun postMessage(message: String) {
            mViewModel.parseQRCode(message)
        }
    }

}