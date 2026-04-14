package tw.gov.moda.digitalwallet.ui.create.contract.detail

import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.dialog.MyDialog
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentContractDetailBinding


/**
 * 規約條款內容頁
 */
@AndroidEntryPoint
class ContractDetailFragment : BaseFragment<FragmentContractDetailBinding>() {

    companion object {
        fun newInstance() = ContractDetailFragment()

        private const val DIALOG_WEB_ERROR = "DIALOG_WEB_ERROR"
    }

    override fun initViewBinding(container: ViewGroup?): FragmentContractDetailBinding = FragmentContractDetailBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: ContractDetailViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)

        binding.btnConfirm.setOnClickListener {
            mViewModel.confirmForAgrees(true)
            mActivityViewModel.pageController.popBackStack()
        }


        binding.btnCancel.setOnClickListener {
            mViewModel.confirmForAgrees(false)
            mActivityViewModel.pageController.popBackStack()
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (this@ContractDetailFragment.isVisible && activity?.isFinishing == false && mProgressDialogWhite?.isShowing == true) {
                    mProgressDialogWhite?.dismiss()
                }
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                if (request?.url != null && request.url.toString() == mViewModel.content.value) {
                    showWebErrorMessage()
                }
                super.onReceivedHttpError(view, request, errorResponse)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                showWebErrorMessage()
                super.onReceivedError(view, request, error)
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                showWebErrorMessage()
                super.onReceivedSslError(view, handler, error)
            }

        }


    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.title.observe(viewLifecycleOwner) { title ->
            binding.tvContractTitle.text = title
            setToolbar(title, { imageView ->
                imageView.isVisible = true
                imageView.setImageResource(R.drawable.ic_arrow_left_2_fill)
                imageView.setOnClickListener {
                    mActivityViewModel.pageController.popBackStack()
                }
            })
        }
        mViewModel.content.observe(viewLifecycleOwner) { contentURL ->
            if (contentURL.contains("file:///android_asset/")){
                context?.apply {
                    val htmlContent = assets.open(contentURL.replace("file:///android_asset/", "")).bufferedReader().use { it.readText() }
                    // 載入 HTML 內容（baseUrl 指定為 assets 以便載入相對路徑資源）
                    binding.webView.loadDataWithBaseURL(
                        "file:///android_asset/",
                        htmlContent,
                        "text/html",
                        "utf-8",
                        null
                    )
                }
            }else{
                binding.webView.loadUrl(contentURL)
            }
        }
    }

    private fun showWebErrorMessage() {
        activity?.apply {
            if (!isFinishing) {
                MyDialog.Builder(this)
                    .setTitle(getString(R.string.connection_error))
                    .setMessage(getString(R.string.connection_error_message))
                    .setRightButtonText(getString(R.string.confirm)) {
                        mActivityViewModel.pageController.popBackStack()
                    }
                    .show(supportFragmentManager, DIALOG_WEB_ERROR)
            }
        }
    }
}