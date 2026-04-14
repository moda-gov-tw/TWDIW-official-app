package tw.gov.moda.digitalwallet.ui.home.wallet.record

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.data.element.OrderEnum
import tw.gov.moda.digitalwallet.ui.adapter.CardRecordAdapter
import tw.gov.moda.digitalwallet.ui.adapter.VerifiableCredentialInfo2Adapter
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentCardRecordPageBinding
import tw.gov.moda.diw.databinding.IncludeToolbarBinding
import tw.gov.moda.diw.databinding.PopupCardInfoBinding

/**
 * 卡片紀錄頁
 */
@AndroidEntryPoint
class CardRecordFragment : BaseFragment<FragmentCardRecordPageBinding>() {

    companion object {
        fun newInstance() = CardRecordFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentCardRecordPageBinding = FragmentCardRecordPageBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: CardRecordViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    private var mPopupMenu: PopupWindow? = null
    private var toolbarBinding: IncludeToolbarBinding? = null

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        setToolbar(
            getString(R.string.credential_record),
            setLeft = { imageView ->
                imageView.isVisible = true
                imageView.setImageResource(R.drawable.selector_button_arrow2_left)
                imageView.setOnClickListener {
                    mActivityViewModel.pageController.popBackStack()
                }
            }
        )

        context?.apply {
            toolbarBinding = IncludeToolbarBinding.bind(binding.root)

            val view = layoutInflater.inflate(R.layout.popup_order, null)
            mPopupMenu = PopupWindow(view).apply {
                width = ViewGroup.LayoutParams.WRAP_CONTENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                isOutsideTouchable = true
                isFocusable = true
            }
            view.findViewById<FrameLayout>(R.id.lay_new_to_old).setOnClickListener {
                mPopupMenu?.dismiss()
                binding.tvOrder.text = getString(R.string.new_to_old)
                mViewModel.refreshRecords(OrderEnum.DESC)
            }
            view.findViewById<FrameLayout>(R.id.lay_old_to_new).setOnClickListener {
                mPopupMenu?.dismiss()
                binding.tvOrder.text = getString(R.string.old_to_new)
                mViewModel.refreshRecords(OrderEnum.ASC)
            }
        }

        binding.layOrder.setOnClickListener {
            mPopupMenu?.showAsDropDown(binding.layOrder)
        }

        context?.apply {
            binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.recyclerView.adapter = CardRecordAdapter()
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
        mViewModel.updateDateTime.observe(viewLifecycleOwner) { datetime ->
            binding.tvCardUpdateDatetime.text = getString(R.string.format_update_datetime).format(datetime)
        }
        mViewModel.issuer.observe(viewLifecycleOwner) { issuer ->
            binding.tvIssuer.text = issuer
        }
        mViewModel.cardImage.observe(viewLifecycleOwner) { image ->
            binding.imgCard.setImageBitmap(image)
        }
        mViewModel.orderType.observe(viewLifecycleOwner) { type ->
            mPopupMenu?.contentView?.apply {
                if (type == OrderEnum.DESC) {
                    findViewById<FrameLayout>(R.id.lay_new_to_old).setBackgroundResource(R.drawable.shape_menu_background)
                    findViewById<FrameLayout>(R.id.lay_old_to_new).setBackgroundResource(0)
                } else {
                    findViewById<FrameLayout>(R.id.lay_new_to_old).setBackgroundResource(0)
                    findViewById<FrameLayout>(R.id.lay_old_to_new).setBackgroundResource(R.drawable.shape_menu_background)
                }
            }
        }
        mViewModel.combinedCredentialOperationRecordList.observe(viewLifecycleOwner) { list ->
            (binding.recyclerView.adapter as? CardRecordAdapter)?.submitList(list)
        }
    }
}