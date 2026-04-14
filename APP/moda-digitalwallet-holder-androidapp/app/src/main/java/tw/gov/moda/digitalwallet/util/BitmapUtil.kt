package tw.gov.moda.digitalwallet.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import java.io.ByteArrayOutputStream


object BitmapUtil {
    @JvmStatic
    fun bitmap2Base64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    @Throws(IllegalArgumentException::class)
    @JvmStatic
    fun createBitmap(base64: String): Bitmap? {
        if (base64.isBlank()) {
            return null
        }
        val decodedString: ByteArray = Base64.decode(base64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        return bitmap
    }

    @JvmStatic
    fun compress(originBitmap: Bitmap, size: Int): Bitmap {
        var bitmap: Bitmap = originBitmap
        while (bitmap.allocationByteCount > size) {
            bitmap = compressByScale(bitmap, 0.8f)
        }
        return bitmap
    }

    /**
     * 依照比例壓縮Bitmap
     *
     * @param bitmap Bitmap
     * @param scale  比例
     * @return Bitmap
     */
    @JvmStatic
    fun compressByScale(bitmap: Bitmap, scale: Float): Bitmap {
        val matrix = Matrix()
        matrix.setScale(scale, scale)
        val newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return newBitmap
    }

}
