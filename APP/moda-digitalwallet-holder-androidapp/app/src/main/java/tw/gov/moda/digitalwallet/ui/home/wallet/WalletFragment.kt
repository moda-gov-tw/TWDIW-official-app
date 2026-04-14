package tw.gov.moda.digitalwallet.ui.home.wallet

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.data.element.OrderEnum
import tw.gov.moda.digitalwallet.data.element.PageEnum
import tw.gov.moda.digitalwallet.data.model.RemindAlert
import tw.gov.moda.digitalwallet.ui.adapter.RemindAlertAdapter
import tw.gov.moda.digitalwallet.ui.adapter.VerifiableCredentialAdapter
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.dialog.MyDialog
import tw.gov.moda.digitalwallet.ui.home.HomeFragment
import tw.gov.moda.digitalwallet.ui.home.HomeViewModel
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentWalletBinding

/**
 * 卡片總覽頁
 */
@AndroidEntryPoint
class WalletFragment : BaseFragment<FragmentWalletBinding>() {

    companion object {
        fun newInstance() = WalletFragment()
        private const val DIALOG_PERMISSION_CAMERA = "DIALOG_PERMISSION_CAMERA"
    }

    override fun initViewBinding(container: ViewGroup?): FragmentWalletBinding = FragmentWalletBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: WalletViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    private var mPopupMenu: PopupWindow? = null

