package tw.gov.moda.digitalwallet.extension

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import java.nio.CharBuffer
import java.nio.charset.Charset

fun String.getBytes(): ByteArray? {
    val data = this.toCharArray()
    if (data.size != 0) {
        val charBuffer = CharBuffer.wrap(data)
        val byteBuffer = Charset.forName("UTF-8").encode(charBuffer)
        val result = ByteArray(byteBuffer.limit())
        byteBuffer[result]
        return result
    } else {
        return null
    }
}

fun String.sha256(): String {
    return try {
        this.toByteArray(Charsets.UTF_8).sha256()
    } catch (ex: java.lang.Exception) {
        ""
    }
}


fun String.fromHex(): ByteArray {
    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}

fun String.getColorSpannable(color: Int, content: String): Spannable {
    val indexStart = this.indexOf(content)
    val indexEnd = indexStart + content.length
    return getColorSpannable(color, indexStart, indexEnd, content)
}

fun String.getColorSpannable(color: Int, start: Int, end: Int, content: String): Spannable {
    val spannable: Spannable = SpannableString(this)
    if (this.contains(content)) {
        spannable.setSpan(
            ForegroundColorSpan(color),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return spannable
}

fun Spannable.setBold(content: String): Spannable {
    val indexStart = this.toString().indexOf(content)
    val indexEnd = indexStart + content.length
    if (this.contains(content)) {
        this.setSpan(
            StyleSpan(Typeface.BOLD),
            indexStart,
            indexEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return this
}
