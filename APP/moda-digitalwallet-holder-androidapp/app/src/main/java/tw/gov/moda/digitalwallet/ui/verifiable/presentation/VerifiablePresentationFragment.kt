package tw.gov.moda.digitalwallet.ui.verifiable.presentation

import android.animation.ValueAnimator
import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.data.element.SDKErrorEnum
import tw.gov.moda.digitalwallet.data.element.VerifiablePresentationEnum
import tw.gov.moda.digitalwallet.data.element.VerificationSourceEnum
import tw.gov.moda.digitalwallet.extension.setOnAntiStickClickLisener
import tw.gov.moda.digitalwallet.extension.showNetworkErrorDialog
import tw.gov.moda.digitalwallet.ui.adapter.RequireVerifiableCredentialGroupAdapter
import tw.gov.moda.digitalwallet.ui.adapter.RequireVerifiablePresentationFieldAdapter
import tw.gov.moda.digitalwallet.ui.base.LoginBaseFragment
import tw.gov.moda.digitalwallet.ui.base.LoginBaseViewModel
import tw.gov.moda.digitalwallet.ui.dialog.MyDialog
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentVerifiablePresentationBinding

@AndroidEntryPoint
class VerifiablePresentationFragment : LoginBaseFragment<FragmentVerifiablePresentationBinding>() {

