package tw.gov.moda.digitalwallet.ui.verifiable.authorized

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.extension.addItemDecoration
import tw.gov.moda.digitalwallet.ui.adapter.AuthorizedCredentialAdapter
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentAuthorizedCredentialBinding

@AndroidEntryPoint
class AuthorizedCredentialFragment : BaseFragment<FragmentAuthorizedCredentialBinding>() {

    companion object {
        fun newInstance() = AuthorizedCredentialFragment()
    }

    private val mViewModel: AuthorizedCredentialViewModel by viewModels()
    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun initViewBinding(container: ViewGroup?): FragmentAuthorizedCredentialBinding {
        return FragmentAuthorizedCredentialBinding.inflate(layoutInflater, container, false)
    }

    override fun getViewModel(): BaseViewModel {
        return mViewModel
    }

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        setToolbar(getString(R.string.authorized_verification_credential_list), { imageView ->
            imageView.isVisible = true
            imageView.setImageResource(R.drawable.ic_arrow_left_2_fill)
            imageView.setOnClickListener {
                mActivityViewModel.pageController.popBackStack()
            }
        })

        context?.apply {
            binding.recyclerView.addItemDecoration(R.drawable.shape_line_gray02)
            binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.recyclerView.adapter = AuthorizedCredentialAdapter()
        }

        mViewModel.getData()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden.not()) {
            mViewModel.getData()
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.authorizedCredentialList.observe(viewLifecycleOwner) { list ->
            (binding.recyclerView.adapter as? AuthorizedCredentialAdapter)?.submitList(list)
        }
    }
}