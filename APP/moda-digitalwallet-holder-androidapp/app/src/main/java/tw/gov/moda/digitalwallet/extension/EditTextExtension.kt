package tw.gov.moda.digitalwallet.extension

import android.text.InputFilter
import android.text.Spanned
import android.text.method.PasswordTransformationMethod
import android.widget.EditText


/**
 * 變更隱碼字元
 */
fun EditText.transformationMethodByChar(char: Char) {
    transformationMethod = object : PasswordTransformationMethod() {
        override fun getTransformation(source: CharSequence, view: android.view.View): CharSequence {
            return PasswordCharSequence(source)
        }

        private inner class PasswordCharSequence(private val source: CharSequence) : CharSequence {
            override fun get(index: Int): Char {
                return char
            }

            override val length: Int
                get() = source.length

            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                return source.subSequence(startIndex, endIndex)
            }
        }
    }
}


fun EditText.filterByRegex(regex: Regex, allowChinese: Boolean = false, maxLength: Int = 0) {


    val charFilter = object : InputFilter {
        override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
            if (allowChinese) {

                if (source?.length == 1) {
                    var code = source.first().code
                    // 全形轉半形
                    if (code in 0xFF01..0xFF60 || code in 0xFFE0..0xFFE6) {
                        code = (code - 0xFEE0)
                    }

                    // 判斷字元 ASCII 表中就進行篩選過濾
                    if (code in 32..126) {
                        return source.replace(regex, "")
                    }


                }
                return source ?: ""
            } else {
                return source?.replace(regex, "") ?: ""
            }
        }
    }

    val newFilters = if (maxLength > 0) {
        arrayOf(charFilter, InputFilter.LengthFilter(maxLength))
    } else {
        arrayOf(charFilter)
    }

    filters = newFilters
}


fun EditText.setError(isError: Boolean) {
    this.isSelected = isError
}