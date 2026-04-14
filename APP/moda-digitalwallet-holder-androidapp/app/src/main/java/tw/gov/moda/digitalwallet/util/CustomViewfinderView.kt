package tw.gov.moda.digitalwallet.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRectF
import com.journeyapps.barcodescanner.Size
import com.journeyapps.barcodescanner.ViewfinderView
import tw.gov.moda.diw.R


class CustomViewfinderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ViewfinderView(context, attrs) {

    private val maskPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.mask) // 半透明黑色
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }

    private val cornerRadius = dpToPx(24f)

    private var mAnchorView: View? = null

    fun setAnchorView(view: View) {
        this.mAnchorView = view
        invalidate()
    }

    override fun refreshSizes() {
        super.refreshSizes()
        framingRect = Rect(0, 0, width, height)
        previewSize = Size(width, height)
        cameraPreview.marginFraction = 0.0
        laserVisibility = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 建立一個透明圖層，避免把整個畫布清掉
        val saveCount = canvas.saveLayer(null, null)

        // 先整個畫黑
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), maskPaint)

        mAnchorView?.also {
            // 中心區域
            val rect = RectF(it.left.toFloat() + dpToPx(4f), it.top.toFloat() + dpToPx(4f), it.right.toFloat() - dpToPx(4f), it.bottom.toFloat() - dpToPx(4f))

            // 清除中間的圓角區域
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, clearPaint)
        }

        canvas.restoreToCount(saveCount)
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }
}