    companion object {
        private const val DIALOG_WITHOUT_4012_ERROR = "DIALOG_WITHOUT_4012_ERROR"
        private const val DIALOG_NOT_SELECTOR_ALL = "DIALOG_NOT_SELECTOR_ALL"
        private const val DIALOG_DELETE_CARD = "DIALOG_DELETE_CARD"
        private const val DIALOG_UNABLE_SEND_DATA = "DIALOG_UNABLE_SEND_DATA"
        fun newInstance() = VerifiablePresentationFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentVerifiablePresentationBinding = FragmentVerifiablePresentationBinding.inflate(layoutInflater, container, false)


    override fun getViewModel(): LoginBaseViewModel? = mViewModel
    override fun getActivityViewModel(): MainViewModel? = mActivityViewModel

    private val mViewModel: VerifiablePresentationViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    private val mVerifyProgressDialog: Dialog by lazy { initVerifyProgressDialog() }

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        setToolbar(getString(R.string.verification_application), { imageView ->
            imageView.isVisible = true
            imageView.setImageResource(R.drawable.ic_arrow_left_2_fill)
            imageView.setOnClickListener {
                mActivityViewModel.pageController.popBackStack()
            }
        })
        binding.btnCancel.setOnClickListener {
            mActivityViewModel.pageController.popBackStack()
        }
        binding.btnConfirm.setOnAntiStickClickLisener {
            mViewModel.checkSelectItem()
        }
        binding.layViewContract.setOnClickListener {
            mViewModel.launchContractDetailFragment(true)
        }
        binding.imgContractArrow.setOnClickListener {
            mViewModel.launchContractDetailFragment(true)
        }
        binding.imgEye.setOnClickListener {
            mViewModel.setIsGroupVisible()
        }
        context?.apply {
            binding.recyclerView.itemAnimator = null
            binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.recyclerView.adapter = RequireVerifiableCredentialGroupAdapter(
                mCheckAllListener = { group, card, isCheck ->
                    mViewModel.setIsCheckAll(group.title, card.card, isCheck)
                },
                mCheckItemListener = { group, card, field, isCheck ->
                    mViewModel.setIsCheck(group.title, card.card, field.field, isCheck)
                },
                mChangeCardListener = { group, card ->
                    mViewModel.showChangeCredential(group.title, card)
                },
                mExpandListener = { group, card, isExpand ->
                    mViewModel.setExpand(group.title, card.card, isExpand)
                },
                mShowAuthorizedListListener = { group ->
                    mViewModel.launchAuthorizedCredentialList(group.title)
                },
                mChildCommitCallback = {
                    if (mProgressDialog?.isShowing == true) {
                        mProgressDialog?.dismiss()
                    }
                }
            )
            binding.recyclerViewCustom.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.recyclerViewCustom.adapter = RequireVerifiablePresentationFieldAdapter(
                mTextChangeListener = { index, text ->
                    mViewModel.setCustomText(index, text)
                }
            )
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.enabledConfirm.observe(viewLifecycleOwner) { enable ->
            binding.btnConfirm.isEnabled = enable
        }
        mViewModel.requireVerifiableCredentialList.observe(viewLifecycleOwner) { list ->
            val newList = list.toMutableList()
            (binding.recyclerView.adapter as? RequireVerifiableCredentialGroupAdapter)?.submitList(newList)
        }
        mViewModel.vpCustomFieldList.observe(viewLifecycleOwner) { list ->
            (binding.recyclerViewCustom.adapter as? RequireVerifiablePresentationFieldAdapter)?.submitList(list)
        }
        mViewModel.verifyProgressBar.observe(viewLifecycleOwner) { isShow ->
            if (isShow && activity?.isFinishing == false) {
                mVerifyProgressDialog.show()
            } else {
                mVerifyProgressDialog.dismiss()
            }
        }
        mViewModel.launchVerificationResultFragment.observe(viewLifecycleOwner) { _ ->
            mActivityViewModel.pageController.launchVerificationResultFragment()
        }
        mViewModel.launchShowCredentialBarCodeFragment.observe(viewLifecycleOwner) { _ ->
            mActivityViewModel.pageController.launchShowCredentialBarCodeFragment()
        }
        mViewModel.verifiablePresentationType.observe(viewLifecycleOwner) { type ->
            when (type) {
                VerifiablePresentationEnum.NORMAL -> {
                    binding.btnConfirm.setText(R.string.start_verify)
                    binding.tvAgreeSend.text = getString(R.string.click_send_data_agree_provider_data,getString(R.string.start_verify))
                }

                VerifiablePresentationEnum.BARCODE -> {
                    binding.btnConfirm.setText(R.string.generate_barcode)
                    binding.tvAgreeSend.text = getString(R.string.click_send_data_agree_provider_data,getString(R.string.generate_barcode))
                }
            }
        }

        mViewModel.launchAuthorizedCredentialFragment.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                mActivityViewModel.pageController.launchAuthorizedCredentialFragment(true)
            }
        }
        mViewModel.verifiablePresentationUnit.observe(viewLifecycleOwner) { unit ->
            binding.tvVerificationTitle.text = "${getString(R.string.provider_of_information)}：$unit"
        }
        mViewModel.verifiablePresentationName.observe(viewLifecycleOwner) { name ->
            if (name.isNotBlank()) {
                val text = getString(R.string.format_known_agree_contract).format(name)
                binding.tvContractTitle.isVisible = true
                binding.tvContractTitle.text = text
                binding.root.findViewById<TextView>(R.id.tv_title).text = name

                binding.tvContractTitle.post {
                    if (binding.tvContractTitle.lineCount > 1) {
                        binding.imgContractArrow.isVisible = true
                        binding.tvContractTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null) // 移除 drawableEnd
                    } else {
                        binding.imgContractArrow.isVisible = false
                        val drawable = ContextCompat.getDrawable(binding.tvContractTitle.context, R.drawable.ic_arrow_right_2)
                        binding.tvContractTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null) // 加回來
                    }
                }
            } else {
                binding.tvContractTitle.isVisible = false
                binding.root.findViewById<TextView>(R.id.tv_title).text = getString(R.string.verification_application)
            }
        }
        mViewModel.showChangeVerifiablePresentationDialog.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                mActivityViewModel.pageController.launchChangeCredentialFragment()
            }
        }
        mViewModel.showNonAuthorizedCredentialDialog.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                context?.apply {
                    MyDialog.Builder(this)
                        .setTitle(R.string.confirm_org_information)
                        .setMessage(getString(SDKErrorEnum.ERROR_4012.message))
                        .setRightButtonText(R.string.confirm)
                        .show(childFragmentManager, DIALOG_WITHOUT_4012_ERROR)
                }
            }
        }
        mViewModel.showNotSelectorAllDialog.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                context?.apply {
                    MyDialog.Builder(this)
                        .setTitle(R.string.confirm_data)
                        .setMessage(R.string.confirm_data_alert)
                        .setRightButtonText(R.string.confirm) {
                            mViewModel.requireVerification(VerificationSourceEnum.VerifiablePresentation)
                        }
                        .setLeftButtonText(R.string.cancel)
                        .show(childFragmentManager, DIALOG_NOT_SELECTOR_ALL)
                }
            }
        }
        mViewModel.showDeleteCredentialDialog.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                context?.apply {
                    MyDialog.Builder(this)
                        .setTitle(R.string.confirm_data)
                        .setMessage(message)
                        .setRightButtonText(R.string.confirm) {
                            mViewModel.deleteCredential()
                        }
                        .setLeftButtonText(R.string.cancel)
                        .show(childFragmentManager, DIALOG_DELETE_CARD)
                }
            }
        }
        mViewModel.showCannotSendDataDialog.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                context?.apply {
                    MyDialog.Builder(this)
                        .setTitle(R.string.cannot_send_data)
                        .setMessage(R.string.miss_require_authorization_content)
                        .setRightButtonText(R.string.confirm) {
                            mViewModel.setShowCannotSendDataDialog(false)
                        }
                        .show(childFragmentManager, DIALOG_UNABLE_SEND_DATA)
                }
            }
        }
        mViewModel.launchContractDetailFragment.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                mActivityViewModel.pageController.launchVerifiablePresentationContractFragment(true)
                mViewModel.launchContractDetailFragment(false)
            }
        }
        mViewModel.showNetworkErrorAlert.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                context?.apply {
                    showNetworkErrorDialog(childFragmentManager) {
                        mViewModel.dismissNetworkErrorDialog()
                    }
                }
            }
        }
        mViewModel.isHidden.observe(viewLifecycleOwner) { isHidden ->
            if (isHidden) {
                binding.imgEye.setImageResource(R.drawable.ic_eye_off)
            } else {
                binding.imgEye.setImageResource(R.drawable.ic_eye_on)
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden.not()) {
            mViewModel.setSelectCredentialList()
        }
    }

    override fun getExceptionAlertAction(): (() -> Unit)? = {
        mActivityViewModel.pageController.launchVerificationResultFragment()
    }


    /**
     * 初始化`卡片授權中`的讀取頁面
     */
    private fun initVerifyProgressDialog(): Dialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_verify_loading, null)
        val progressBar = layout.findViewById<ImageView>(R.id.progress_bar)
        ValueAnimator.ofFloat(0F, 360F).apply {
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            duration = 800
        }?.also {
            it.addUpdateListener {
                progressBar.rotation = (it.animatedValue as Float)
            }
            it.start()
        }
        val dialog = Dialog(requireContext(), R.style.Theme_Digitalwallet_Loading)
        dialog.setCancelable(false)
        dialog.setContentView(layout)
        return dialog
    }
}