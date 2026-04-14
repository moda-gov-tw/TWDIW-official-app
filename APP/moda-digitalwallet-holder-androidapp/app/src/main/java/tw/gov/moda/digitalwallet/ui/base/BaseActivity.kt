package tw.gov.moda.digitalwallet.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import tw.gov.moda.digitalwallet.ui.dialog.MyDialog
import tw.gov.moda.diw.R

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    protected abstract fun initViewBinding(): VB

    protected abstract fun getViewModel(): BaseViewModel

    protected val binding: VB by lazy { initViewBinding() }

    protected val mProgressDialog: Dialog by lazy { initProgressDialog() }

    private val mProgressDialogWhite: Dialog by lazy { initProgressDialogWhite() }

    private val mAlertObscuredDialog: MyDialog by lazy {
        initAlertObscuredDialog()
    }

    private var mIsShowObscuredDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        subscribeObservers()
    }

    fun getProgressDialog(): Dialog {
        return mProgressDialog
    }

    fun getProgressDialogWhite(): Dialog {
        return mProgressDialogWhite
    }

    fun setObscuredDetector(view: View) {
        // 只要有覆蓋層，這個 WebView 就不吃點擊（防 tapjacking）
        view.setFilterTouchesWhenObscured(true)


        // 進一步：偵測到覆蓋就攔截觸控並提示
        view.setOnTouchListener(OnTouchListener { v: View?, event: MotionEvent? ->
            val obscured = (event!!.getFlags() and MotionEvent.FLAG_WINDOW_IS_OBSCURED) != 0
                    || (event.getFlags() and MotionEvent.FLAG_WINDOW_IS_PARTIALLY_OBSCURED) != 0
            if (obscured && !mAlertObscuredDialog.isAdded && !mIsShowObscuredDialog) {
                mAlertObscuredDialog.show(supportFragmentManager, null)
                return@OnTouchListener true// 攔截此次觸控
            }
            false
        })
    }

    protected open fun initView() {

    }

    protected open fun subscribeObservers() {
        getViewModel().progressBar.observe(this) { isShow ->
            if (isShow && isFinishing == false) {
                mProgressDialog.show()
            } else {
                mProgressDialog.dismiss()
            }
        }
        getViewModel().progressBarWhite.observe(this) { isShow ->
            if (isShow && isFinishing == false) {
                mProgressDialogWhite.show()
            } else {
                mProgressDialogWhite.dismiss()
            }
        }
    }


    private fun initProgressDialog(): Dialog {
        val layout = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)
        val dialog = Dialog(this, R.style.Theme_Digitalwallet_Loading)
        dialog.setCancelable(false)
        dialog.setContentView(layout)
        return dialog
    }

    private fun initProgressDialogWhite(): Dialog {
        val layout = LayoutInflater.from(this).inflate(R.layout.dialog_loading_white, null)
        val dialog = Dialog(this, R.style.Theme_Digitalwallet_Loading)
        dialog.setCancelable(false)
        dialog.setContentView(layout)
        return dialog
    }

    private fun initAlertObscuredDialog(): MyDialog {
        return MyDialog.Builder(this)
            .setTitle(getString(R.string.use_remind))
            .setMessage(getString(R.string.msg_remind_obscured))
            .setCacenlable(false)
            .setRightButtonText(R.string.confirm, {
                mIsShowObscuredDialog = true
            })
            .create()
    }
}