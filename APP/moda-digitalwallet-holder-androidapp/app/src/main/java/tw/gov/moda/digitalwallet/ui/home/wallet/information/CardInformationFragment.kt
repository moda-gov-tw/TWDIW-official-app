package tw.gov.moda.digitalwallet.ui.home.wallet.information

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.data.element.VerificationSourceEnum
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.ui.adapter.VerifiableCredentialInfo2Adapter
import tw.gov.moda.digitalwallet.ui.base.LoginBaseFragment
import tw.gov.moda.digitalwallet.ui.base.LoginBaseViewModel
import tw.gov.moda.digitalwallet.ui.dialog.MyDialog
import tw.gov.moda.digitalwallet.ui.home.wallet.record.CardRecordFragment
import tw.gov.moda.digitalwallet.ui.login.pincode.LoginPinCodeFragment
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentCardInformationPageBinding
import tw.gov.moda.diw.databinding.IncludeToolbarBinding
import tw.gov.moda.diw.databinding.PopupCardInfoBinding

@AndroidEntryPoint
class CardInformationFragment : LoginBaseFragment<FragmentCardInformationPageBinding>() {

    companion object {
        fun newInstance() = CardInformationFragment()

        private const val DIALOG_DELETE_CARD = "DIALOG_DELETE_CARD"
        private const val DIALOG_DELETE_SUCCESSFUL = "DIALOG_DELETE_SUCCESSFUL"
    }

    override fun initViewBinding(container: ViewGroup?): FragmentCardInformationPageBinding = FragmentCardInformationPageBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): LoginBaseViewModel? = mViewModel
    override fun getActivityViewModel(): MainViewModel? = mActivityViewModel

    private val mViewModel: CardInformationViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    private var mPopupMenu: PopupWindow? = null
    private var toolbarBinding: IncludeToolbarBinding? = null
    private var popupCardInfoBinding: PopupCardInfoBinding? = null


    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        setToolbar(
            getString(R.string.credential_info),
            setLeft = { imageView ->
                imageView.isVisible = true
                imageView.setImageResource(R.drawable.selector_button_arrow2_left)
                imageView.setOnClickListener {
                    mActivityViewModel.pageController.popBackStack()
                }
            },
            setRight = { imageView ->
                imageView.isVisible = true
                imageView.setImageResource(R.drawable.selector_outline_button_more)
                imageView.setOnClickListener {
                    mPopupMenu?.showAsDropDown(toolbarBinding?.imgRight)
                }
            }
        )

        context?.apply {
            toolbarBinding = IncludeToolbarBinding.bind(binding.root)
            popupCardInfoBinding = PopupCardInfoBinding.inflate(LayoutInflater.from(this))
            popupCardInfoBinding?.layCredentialRecord?.setOnClickListener {
                mPopupMenu?.dismiss()
                mActivityViewModel.pageController.launchCardRecordFragment()
            }
            popupCardInfoBinding?.layDeleteCard?.setOnClickListener {
                mPopupMenu?.dismiss()
                MyDialog.Builder(this)
                    .setTitle(R.string.remove_credential)
                    .setMessage(getString(R.string.format_are_you_sure_you_want_to_delete).format(mViewModel.cardName.value ?: ""))
                    .setRightButtonText(R.string.confirm) {
                        mViewModel.requireVerification(VerificationSourceEnum.DeleteVerifiableCredential)
                    }
                    .setLeftButtonText(R.string.cancel)
                    .show(parentFragmentManager, DIALOG_DELETE_CARD)
            }
            mPopupMenu = PopupWindow(popupCardInfoBinding?.root).apply {
                width = ViewGroup.LayoutParams.WRAP_CONTENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                isOutsideTouchable = true
                isFocusable = true
            }
            binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.recyclerView.adapter = VerifiableCredentialInfo2Adapter(
                statusChange = { status ->
                    binding.tvHideAll.text = if (status) getString(R.string.hide_all) else getString(R.string.show_all)
                }
            )
            binding.tvHideAll.setOnClickListener {
                (binding.recyclerView.adapter as? VerifiableCredentialInfo2Adapter)?.hideAllTextViews()
            }
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        // 呈現 VC 資料
        mViewModel.credentialInformation.observe(viewLifecycleOwner) { list ->
            (binding.recyclerView.adapter as? VerifiableCredentialInfo2Adapter)?.submitList(list)
        }
        mViewModel.cardName.observe(viewLifecycleOwner) { cardName ->
            binding.tvCardTitle.text = cardName
        }
        mViewModel.issuer.observe(viewLifecycleOwner) { issuer ->
            binding.tvIssuer.text = issuer
        }
        mViewModel.cardImage.observe(viewLifecycleOwner) { image ->
            binding.imgCard.setImageBitmap(image)
        }
        mViewModel.popBackStack.observe(viewLifecycleOwner) { isBack ->
            if (isBack) {
                mActivityViewModel.pageController.popToFirstPage()

                activity?.apply {
                    MyDialog.Builder(this)
                        .setTitle(R.string.completed_delete_credential)
                        .setMessage(getString(R.string.format_you_deleted_the_credential).format(mViewModel.cardName.value ?: ""))
                        .setRightButtonText(R.string.confirm)
                        .show(supportFragmentManager, DIALOG_DELETE_SUCCESSFUL)
                }
            }
        }
        mViewModel.cardIALLevel.observe(viewLifecycleOwner) { level ->
            binding.tvIalLevel.text = getString(R.string.format_ial_level).format(level)
        }
        mViewModel.cardExpireDate.observe(viewLifecycleOwner) { date ->
            binding.tvCardExpireDate.text = getString(R.string.expire_date).format(date)
        }
    }
}