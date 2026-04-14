package tw.gov.moda.digitalwallet.extension

import android.content.Context
import androidx.fragment.app.FragmentManager
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.ui.dialog.MyDialog
import tw.gov.moda.diw.R

fun Context.showMessageDialog(fragmentManager: FragmentManager, text: String, action: (() -> Unit)? = null) {
    MyDialog.Builder(this)
        .setCacenlable(false)
        .setTitle(R.string.prompt_message)
        .setMessage(text)
        .setRightButtonText(getString(R.string.confirm)) {
            action?.invoke()
        }
        .show(fragmentManager, AppConstants.Dialog.MESSAGE_DIALOG)
}

fun Context.showSDKMessageDialog(fragmentManager: FragmentManager, text: String, action: (() -> Unit)? = null) {
    MyDialog.Builder(this)
        .setCacenlable(false)
        .setTitle(R.string.error_message)
        .setMessage(text)
        .setRightButtonText(getString(R.string.confirm)) {
            action?.invoke()
        }
        .show(fragmentManager, AppConstants.Dialog.SDK_MESSAGE_DIALOG)
}

fun Context.showNetworkErrorDialog(fragmentManager: FragmentManager, action: (() -> Unit)? = null) {
    MyDialog.Builder(this)
        .setTitle(getString(R.string.connection_error))
        .setMessage(getString(R.string.connection_error_message))
        .setRightButtonText(getString(R.string.confirm)) {
            action?.invoke()
        }
        .show(fragmentManager, AppConstants.Dialog.NETWORK_ERROR_DIALOG)
}