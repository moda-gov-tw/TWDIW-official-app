package tw.gov.moda.digitalwallet.ui.home.setting.delete

import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.extension.setOnAntiStickClickLisener
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.home.HomeFragment
import tw.gov.moda.digitalwallet.ui.home.setting.SettingFragment
import tw.gov.moda.digitalwallet.ui.home.setting.wallet.WalletSettingFragment
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentDeleteWalletResultBinding

@AndroidEntryPoint
class DeleteWalletResultFragment : BaseFragment<FragmentDeleteWalletResultBinding>() {

    companion object {
        fun newInstance() = DeleteWalletResultFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentDeleteWalletResultBinding = FragmentDeleteWalletResultBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: DeleteWalletResultViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

    }

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        mActivityViewModel.pageController.popBackStack(listOf(SettingFragment::class.hashTag(), WalletSettingFragment::class.hashTag(), HomeFragment::class.hashTag()))
        binding.btnConfirm.setOnAntiStickClickLisener {
            mActivityViewModel.pageController.logout()
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.walletName.observe(viewLifecycleOwner) { name ->
            binding.tvWalletName.text = getString(R.string.format_success_delete_wallet).format(name)
        }
    }
}