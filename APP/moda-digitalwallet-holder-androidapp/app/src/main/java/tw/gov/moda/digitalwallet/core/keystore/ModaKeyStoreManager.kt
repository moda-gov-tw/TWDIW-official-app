package tw.gov.moda.digitalwallet.core.keystore

import android.security.keystore.StrongBoxUnavailableException
import java.security.InvalidAlgorithmParameterException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import javax.crypto.Cipher

/**
 * 金鑰庫(KeyStore)管理器
 */
interface ModaKeyStoreManager {

    fun detectHardwareSecurity(): Boolean

    fun hasSecretKey(): Boolean

    @Throws(InvalidAlgorithmParameterException::class, NoSuchAlgorithmException::class, NoSuchProviderException::class, StrongBoxUnavailableException::class)
    fun initSecretKey()

    @Throws(InvalidAlgorithmParameterException::class, NoSuchAlgorithmException::class, NoSuchProviderException::class, StrongBoxUnavailableException::class)
    fun reset()

    @Throws(InvalidAlgorithmParameterException::class, NoSuchAlgorithmException::class, NoSuchProviderException::class)
    fun getEncryptCipher(): Cipher

    @Throws(InvalidAlgorithmParameterException::class, NoSuchAlgorithmException::class, NoSuchProviderException::class)
    fun getDecryptCipher(iv: ByteArray): Cipher
}