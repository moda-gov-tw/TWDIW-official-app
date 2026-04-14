package tw.gov.moda.digitalwallet.ui.record

import JsonZipUtil
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.gov.moda.digitalwallet.data.element.OperateRecordEnum
import tw.gov.moda.digitalwallet.data.element.OrderEnum
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.digitalwallet.ui.record.adapter.OperateRecordAdapter
import tw.gov.moda.digitalwallet.ui.record.adapter.PresentationRecordAdapter
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentOperateRecordBinding
import tw.gov.moda.diw.databinding.IncludeToolbarBinding
import tw.gov.moda.diw.databinding.ItemOperateRecordTablayoutBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class OperateRecordFragment : BaseFragment<FragmentOperateRecordBinding>() {

    private val mViewModel: OperateRecordViewModel by viewModels()

    companion object {
        fun newInstance() = OperateRecordFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentOperateRecordBinding {
        return FragmentOperateRecordBinding.inflate(layoutInflater, container, false)
    }

    override fun getViewModel(): OperateRecordViewModel = mViewModel

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    private var mPopupMenu: PopupWindow? = null

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        setToolbar(getString(R.string.operation_record), { imageView ->
            imageView.isVisible = true
            imageView.setImageResource(R.drawable.ic_arrow2_left_default)
            imageView.setOnClickListener {
                mActivityViewModel.pageController.popBackStack()
            }
        }, { imageView ->
            imageView.isVisible = true
            imageView.setImageResource(R.drawable.ic_export)
            imageView.setOnClickListener {
                mViewModel.exportRecords()
            }
        })
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
                mViewModel.getData(mViewModel.operationRecordEnum.value ?: OperateRecordEnum.Presentation, OrderEnum.DESC)
            }
            view.findViewById<FrameLayout>(R.id.lay_old_to_new).setOnClickListener {
                mPopupMenu?.dismiss()
                binding.tvOrder.text = getString(R.string.old_to_new)
                mViewModel.getData(mViewModel.operationRecordEnum.value ?: OperateRecordEnum.Presentation, OrderEnum.ASC)
            }
        }

        binding.layOrder.setOnClickListener {
            mPopupMenu?.showAsDropDown(binding.layOrder)
        }

        val tab1 = binding.tabLayout.newTab().apply {
            val view = ItemOperateRecordTablayoutBinding.inflate(layoutInflater, binding.root as ViewGroup, false)
            view.tvTitle.text = getString(R.string.authorization_record)
            setCustomView(view.root)
        }
        val tab2 = binding.tabLayout.newTab().apply {
            val view = ItemOperateRecordTablayoutBinding.inflate(layoutInflater, binding.root as ViewGroup, false)
            view.tvTitle.text = getString(R.string.modification_record)
            setCustomView(view.root)
        }

        context?.apply {
            binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }


        binding.tabLayout.addTab(tab1)
        binding.tabLayout.addTab(tab2)
        binding.tabLayout.setSelectedTabIndicator(null)
        binding.tabLayout.tabRippleColor = null
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: return
                val type = if (position == 0) {
                    OperateRecordEnum.Presentation
                } else {
                    OperateRecordEnum.Modification
                }
                mViewModel.getData(type, mViewModel.orderType.value ?: OrderEnum.DESC)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
        binding.tabLayout.selectTab(tab1)
        mViewModel.getData(mViewModel.operationRecordEnum.value ?: OperateRecordEnum.Presentation, mViewModel.orderType.value ?: OrderEnum.DESC)
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.operationRecordEnum.observe(viewLifecycleOwner) { type ->

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
        mViewModel.operateRecordList.observe(viewLifecycleOwner) { list ->
            if (binding.recyclerView.adapter is OperateRecordAdapter) {
                (binding.recyclerView.adapter as? OperateRecordAdapter)?.submitList(list)
            } else {
                val adapter = OperateRecordAdapter {
                    mViewModel.nextPage()
                }
                binding.recyclerView.adapter = adapter
                adapter.submitList(list)
            }
        }
        mViewModel.presentationRecordList.observe(viewLifecycleOwner) { list ->
            if (binding.recyclerView.adapter is PresentationRecordAdapter) {
                (binding.recyclerView.adapter as? PresentationRecordAdapter)?.submitList(list)
            } else {
                val adapter = PresentationRecordAdapter {
                    mViewModel.nextPage()
                }
                binding.recyclerView.adapter = adapter
                adapter.submitList(list)
            }
        }
        mViewModel.updateTime.observe(viewLifecycleOwner) { text ->
            binding.tvRecordUpdateTime.text = getString(R.string.format_update_datetime).format(text)
        }
        mViewModel.jsonFilesToZip.observe(viewLifecycleOwner) { jsonList ->
            MainScope().launch {
                context?.apply {
                    // 1) 決定放哪裡：cacheDir（臨時）或 filesDir（較持久）
                    val baseDir = cacheDir  // 或 filesDir

                    // 2) 將多筆 JSON 轉成多個檔案
                    val jsonFiles = withContext(Dispatchers.Default) {
                        jsonList.map { (name, json) ->
                            JsonZipUtil.jsonToFile(baseDir, name, json)
                        }
                    }

                    // 3) 壓成一個 ZIP（可選擇把檔案放在 zip 裡的某個資料夾）
                    val sdf = SimpleDateFormat("yyyy_MM_dd_HHmmss", Locale.TAIWAN)
                    val prefix = "${getString(R.string.app_name)}操作日誌"
                    val filename = sdf.format(Date())
                    val zipFile = File(baseDir, "${prefix}_${filename}.zip")
                    withContext(Dispatchers.Default) {
                        JsonZipUtil.filesToZip(
                            files = jsonFiles,
                            zipFile = zipFile,
                            insideFolder = "logs/",
                            deleteZipsWithPrefix = prefix
                        )
                    }

                    // 4) 分享 ZIP
                    JsonZipUtil.shareZip(this, zipFile)
                }
            }
        }
    }
}