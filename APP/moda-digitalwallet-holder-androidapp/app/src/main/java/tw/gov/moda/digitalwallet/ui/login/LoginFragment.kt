package tw.gov.moda.digitalwallet.ui.login

import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.extension.setOnAntiStickClickLisener
import tw.gov.moda.digitalwallet.ui.adapter.WalletListAdapter
import tw.gov.moda.digitalwallet.ui.base.BaseActivity
import tw.gov.moda.digitalwallet.ui.base.LoginBaseFragment
import tw.gov.moda.digitalwallet.ui.base.LoginBaseViewModel
import tw.gov.moda.digitalwallet.ui.create.contract.ContractFragment
import tw.gov.moda.digitalwallet.ui.create.wallet.explanation.CreateWalletExplanationFragment
import tw.gov.moda.digitalwallet.ui.create.wallet.name.CreateWalletNameFragment
import tw.gov.moda.digitalwallet.ui.create.wallet.pincode.CreatePinCodeFragment
import tw.gov.moda.digitalwallet.ui.create.welcome.WelcomeFragment
import tw.gov.moda.digitalwallet.ui.home.HomeFragment
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.digitalwallet.ui.splash.SplashFragment
import tw.gov.moda.diw.databinding.FragmentLoginBinding


/**
 * 登入頁
 */
@AndroidEntryPoint
class LoginFragment : LoginBaseFragment<FragmentLoginBinding>() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentLoginBinding = FragmentLoginBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): LoginBaseViewModel? = mViewModel

    override fun getActivityViewModel(): MainViewModel? = mActivityViewModel

    private val mViewModel: LoginViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    private var mPopupMenu: PopupWindow? = null

    private var mAdapter: WalletListAdapter = WalletListAdapter { wallet: Wallet ->
        mPopupMenu?.dismiss()
        mViewModel.selectWallet(wallet)
    }

    override fun onResume() {
        super.onResume()
        mViewModel.initWallet()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mActivityViewModel.pageController.popBackStack(
                listOf(
                    CreateWalletNameFragment::class.hashTag(),
                    CreatePinCodeFragment::class.hashTag(),
                    HomeFragment::class.hashTag()
                )
            )
            mViewModel.initWallet()
        }
    }

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        mActivityViewModel.pageController.popBackStack(
            listOf(
                ContractFragment::class.hashTag(),
                SplashFragment::class.hashTag(),
                WelcomeFragment::class.hashTag(),
                CreateWalletNameFragment::class.hashTag(),
                CreateWalletExplanationFragment::class.hashTag(),
                CreatePinCodeFragment::class.hashTag(),
                HomeFragment::class.hashTag()
            )
        )

        binding.btnConfirm.setOnAntiStickClickLisener {
            mViewModel.confirm()
        }
        (activity as? BaseActivity<*>)?.setObscuredDetector(binding.btnConfirm)
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.walletNickname.observe(viewLifecycleOwner) { nickname ->
            binding.tvWalletName.text = nickname
            mAdapter.setSelectedNickname(nickname)
            if (mViewModel.getContinueUsing()) {
                // 是否繼續使用皮夾？
                mViewModel.confirm()
            }
        }
        mViewModel.walletList.observe(viewLifecycleOwner) { list ->
            mAdapter.submitList(list)
        }
        mViewModel.showDeviceSecure.observe(viewLifecycleOwner) { wallet ->
            if (wallet != null) {
                activity?.apply {
                    mActivityViewModel.authenticateBiometric(this, wallet)
                }
            }
        }
        mViewModel.launchHomeFragment.observe(viewLifecycleOwner) { isShow ->
            if (isShow && activity != null) {
                mActivityViewModel.pageController.launchHomeFragment()
            }
        }
        mViewModel.isNewWallet.observe(viewLifecycleOwner) { isNew ->
            if (isNew) {
                mActivityViewModel.pageController.launchCreateWalletLevelFragment()
            }
        }
    }
}