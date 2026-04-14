package tw.gov.moda.digitalwallet.ui.home.barcode

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.ui.adapter.QuickAuthorizationListAdapter
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.home.HomeFragment
import tw.gov.moda.digitalwallet.ui.home.barcode.search.SearchCredentialListFragment
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.databinding.FragmentShowCredentialsBinding

/**
 * 皮夾條碼頁
 */
@AndroidEntryPoint
class ShowCredentialFragment : BaseFragment<FragmentShowCredentialsBinding>() {

    companion object {
        fun newInstance() = ShowCredentialFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentShowCredentialsBinding = FragmentShowCredentialsBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: ShowCredentialViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mViewModel.refreshShowCredentialList()
        }
    }

    override fun initView() {
        super.initView()
        mViewModel.refreshShowCredentialList()
        context?.apply {
            binding.rvMyFavorite.itemAnimator = null
            binding.rvMyFavorite.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.rvMyFavorite.adapter = QuickAuthorizationListAdapter(
                mOpenPageListener = {
                    mViewModel.selectShowCredential(it)
                },
                mAddToFavoriteListener = {
                    mViewModel.toggleFavorite(it)
                }
            )
            binding.rvQuickAuthorization.itemAnimator = null
            binding.rvQuickAuthorization.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.rvQuickAuthorization.adapter = QuickAuthorizationListAdapter(
                mOpenPageListener = {
                    mViewModel.selectShowCredential(it)
                },
                mAddToFavoriteListener = {
                    mViewModel.toggleFavorite(it)
                }
            )
        }

        binding.imgOpenQuickAuthorizationList.setOnClickListener {
            binding.rvQuickAuthorization.isVisible = !binding.rvQuickAuthorization.isVisible
            binding.imgOpenQuickAuthorizationList.isSelected = !binding.imgOpenQuickAuthorizationList.isSelected
        }
        binding.imgOpenMyFavorite.setOnClickListener {
            binding.rvMyFavorite.isVisible = !binding.rvMyFavorite.isVisible
            binding.imgOpenMyFavorite.isSelected = !binding.imgOpenMyFavorite.isSelected
        }
        binding.laySearch.setOnClickListener {
            (parentFragment as? HomeFragment)?.changeFragment(SearchCredentialListFragment.newInstance(), SearchCredentialListFragment::class.hashTag())
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            mViewModel.getShowCredentialList()
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.isEmptyFavoriteList.observe(viewLifecycleOwner) { isEmpty ->
            binding.imgOpenMyFavorite.isVisible = !isEmpty
            binding.tvQuicklyClick.isVisible = isEmpty
        }
        mViewModel.isEmptyQuickAuthorizationList.observe(viewLifecycleOwner) { isEmpty ->
            binding.imgOpenQuickAuthorizationList.isVisible = !isEmpty
        }
        mViewModel.quickAuthorizationList.observe(viewLifecycleOwner) { list ->
            binding.swipeRefreshLayout.isRefreshing = false
            (binding.rvQuickAuthorization.adapter as? QuickAuthorizationListAdapter)?.submitList(list)
        }
        mViewModel.filteredFavoriteList.observe(viewLifecycleOwner) { list ->
            binding.swipeRefreshLayout.isRefreshing = false
            (binding.rvMyFavorite.adapter as? QuickAuthorizationListAdapter)?.setFavorites(list)
            (binding.rvMyFavorite.adapter as? QuickAuthorizationListAdapter)?.submitList(list)
        }
        mViewModel.closeSwipeRefreshLayout.observe(viewLifecycleOwner){
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}