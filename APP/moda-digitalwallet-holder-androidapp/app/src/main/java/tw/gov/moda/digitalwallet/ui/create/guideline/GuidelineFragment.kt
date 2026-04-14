package tw.gov.moda.digitalwallet.ui.create.guideline

import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.ui.adapter.GuidelineAdapter
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.databinding.FragmentGuidelineBinding


/**
 * 教學導覽頁
 */
@AndroidEntryPoint
class GuidelineFragment : BaseFragment<FragmentGuidelineBinding>() {

    companion object {
        fun newInstance() = GuidelineFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentGuidelineBinding = FragmentGuidelineBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: GuidelineViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        binding.viewPager.adapter = GuidelineAdapter().apply { submitList(listOf(0, 1, 2)) }
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        binding.imgLeft.setOnClickListener {
            mViewModel.switchPage(binding.viewPager.currentItem, -1)
        }
        binding.imgRight.setOnClickListener {
            mViewModel.switchPage(binding.viewPager.currentItem, 1)
        }

        binding.viewPager.setPageTransformer { page, position ->
            if (position <= -1.0F || position >= 1.0F) {
                page.setTranslationX(page.getWidth() * position);
                page.setAlpha(0.0F);
            } else if (position == 0.0F) {
                page.setTranslationX(page.getWidth() * position);
                page.setAlpha(1.0F);
            } else {
                page.setTranslationX(page.getWidth() * -position);
                page.setAlpha(1.0F - Math.abs(position));
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            private var mPosition = -1
            private var mIsChangedPage = false

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mIsChangedPage = true
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == 0) {
                    mViewModel.scrollPage(mPosition, mIsChangedPage)
                } else if (state == 1) {
                    mIsChangedPage = false
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                mPosition = position
            }
        })
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.indexPage.observe(viewLifecycleOwner) { index ->
            binding.viewPager.setCurrentItem(index, true)
        }
        mViewModel.launchContractFragment.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                mActivityViewModel.pageController.launchContractFragment()
            }
        }
        mViewModel.backStack.observe(viewLifecycleOwner) { back ->
            if (back) {
                mActivityViewModel.pageController.popBackStack()
            }
        }
    }
}