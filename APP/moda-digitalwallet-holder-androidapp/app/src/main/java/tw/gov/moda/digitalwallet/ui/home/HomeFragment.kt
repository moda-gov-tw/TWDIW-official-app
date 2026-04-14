package tw.gov.moda.digitalwallet.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.data.element.PageEnum
import tw.gov.moda.digitalwallet.data.model.RemindAlert
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.ui.adapter.RemindAlertAdapter
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.create.welcome.WelcomeFragment
import tw.gov.moda.digitalwallet.ui.dialog.MyDialog
import tw.gov.moda.digitalwallet.ui.home.add.AddCredentialFragment
import tw.gov.moda.digitalwallet.ui.home.barcode.ShowCredentialFragment
import tw.gov.moda.digitalwallet.ui.home.setting.SettingFragment
import tw.gov.moda.digitalwallet.ui.home.wallet.WalletFragment
import tw.gov.moda.digitalwallet.ui.login.LoginFragment
import tw.gov.moda.digitalwallet.ui.login.pincode.LoginPinCodeFragment
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentHomeBinding

/**
 * 首頁
 */
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    companion object {
        fun newInstance() = HomeFragment()
        private const val DIALOG_PERMISSION_CAMERA = "DIALOG_PERMISSION_CAMERA"
    }

    override fun initViewBinding(container: ViewGroup?): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: HomeViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true) {
            mViewModel.selectTab(PageEnum.Scan)
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
            mActivityViewModel.detectAppLink()
        }
    }

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        mActivityViewModel.pageController.popBackStack(listOf(LoginPinCodeFragment::class.hashTag(), WelcomeFragment::class.hashTag(), LoginFragment::class.hashTag()))
        binding.tabWallet.setOnClickListener {
            mViewModel.selectTab(PageEnum.Wallet)
        }
        binding.tabAddCredential.setOnClickListener {
            mViewModel.selectTab(PageEnum.AddCredential)
        }
        binding.tabShowCredential.setOnClickListener {
            mViewModel.selectTab(PageEnum.ShowCredential)
        }
        binding.tabScan.setOnClickListener {
            context?.apply {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    mViewModel.selectTab(PageEnum.Scan)
                } else {
                    requestMultiplePermissions.launch(arrayOf(Manifest.permission.CAMERA))
                }
            }
        }

        binding.tabSetting.setOnClickListener {
            mViewModel.selectTab(PageEnum.Setting)
        }

        // 建立提醒彈跳視窗設定
        binding.viewPagerRemindAlerts.adapter = RemindAlertAdapter { remindAlert ->
            mViewModel.confirmRemindAlert(remindAlert)
            val nextIndex = binding.viewPagerRemindAlerts.currentItem + 1
            if (nextIndex < (binding.viewPagerRemindAlerts.adapter?.itemCount ?: 0)) {
                binding.viewPagerRemindAlerts.setCurrentItem(nextIndex, true)
            } else {
                binding.layRemindAlerts.isVisible = false
            }
        }
        binding.viewPagerRemindAlerts.post {
            (binding.viewPagerRemindAlerts.getChildAt(0) as? RecyclerView)?.itemAnimator = null
        }
        TabLayoutMediator(binding.tabRemindAlerts, binding.viewPagerRemindAlerts) { _, _ -> }.attach()

        mActivityViewModel.updateAutoLogoutTime()
        mActivityViewModel.detectAppLink()
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.indexPage.observe(viewLifecycleOwner) { indexPage ->
            if (indexPage != null) {
                when (indexPage) {
                    PageEnum.Wallet -> {
                        changeFragment(WalletFragment.newInstance(), WalletFragment::class.hashTag())
                        binding.viewTop.setBackgroundResource(R.color.white)
                    }

                    PageEnum.AddCredential -> {
                        changeFragment(AddCredentialFragment.newInstance(), AddCredentialFragment::class.hashTag())
                        binding.viewTop.setBackgroundResource(R.color.white)
                    }

                    PageEnum.ShowCredential -> {
                        changeFragment(ShowCredentialFragment.newInstance(), ShowCredentialFragment::class.hashTag())
                        binding.viewTop.setBackgroundResource(R.color.white)
                    }

                    PageEnum.Scan -> {
                        mActivityViewModel.pageController.launchScanFragment()
                        return@observe
                    }

                    PageEnum.Setting -> {
                        changeFragment(SettingFragment.newInstance(), SettingFragment::class.hashTag())
                        binding.viewTop.setBackgroundResource(R.color.gray_01)
                    }
                }
                binding.tabWallet.isSelected = false
                binding.tabAddCredential.isSelected = false
                binding.tabShowCredential.isSelected = false
                binding.tabSetting.isSelected = false
                when (indexPage) {
                    PageEnum.Wallet -> binding.tabWallet.isSelected = true
                    PageEnum.AddCredential -> binding.tabAddCredential.isSelected = true
                    PageEnum.ShowCredential -> binding.tabShowCredential.isSelected = true
                    PageEnum.Setting -> binding.tabSetting.isSelected = true
                    else -> {}
                }
            }
        }
        mViewModel.remindCredentialListAlerts.observe(viewLifecycleOwner) { data ->
            if (data != null) {

                val items = ArrayList<RemindAlert>()
                if (data.failure.isShow) {
                    items.add(data.failure)
                }
                if (data.willExpire.isShow) {
                    items.add(data.willExpire)
                }
                if (data.expired.isShow) {
                    items.add(data.expired)
                }

                if (items.isNotEmpty()) {
                    binding.layRemindAlerts.isVisible = true
                    (binding.viewPagerRemindAlerts.adapter as? RemindAlertAdapter)?.submitList(items) {
                        for (i in 0 until binding.tabRemindAlerts.tabCount) {
                            val tab = (binding.tabRemindAlerts.getChildAt(0) as ViewGroup).getChildAt(i)
                            tab.isClickable = false
                            tab.isEnabled = false
                        }
                    }

                    // 判斷是否顯示 Tablayout Indicator
                    if (items.size == 1) {
                        binding.tabRemindAlerts.visibility = View.INVISIBLE
                    } else {
                        binding.tabRemindAlerts.visibility = View.VISIBLE
                    }
                    mViewModel.launchRemindCredentialListAlerts(null)
                }
            }
        }
    }

    fun changeFragment(fragment: Fragment, tag: String) {
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        val fragmentList: List<Fragment> = childFragmentManager.fragments
        for (existFragment in fragmentList) {
            transaction.hide(existFragment)
        }
        val existFragment: Fragment? = childFragmentManager.findFragmentByTag(tag)
        if (existFragment != null) {
            transaction.show(existFragment)
        } else {
            transaction.add(binding.layContainer.getId(), fragment, tag)
        }
        transaction.commit()
    }


}