package tw.gov.moda.digitalwallet.ui.create.wallet.level

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.extension.setOnAntiStickClickLisener
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.create.wallet.name.CreateWalletNameFragment
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentCreateWalletLevelBinding

/**
 * 建立皮夾的類型選擇頁
 */
@AndroidEntryPoint
class CreateWalletLevelFragment : BaseFragment<FragmentCreateWalletLevelBinding>() {

    companion object {
        fun newInstance() = CreateWalletLevelFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentCreateWalletLevelBinding = FragmentCreateWalletLevelBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: CreateWalletLevelViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()


    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        setToolbar(getString(R.string.create_wallet), { imageView ->
            imageView.isVisible = true
            imageView.setImageResource(R.drawable.ic_arrow_left_2_fill)
            imageView.setOnClickListener {
                mActivityViewModel.pageController.popToFirstPage()
            }
        })

        mActivityViewModel.pageController.popBackStack(
            listOf(
                CreateWalletNameFragment::class.hashTag()
            )
        )

        binding.btnCancel.setOnClickListener {
            mActivityViewModel.pageController.popToFirstPage()
        }
        binding.btnConfirm.setOnAntiStickClickLisener {
            mActivityViewModel.pageController.launchCreateWalletNameFragment()
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
    }
}