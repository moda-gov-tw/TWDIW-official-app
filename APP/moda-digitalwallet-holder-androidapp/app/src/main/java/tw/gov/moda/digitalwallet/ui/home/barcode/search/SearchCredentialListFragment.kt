package tw.gov.moda.digitalwallet.ui.home.barcode.search

import android.content.Intent
import android.net.Uri
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentActivity.INPUT_METHOD_SERVICE
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.core.verifiable.VerifiableManager
import tw.gov.moda.digitalwallet.data.element.PageEnum.AddCredential
import tw.gov.moda.digitalwallet.data.element.PageEnum.ShowCredential
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.ui.adapter.AddCredentialListAdapter
import tw.gov.moda.digitalwallet.ui.adapter.QuickAuthorizationListAdapter
import tw.gov.moda.digitalwallet.ui.adapter.SearchRecordAdapter
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.dialog.MyDialog
import tw.gov.moda.digitalwallet.ui.home.HomeFragment
import tw.gov.moda.digitalwallet.ui.home.add.AddCredentialFragment
import tw.gov.moda.digitalwallet.ui.home.barcode.ShowCredentialFragment
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentSearchCredentialListBinding
import javax.inject.Inject

@AndroidEntryPoint
class SearchCredentialListFragment : BaseFragment<FragmentSearchCredentialListBinding>() {

    companion object {
        fun newInstance() = SearchCredentialListFragment()
        private const val DIALOG_OPEN_BROWSER = "DIALOG_OPEN_BROWSER"
    }

    override fun initViewBinding(container: ViewGroup?): FragmentSearchCredentialListBinding = FragmentSearchCredentialListBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: SearchCredentialListViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    @Inject
    lateinit var mVerifiableManager: VerifiableManager

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            hideSoftKeyboard()
            binding.etSearch.apply {
                text.clear()
                clearFocus()
            }
        } else {
            mViewModel.refreshSearchRecord()
            mViewModel.switchResultAdapter()
        }
    }

    override fun initView() {
        super.initView()
        mViewModel.refreshSearchRecord()
        mViewModel.switchResultAdapter()
        context?.apply {
            binding.rvSearchRecord.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.rvSearchRecord.adapter = SearchRecordAdapter(
                mSearchListener = {
                    binding.etSearch.setText(it.keyword)
                },
                mDeleteRecordListener = {
                    mViewModel.deleteSearchRecord(it)
                }
            )
            binding.rvSearchResult.itemAnimator = null
            binding.rvSearchResult.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            val keyword = text?.toString()?.trim().orEmpty()
            binding.laySearchRecord.isVisible = keyword.isEmpty()
            binding.laySearchResult.isVisible = keyword.isNotEmpty()

            if (keyword.isNotEmpty()) {
                mViewModel.searchResult(keyword)
            } else {
                binding.layNoFoundResult.isVisible = false
            }
        }
        binding.etSearch.setOnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN)
            ) {
                val keyword = binding.etSearch.text.toString().trim()
                if (keyword.isNotBlank()) {
                    mViewModel.saveRecord(keyword)
                }
                binding.etSearch.clearFocus()
                hideSoftKeyboard()
                true
            } else {
                false
            }
        }
        binding.etSearch.setOnFocusChangeListener { _, isFocus ->
            binding.laySearch.isSelected = isFocus
            binding.imgDelete.isVisible = isFocus
        }
        binding.imgBack.setOnClickListener {
            mViewModel.goBack()
        }
        binding.imgDelete.setOnClickListener {
            binding.etSearch.text.clear()
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.searchRecordList.observe(viewLifecycleOwner) { list ->
            (binding.rvSearchRecord.adapter as? SearchRecordAdapter)?.submitList(list)
        }
        mViewModel.searchResultFromShowList.observe(viewLifecycleOwner) { list ->
            (binding.rvSearchResult.adapter as? QuickAuthorizationListAdapter)?.submitList(list)
        }
        mViewModel.searchResultFromAddList.observe(viewLifecycleOwner) { list ->
            (binding.rvSearchResult.adapter as? AddCredentialListAdapter)?.submitList(list)
        }
        mViewModel.favoriteList.observe(viewLifecycleOwner) { list ->
            (binding.rvSearchResult.adapter as? QuickAuthorizationListAdapter)?.setFavorites(list)
        }
        mViewModel.showNoResultText.observe(viewLifecycleOwner) { isShow ->
            binding.layNoFoundResult.isVisible = isShow
        }
        mViewModel.backToFragmentByPag.observe(viewLifecycleOwner) { page ->
            when (page) {
                AddCredential -> {
                    (parentFragment as? HomeFragment)?.changeFragment(AddCredentialFragment.newInstance(), AddCredentialFragment::class.hashTag())
                }

                ShowCredential -> {
                    (parentFragment as? HomeFragment)?.changeFragment(ShowCredentialFragment.newInstance(), ShowCredentialFragment::class.hashTag())
                }

                else -> {}
            }
        }
        mViewModel.switchAdapterByPage.observe(viewLifecycleOwner) { page ->
            when (page) {
                AddCredential -> {
                    binding.rvSearchResult.adapter = AddCredentialListAdapter(
                        mListener = {
                            mViewModel.checkBrowserType(it.type, it.issuerServiceUrl, it.name)
                        },
                        coroutineScope = viewLifecycleOwner.lifecycleScope,
                        verifiableManager = mVerifiableManager
                    )
                }

                ShowCredential -> {
                    binding.rvSearchResult.adapter = QuickAuthorizationListAdapter(
                        mOpenPageListener = {
                            mViewModel.selectShowCredential(it)
                        },
                        mAddToFavoriteListener = {
                            mViewModel.toggleFavorite(it)
                        }
                    )
                }

                else -> {}
            }
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
    }

    private fun hideSoftKeyboard() {
        activity?.apply {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(window.decorView.applicationWindowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}