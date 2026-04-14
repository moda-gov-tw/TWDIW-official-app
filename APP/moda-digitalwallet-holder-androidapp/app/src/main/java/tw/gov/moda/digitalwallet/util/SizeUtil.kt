package tw.gov.moda.digitalwallet.util

import android.content.Context

object SizeUtil {
    fun dpToPx(context: Context, dpValue: Float): Int {
        val scale = context.getResources().getDisplayMetrics().density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun pxTodp(context: Context, pxValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (pxValue - 0.5f) / scale
    }
}