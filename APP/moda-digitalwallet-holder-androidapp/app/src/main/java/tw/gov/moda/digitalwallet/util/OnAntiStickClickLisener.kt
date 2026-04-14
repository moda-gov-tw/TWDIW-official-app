package tw.gov.moda.digitalwallet.util

import android.util.Log
import android.view.View
import java.util.concurrent.atomic.AtomicBoolean

// 建立防止1秒內連點觸發機制
class OnAntiStickClickLisener(val onClickLisener: (View) -> Unit) : View.OnClickListener {
    private var pausePeriod = 0L
    private val isLocked = AtomicBoolean(false)
    override fun onClick(v: View) {
        if (!isLocked.compareAndSet(false, true)) return

        if (System.currentTimeMillis() - pausePeriod > 1_000L) {
            onClickLisener.invoke(v)
            pausePeriod = System.currentTimeMillis()
        }
        isLocked.set(false)
    }
}