    private var mHandler = Handler(Looper.getMainLooper())

    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true) {
            (parentFragment as? HomeFragment)?.viewModels<HomeViewModel>()?.apply {
                value.selectTab(PageEnum.Scan)
            }
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                activity?.apply {
                    MyDialog.Builder(this)
                        .setTitle(R.string.prompt_message)
                        .setMessage(R.string.msg_camera_permission_deny_warnning)
                        .setRightButtonText(R.string.confirm) {
                            context?.apply {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    addCategory(Intent.CATEGORY_DEFAULT)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                    addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                                }
                                val uri: Uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            }
                        }
                        .setLeftButtonText(R.string.cancel)
                        .show(childFragmentManager, DIALOG_PERMISSION_CAMERA)
                }
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mViewModel.onShown()
        }
    }

    override fun initView() {
        super.initView()
        context?.apply {
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
                (parentFragment as? HomeFragment)?.viewModels<HomeViewModel>()?.value?.also { homeViewModel ->
                    homeViewModel.needScrollToTop(true)
                }
                mViewModel.onShown(OrderEnum.DESC)
            }
            view.findViewById<FrameLayout>(R.id.lay_old_to_new).setOnClickListener {
                mPopupMenu?.dismiss()
                binding.tvOrder.text = getString(R.string.old_to_new)
                (parentFragment as? HomeFragment)?.viewModels<HomeViewModel>()?.value?.also { homeViewModel ->
                    homeViewModel.needScrollToTop(true)
                }
                mViewModel.onShown(OrderEnum.ASC)
            }
        }
        binding.layOrder.setOnClickListener {
            mPopupMenu?.showAsDropDown(binding.layOrder)
        }
        binding.imgAddCard.setOnClickListener {
            context?.apply {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    (parentFragment as? HomeFragment)?.viewModels<HomeViewModel>()?.apply {
                        value.selectTab(PageEnum.Scan)
                    }
                } else {
                    requestMultiplePermissions.launch(arrayOf(Manifest.permission.CAMERA))
                }
            }
        }
        context?.apply {
            binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.recyclerView.adapter = VerifiableCredentialAdapter {
                mViewModel.setDecodingVerifiableCredential(it)
            }
        }

        // 初始化狀態列
        binding.includeRefreshingCard.root.isVisible = false
        binding.includeRefreshFailureCard.root.isVisible = false
        binding.includeRefreshSuccessfullyCard.root.isVisible = false

        binding.includeRefreshFailureCard.tvCheck.apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }
        binding.includeRefreshFailureCard.tvCheck.setOnClickListener {
            binding.includeRefreshFailureCard.root.isVisible = false
            mViewModel.checkFailureCard()
        }

        binding.includeRefreshingCard.imgClose.setOnClickListener {
            binding.includeRefreshingCard.root.isVisible = false
            mViewModel.closeRefreshingCardView(true)
        }
        binding.includeRefreshFailureCard.imgClose.setOnClickListener {
            binding.includeRefreshFailureCard.root.isVisible = false
        }
        binding.includeRefreshSuccessfullyCard.imgClose.setOnClickListener {
            binding.includeRefreshSuccessfullyCard.root.isVisible = false
        }

        // 暫時先關閉該功能
        binding.imgAddNewCard.isVisible = true
        binding.imgAddNewCard.setOnClickListener {
            mActivityViewModel.pageController.launchOperateRecordFragment()
        }
        binding.imgRefreshCard.setOnClickListener {
            mViewModel.retryDetectVC(false)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            mViewModel.retryDetectVC(false)
        }

    }

    override fun onPause() {
        binding.includeRefreshSuccessfullyCard.root.isVisible = false
        mHandler.removeCallbacksAndMessages(null)
        super.onPause()
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.walletName.observe(viewLifecycleOwner) { walletName ->
            binding.tvWalletName.text = walletName
        }
        mViewModel.boundVerifiableCredentialList.observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                binding.swipeRefreshLayout.isVisible = true
                binding.recyclerView.isVisible = true
                binding.layEmptyCard.isVisible = false
            } else {
                binding.swipeRefreshLayout.isVisible = false
                binding.recyclerView.isVisible = false
                binding.layEmptyCard.isVisible = true
            }

            (binding.recyclerView.adapter as? VerifiableCredentialAdapter)?.apply {
                submitList(list) {
                    (parentFragment as? HomeFragment)?.viewModels<HomeViewModel>()?.value?.also { homeViewModel ->
                        if (homeViewModel.needScrollToTop()) {
                            binding.recyclerView.smoothScrollToPosition(0)
                            homeViewModel.needScrollToTop(false)
                        }
                    }
                }
            }
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
        mViewModel.refreshingCountOfCard.observe(viewLifecycleOwner) { text ->
            if (text.isNotBlank()) {
                binding.includeRefreshingCard.root.isVisible = true
                binding.includeRefreshFailureCard.root.isVisible = false
                binding.includeRefreshSuccessfullyCard.root.isVisible = false
                binding.includeRefreshingCard.tvTitle.text = getString(R.string.format_card_refreshing_count_sum).format(text)
            }
        }
        mViewModel.refreshCardFailure.observe(viewLifecycleOwner) { text ->
            if (text.isNotBlank()) {
                binding.includeRefreshingCard.root.isVisible = false
                binding.includeRefreshFailureCard.root.isVisible = true
                binding.includeRefreshSuccessfullyCard.root.isVisible = false

                binding.includeRefreshFailureCard.tvCheck.isVisible = true
                binding.includeRefreshFailureCard.tvTitle.text = getString(R.string.format_card_refresh_failure_count_sum).format(text)
            } else {
                binding.includeRefreshingCard.root.isVisible = false
                binding.includeRefreshFailureCard.root.isVisible = false
                binding.includeRefreshSuccessfullyCard.root.isVisible = false
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
        mViewModel.refreshCardSuccessfully.observe(viewLifecycleOwner) { text ->
            if (text.isNotBlank()) {
                binding.includeRefreshingCard.root.isVisible = false
                binding.includeRefreshFailureCard.root.isVisible = false
                binding.includeRefreshSuccessfullyCard.root.isVisible = true
                binding.includeRefreshSuccessfullyCard.tvTitle.text = getString(R.string.format_card_refresh_successfully_count_sum).format(text)
                mHandler.removeCallbacksAndMessages(null)
                mHandler.postDelayed({
                    binding.includeRefreshSuccessfullyCard.root.isVisible = false
                }, 3_500)

            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
        mViewModel.itemChangedPosition.observe(viewLifecycleOwner) { position ->
            (binding.recyclerView.adapter as? VerifiableCredentialAdapter)?.notifyItemChanged(position)
        }
        mViewModel.launchCardInformationFragment.observe(viewLifecycleOwner) { isShow ->
            if (isShow && activity?.isFinishing == false) {
                mActivityViewModel.pageController.launchCardInformationFragment(isShow)
            }
        }
        mViewModel.remindCredentialListAlerts.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                if (data.disconnect.isShow) {
                    // 顯示網路失敗 Toast
                    binding.includeRefreshingCard.root.isVisible = false
                    binding.includeRefreshFailureCard.root.isVisible = true
                    binding.includeRefreshSuccessfullyCard.root.isVisible = false

                    binding.includeRefreshFailureCard.tvCheck.isVisible = false
                    binding.includeRefreshFailureCard.tvTitle.text = getString(R.string.msg_card_refresh_failure)
                }
                (parentFragment as? HomeFragment)?.viewModels<HomeViewModel>()?.apply {
                    value.launchRemindCredentialListAlerts(data)
                }
                mViewModel.resetRemindCredentialListAlerts()
            }
        }
        mActivityViewModel.deeplinkController.getLiveData().addVerifiableCredentialSuccessful.observe(this) { name ->
            if (name != null) {
                activity?.apply {
                    if (name.isNotBlank() && !isFinishing) {
                        mViewModel.onShown()
                    }
                }
                mActivityViewModel.deeplinkController.addVerifiableCredentialSuccessful(null)
            }
        }
    }
}