package tw.gov.moda.digitalwallet.extension

import android.util.Base64
import java.security.MessageDigest

fun ByteArray.base64(): String {
    return Base64.encodeToString(this, Base64.NO_WRAP)
}

fun ByteArray.sha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    md.update(this)
    val digest = md.digest()
    return digest.hex()
}

fun ByteArray.hex(): String {
    val sb: StringBuilder = StringBuilder(this.size * 2)
    for (b in this) sb.append(String.format("%02x", b))
    return sb.toString()
}