package tw.gov.moda.digitalwallet.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import tw.gov.moda.digitalwallet.exception.SDKException
import tw.gov.moda.digitalwallet.extension.showMessageDialog
import tw.gov.moda.digitalwallet.extension.showSDKMessageDialog
import tw.gov.moda.digitalwallet.ui.main.MainActivity
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    companion object {
        private const val INSETS_TOP = "INSETS_TOP"
        private const val INSETS_BOTTOM = "INSETS_BOTTOM"
    }

    protected abstract fun initViewBinding(container: ViewGroup?): VB

    protected abstract fun getViewModel(): BaseViewModel?

    protected val binding get() = _binding!!

    private var _binding: VB? = null

    protected val mProgressDialog: Dialog?
        get() = (activity as? MainActivity)?.getProgressDialog()

    protected val mProgressDialogWhite: Dialog?
        get() = (activity as? MainActivity)?.getProgressDialogWhite()

    private var mInsetsTop = 0
    private var mInsetsBottom = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = initViewBinding(container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            mInsetsTop = savedInstanceState.getInt(INSETS_TOP)
            mInsetsBottom = savedInstanceState.getInt(INSETS_BOTTOM)
        }
        initView()
        subscribeObservers()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putAll(
            bundleOf(
                INSETS_TOP to mInsetsTop,
                INSETS_BOTTOM to mInsetsBottom
            )
        )

    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.mViewModel?.updateAutoLogoutTime()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (activity as? MainActivity)?.mViewModel?.updateAutoLogoutTime()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected open fun initView() {

    }

    protected open fun subscribeObservers() {
        getViewModel()?.alertMessage?.observe(viewLifecycleOwner) { resId ->
            if (resId != null && resId > 0) {
                mProgressDialog?.dismiss()
                context?.showMessageDialog(childFragmentManager, getString(resId))
            }
        }
        getViewModel()?.alertSDKMessage?.observe(viewLifecycleOwner) { message ->
            if (message != null && message.isNotBlank()) {
                mProgressDialog?.dismiss()
                context?.showSDKMessageDialog(childFragmentManager, message, getAlertMessageAction())
            }
        }
        getViewModel()?.progressBar?.observe(viewLifecycleOwner) { isShow ->
            if (isShow && activity?.isFinishing == false) {
                mProgressDialog?.show()
            } else {
                mProgressDialog?.dismiss()
            }
        }
        getViewModel()?.progressBarWhite?.observe(viewLifecycleOwner) { isShow ->
            if (isShow && activity?.isFinishing == false) {
                mProgressDialogWhite?.show()
            } else {
                mProgressDialogWhite?.dismiss()
            }
        }
        // 錯誤例外處理
        getViewModel()?.exception?.observe(viewLifecycleOwner) { exception ->
            if (exception is SDKException) {
                // SDK 錯誤
                context?.showMessageDialog(childFragmentManager, exception.msg, getExceptionAlertAction())
            } else {
                // 其他
                val msg = getString(R.string.msg_another_exception)
                context?.showMessageDialog(childFragmentManager, msg, getExceptionAlertAction())
            }
        }
    }

    protected fun setInsets(viewModel: MainViewModel, topView: View, bottomView: View) {
        viewModel.getInsets()?.also { insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            if (systemBars.top != 0) {
                topView.updateLayoutParams<ViewGroup.LayoutParams> {
                    height = systemBars.top
                }
                mInsetsTop = systemBars.top
            } else {
                topView.updateLayoutParams<ViewGroup.LayoutParams> {
                    height = mInsetsTop
                }
            }
            val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            if (navigationBars.bottom != 0) {
                bottomView.updateLayoutParams<ViewGroup.LayoutParams> {
                    height = navigationBars.bottom
                }
                mInsetsBottom = navigationBars.bottom
            } else {
                bottomView.updateLayoutParams<ViewGroup.LayoutParams> {
                    height = mInsetsBottom
                }
            }
        }
    }

    protected fun setToolbar(title: String, setLeft: ((ImageView) -> Unit)? = null, setRight: ((ImageView) -> Unit)? = null) {
        binding.root.findViewById<TextView>(R.id.tv_title).text = title
        setLeft?.invoke(binding.root.findViewById(R.id.img_left))
        setRight?.invoke(binding.root.findViewById(R.id.img_right))
    }

    protected open fun getExceptionAlertAction(): (() -> Unit)? {
        return {
            (activity as? MainActivity)?.mViewModel?.setLoginStatus(false)
            activity?.finish()
        }
    }

    protected open fun getAlertMessageAction(): (() -> Unit)? {
        return null
    }
}