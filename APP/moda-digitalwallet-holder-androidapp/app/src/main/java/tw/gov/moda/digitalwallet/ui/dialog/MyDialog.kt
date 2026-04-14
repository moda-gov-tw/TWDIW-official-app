package tw.gov.moda.digitalwallet.ui.dialog

import android.app.Activity
import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import kotlinx.parcelize.Parcelize
import tw.gov.moda.digitalwallet.ui.base.BaseDialog
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.DialogMessageBinding

class MyDialog : BaseDialog<DialogMessageBinding>() {
    companion object {
        const val TAG = "MyDialog"
        private const val INTENT_DATA_CONFIG = "INTENT_DATA_CONFIG"
        private fun newInstance(data: DialogConfig): MyDialog {
            val dialog = MyDialog()
            dialog.arguments = bundleOf(INTENT_DATA_CONFIG to data)
            return dialog
        }
    }

    private var mCustomView: View? = null
    private var rightButtonClickListener: (() -> Unit)? = null
    private var leftButtonClickListener: (() -> Unit)? = null
    private var closeButtonClickListener: (() -> Unit)? = null
    private var onShowListener: (() -> Unit)? = null

    override fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?): DialogMessageBinding = DialogMessageBinding.inflate(inflater, container, false)

    override fun getViewModel(): BaseViewModel? = mViewModel

    private var mViewModel: BaseViewModel? = null

    override fun getTheme(): Int {
        return R.style.CustomDialog_New
    }

    override fun initView() {
        super.initView()
        arguments?.let { BundleCompat.getParcelable(it, INTENT_DATA_CONFIG, DialogConfig::class.java) }?.also { config ->
            binding.tvTitle.text = config.title ?: ""
            binding.tvTitle.isVisible = !config.title.isNullOrBlank()
            binding.viewLine.isVisible = !config.title.isNullOrBlank()

            if (config.text == null) {
                binding.tvMessage.text = config.message ?: ""
                binding.tvMessage.isVisible = !config.message.isNullOrBlank()
            } else {
                binding.tvMessage.setText(config.text, config.type)
                binding.tvMessage.isVisible = !config.text.isNullOrBlank()
            }

            isCancelable = config.isCancelable


            binding.tvRight.text = config.rightButtonText ?: ""
            binding.tvRight.isVisible = !config.rightButtonText.isNullOrBlank()
            binding.tvRight.setOnClickListener {
                dismiss()
                rightButtonClickListener?.invoke()
            }

            binding.tvLeft.text = config.leftButtonText ?: ""
            binding.tvLeft.isVisible = !config.leftButtonText.isNullOrBlank()
            binding.tvLeft.setOnClickListener {
                dismiss()
                leftButtonClickListener?.invoke()
            }
        } ?: dismiss()

        dialog?.setOnShowListener { onShowListener?.invoke() }

        binding.viewCustom.isVisible = mCustomView != null
        mCustomView?.also { customView ->
            (customView.parent as? ViewGroup)?.also { viewParent ->
                viewParent.removeAllViews()
            }
            binding.viewCustom.removeAllViews()
            binding.viewCustom.addView(customView)
        }
    }

    public fun setViewModel(viewModel: BaseViewModel) {
        mViewModel = viewModel
    }

    private fun setRightButtonClickListener(listener: (() -> Unit)?) {
        rightButtonClickListener = listener
    }

    private fun setLeftButtonClickListener(listener: (() -> Unit)?) {
        leftButtonClickListener = listener
    }

    private fun setCloseButtonClickListener(listener: (() -> Unit)?) {
        closeButtonClickListener = listener
    }

    private fun setOnShowListener(listener: (() -> Unit)?) {
        onShowListener = listener
    }

    private fun setCustomView(view: View?) {
        mCustomView = view
    }

    open class Builder(private val context: Context) {
        private val mDialogConfig = DialogConfig()
        private var rightButtonClickListener: (() -> Unit)? = null
        private var leftButtonClickListener: (() -> Unit)? = null
        private var closeButtonClickListener: (() -> Unit)? = null
        private var customView: View? = null
        private var onShowListener: (() -> Unit)? = null

        fun setTitle(title: String?): Builder {
            mDialogConfig.title = title
            return this
        }

        fun setTitle(@StringRes resId: Int): Builder {
            mDialogConfig.title = context.getString(resId)
            return this
        }

        fun setMessage(message: String?): Builder {
            mDialogConfig.message = message
            return this
        }

        fun setMessage(text: CharSequence, type: TextView.BufferType): Builder {
            mDialogConfig.text = text
            mDialogConfig.type = type
            return this
        }

        fun setMessage(@StringRes resId: Int): Builder {
            mDialogConfig.message = context.getString(resId)
            return this
        }

        fun setRightButtonText(rightButtonText: String?, listener: (() -> Unit)? = null): Builder {
            mDialogConfig.rightButtonText = rightButtonText
            this.rightButtonClickListener = listener
            return this
        }

        fun setRightButtonText(@StringRes resId: Int, listener: (() -> Unit)? = null): Builder {
            mDialogConfig.rightButtonText = context.getString(resId)
            this.rightButtonClickListener = listener
            return this
        }

        fun setLeftButtonText(leftButtonText: String?, listener: (() -> Unit)? = null): Builder {
            mDialogConfig.leftButtonText = leftButtonText
            this.leftButtonClickListener = listener
            return this
        }

        fun setLeftButtonText(@StringRes resId: Int, listener: (() -> Unit)? = null): Builder {
            mDialogConfig.leftButtonText = context.getString(resId)
            this.leftButtonClickListener = listener
            return this
        }

        fun setCloseClickListener(listener: (() -> Unit)?): Builder {
            this.closeButtonClickListener = listener
            return this
        }

        fun setRightClickListener(listener: (() -> Unit)?): Builder {
            this.rightButtonClickListener = listener
            return this
        }

        fun setLeftClickListener(listener: (() -> Unit)?): Builder {
            this.leftButtonClickListener = listener
            return this
        }

        fun setOnShowListener(listener: (() -> Unit)?): Builder {
            this.onShowListener = listener
            return this
        }

        fun setCacenlable(isCancelable: Boolean): Builder {
            mDialogConfig.isCancelable = isCancelable
            return this
        }

        fun setCustomView(view: View?): Builder {
            this.customView = view
            return this
        }

        fun create(): MyDialog {
            val dialog = newInstance(mDialogConfig)
            dialog.setRightButtonClickListener(rightButtonClickListener)
            dialog.setLeftButtonClickListener(leftButtonClickListener)
            dialog.setCloseButtonClickListener(closeButtonClickListener)
            dialog.setOnShowListener(onShowListener)
            dialog.setCustomView(customView)
            return dialog
        }

        fun show(fm: FragmentManager, tag: String): MyDialog {
            val dialog = create()
            if (context is Activity && !context.isFinishing) {
                dialog.show(fm, tag)
            } else {
                dialog.show(fm, tag)
            }
            return dialog
        }
    }

    @Parcelize
    private data class DialogConfig(
        var title: String? = null,
        var message: String? = null,
        var rightButtonText: String? = null,
        var leftButtonText: String? = null,
        var type: TextView.BufferType? = null,
        var text: CharSequence? = null,
        var isCancelable: Boolean = false
    ) : Parcelable

}