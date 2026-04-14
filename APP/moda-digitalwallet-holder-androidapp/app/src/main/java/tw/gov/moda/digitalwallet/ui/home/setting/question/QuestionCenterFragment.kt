package tw.gov.moda.digitalwallet.ui.home.setting.question

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.extension.showNetworkErrorDialog
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentQuestionCenterBinding


/**
 * 問題中心頁
 */
@AndroidEntryPoint
class QuestionCenterFragment : BaseFragment<FragmentQuestionCenterBinding>() {

    companion object {
        fun newInstance() = QuestionCenterFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentQuestionCenterBinding = FragmentQuestionCenterBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: QuestionCenterViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        setToolbar(getString(R.string.question_center), { imageView ->
            imageView.isVisible = true
            imageView.setImageResource(R.drawable.selector_button_arrow2_left)
            imageView.setOnClickListener {
                mActivityViewModel.pageController.popBackStack()
            }
        })

        binding.layCommonQuestion.setOnClickListener {
            mViewModel.commonQuestion()
        }
        binding.layReportQuestion.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply { setData(Uri.parse(AppConstants.Net.REPORT_QUESTION)) })
        }

        binding.layAboutDigitalWallet.setOnClickListener {
            mActivityViewModel.pageController.launchContactCustomerServiceFragment()
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.showNetworkErrorAlert.observe(viewLifecycleOwner) { isShow ->
            activity?.apply {
                if (isShow && !isFinishing) {
                    showNetworkErrorDialog(supportFragmentManager) {
                        mViewModel.dismissNetworkErrorDialog()
                    }
                }
            }
        }
        mViewModel.launchCommonQuestion.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                mActivityViewModel.pageController.launchWebViewFragment()
                mViewModel.launchCommonQuestion(false)
            }
        }
    }


}