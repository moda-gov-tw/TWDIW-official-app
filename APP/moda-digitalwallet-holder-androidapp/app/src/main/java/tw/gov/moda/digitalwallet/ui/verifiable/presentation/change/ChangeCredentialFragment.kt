package tw.gov.moda.digitalwallet.ui.verifiable.presentation.change

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.ui.adapter.ChangeCredentialAdapter
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentChangeCredentialBinding

@AndroidEntryPoint
class ChangeCredentialFragment : BaseFragment<FragmentChangeCredentialBinding>(), View.OnClickListener {

    companion object {
        fun newInstance() = ChangeCredentialFragment()
    }

    private val mViewModel: ChangeCredentialViewModel by viewModels()

    override fun initViewBinding(container: ViewGroup?): FragmentChangeCredentialBinding {
        return FragmentChangeCredentialBinding.inflate(layoutInflater, container, false)
    }

    override fun getViewModel(): BaseViewModel = mViewModel
    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        setToolbar(getString(R.string.change_credential_2), { imageView ->
            imageView.isVisible = true
            imageView.setImageResource(R.drawable.ic_arrow_left_2_fill)
            imageView.setOnClickListener {
                mActivityViewModel.pageController.popBackStack()
            }
        })

        binding.recyclerView.itemAnimator = null
        binding.recyclerView.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = ChangeCredentialAdapter(
            mItemCheckListener = { index, uid, isCheck ->
                mViewModel.setSelectCard(index, uid, isCheck)
            },
            mItemExpandListener = { index, isExpand ->
                mViewModel.toggleExpandContent(index, isExpand)
            }
        )
        binding.imgEye.setOnClickListener(this)
        binding.btnConfirm.setOnClickListener(this)
        binding.layExpand.setOnClickListener(this)
        mViewModel.getData()
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.verifiablePresentationCardList.observe(viewLifecycleOwner) { list ->
            (binding.recyclerView.adapter as? ChangeCredentialAdapter)?.submitList(list)
        }
        mViewModel.isHiddenContent.observe(viewLifecycleOwner) { isHidden ->
            if (isHidden) {
                binding.imgEye.setImageResource(R.drawable.ic_eye_off)
            } else {
                binding.imgEye.setImageResource(R.drawable.ic_eye_on)
            }
            (binding.recyclerView.adapter as? ChangeCredentialAdapter)?.setVisible(isHidden)
        }
        mViewModel.selectCountTitle.observe(viewLifecycleOwner) { title ->
            binding.tvCertificateContent.text = title
        }
        mViewModel.isExpandAll.observe(viewLifecycleOwner) { isExpand ->
            if (isExpand) {
                binding.tvToggleExpand.text = getString(R.string.collapse_all)
                binding.imgToggleExpand.isSelected = false
            } else {
                binding.tvToggleExpand.text = getString(R.string.expand_all)
                binding.imgToggleExpand.isSelected = true
            }
        }
        mViewModel.selectCountMax.observe(viewLifecycleOwner) { text ->
            binding.tvSelectCredentialMax.text = text
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.imgEye -> {
                mViewModel.toggleHideContent()
            }
            binding.layExpand -> {
                mViewModel.toggleExpandAllList()
            }
            binding.btnConfirm -> {
                mViewModel.setSelectCardList()
                mActivityViewModel.pageController.popBackStack()
            }
        }
    }
}