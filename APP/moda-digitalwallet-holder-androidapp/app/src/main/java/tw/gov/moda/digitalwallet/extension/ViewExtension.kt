package tw.gov.moda.digitalwallet.extension

import android.view.View
import tw.gov.moda.digitalwallet.util.OnAntiStickClickLisener

fun View.setOnAntiStickClickLisener(listener: (View) -> Unit) {
    setOnClickListener(OnAntiStickClickLisener(listener))
}