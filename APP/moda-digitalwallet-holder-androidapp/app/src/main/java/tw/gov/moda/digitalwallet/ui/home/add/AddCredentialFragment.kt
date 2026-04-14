package tw.gov.moda.digitalwallet.ui.home.add

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.core.verifiable.VerifiableManager
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.extension.showNetworkErrorDialog
import tw.gov.moda.digitalwallet.ui.adapter.AddCredentialListAdapter
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.dialog.MyDialog
import tw.gov.moda.digitalwallet.ui.home.HomeFragment
import tw.gov.moda.digitalwallet.ui.home.barcode.search.SearchCredentialListFragment
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentAddCredentialBinding
import javax.inject.Inject

/**
 * 加入憑證頁
 */
@AndroidEntryPoint
class AddCredentialFragment : BaseFragment<FragmentAddCredentialBinding>() {

    companion object {
        fun newInstance() = AddCredentialFragment()
        private const val DIALOG_OPEN_BROWSER = "DIALOG_OPEN_BROWSER"
    }

    override fun initViewBinding(container: ViewGroup?): FragmentAddCredentialBinding = FragmentAddCredentialBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: AddCredentialViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    @Inject
    lateinit var mVerifiableManager: VerifiableManager

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mViewModel.refreshAddCredentialList()
        }
    }

    override fun initView() {
        super.initView()
        mViewModel.refreshAddCredentialList()
        context?.apply {
            binding.rvAddCredential.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.rvAddCredential.adapter = AddCredentialListAdapter(
                mListener = {
                    mViewModel.checkBrowserType(it.type, it.issuerServiceUrl, it.name)
                },
                coroutineScope = viewLifecycleOwner.lifecycleScope,
                verifiableManager = mVerifiableManager
            )
        }
        binding.laySearch.setOnClickListener {
            (parentFragment as? HomeFragment)?.changeFragment(SearchCredentialListFragment.newInstance(), SearchCredentialListFragment::class.hashTag())
        }
        binding.imgScan.setOnClickListener {
            mActivityViewModel.pageController.launchScanFragment()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            mViewModel.getAddCredentialList()
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.addCredentialList.observe(viewLifecycleOwner) { list ->
            binding.swipeRefreshLayout.isRefreshing = false
            (binding.rvAddCredential.adapter as? AddCredentialListAdapter)?.submitList(list)
        }
        mViewModel.showOpenBrowserAlert.observe(viewLifecycleOwner) { name ->
            activity?.apply {
                MyDialog.Builder(this)
                    .setTitle(getString(R.string.reminder_message))
                    .setMessage(String.format(getString(R.string.you_will_leave_your_digital_voucher_wallet), name))
                    .setRightButtonText(getString(R.string.confirm)) {
                        mViewModel.openBrowser()
                    }
                    .setLeftButtonText(R.string.cancel)
                    .show(supportFragmentManager, DIALOG_OPEN_BROWSER)
            }
        }
        mViewModel.openBrowser.observe(viewLifecycleOwner) { url ->
            activity?.takeIf { !it.isFinishing }?.apply {
                startActivity(Intent(Intent.ACTION_VIEW).apply { setData(Uri.parse(url)) })
            }
        }
        mViewModel.openWebView.observe(viewLifecycleOwner) {
            mActivityViewModel.pageController.launchWebViewFragment()
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
        mViewModel.closeSwipeRefreshLayout.observe(viewLifecycleOwner){
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}