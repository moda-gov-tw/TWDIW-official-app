package tw.gov.moda.digitalwallet.extension

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.addItemDecoration(@DrawableRes drawableRes: Int): RecyclerView {
    this.addItemDecoration(object : RecyclerView.ItemDecoration() {
        override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            val divider = AppCompatResources.getDrawable(parent.context, drawableRes) ?: return
            val childCount = parent.childCount
            for (i in 0 until childCount - 1) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as? RecyclerView.LayoutParams ?: return
                val top = child.bottom + params.bottomMargin
                val bottom = top + divider.intrinsicHeight
                val left = parent.paddingLeft
                val right = parent.width - parent.paddingRight
                divider.setBounds(left, top, right, bottom)
                divider.draw(canvas)
            }
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val divider = AppCompatResources.getDrawable(parent.context, drawableRes)
            divider ?: return
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.top = divider.intrinsicHeight
            }
        }
    })
    return this
}