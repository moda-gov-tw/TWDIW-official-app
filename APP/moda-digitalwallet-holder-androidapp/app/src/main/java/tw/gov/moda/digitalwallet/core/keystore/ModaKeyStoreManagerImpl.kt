package tw.gov.moda.digitalwallet.core.keystore

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.StrongBoxUnavailableException
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.GeneralSecurityException
import java.security.InvalidAlgorithmParameterException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.ProviderException
import java.security.UnrecoverableEntryException
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.net.ssl.KeyStoreBuilderParameters


/**
 * 金鑰庫(KeyStore)管理器
 *
 * @constructor [Context]
 */
class ModaKeyStoreManagerImpl(@ApplicationContext val mContext: Context) : ModaKeyStoreManager {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_NAME = "MODA_DIGITAL_BIOMETRIC"

        private const val ENCRYPTION_BLOCK_MODE: String = KeyProperties.BLOCK_MODE_GCM
        private const val ENCRYPTION_PADDING: String = KeyProperties.ENCRYPTION_PADDING_NONE
        private const val ENCRYPTION_ALGORITHM: String = KeyProperties.KEY_ALGORITHM_AES
        private const val TRANSFORMATION: String = "$ENCRYPTION_ALGORITHM/$ENCRYPTION_BLOCK_MODE/$ENCRYPTION_PADDING"
    }

    private var mSecretKey: SecretKey? = null
    private val mKeyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE)

    private var mIsStrongBox = true
    private var mIsValidationByGeneratorKey = false

    init {
        mKeyStore.load(null)
    }


    /**
     * 初始化金鑰
     * 演算法：AES256/GCM/NOPADDING
     *
     * @param
     * @throws [NoSuchAlgorithmException], [NoSuchProviderException], [InvalidAlgorithmParameterException]
     */
    @Throws(NoSuchAlgorithmException::class, NoSuchProviderException::class, InvalidAlgorithmParameterException::class)
    override fun initSecretKey() {
        mSecretKey = getKey()
        if (mSecretKey == null) {
            mSecretKey = generateSecretKey(mIsStrongBox)
        }
    }

    /**
     * 檢測是否支援StrongBox
     */
    override fun detectHardwareSecurity(): Boolean {
        if (this.mIsValidationByGeneratorKey) {
            return this.mIsStrongBox
        }

        // 驗證是否支援 StrongBoxBacked
        mIsStrongBox = mContext.packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_STRONGBOX_KEYSTORE)
        if (mIsStrongBox) {
            try {
                // 因系統差異，需透過產生金鑰方式驗證 StrongBoxBacked 是否有問題。
                val keyGenParameterSpec = KeyGenParameterSpec.Builder(KEY_NAME + UUID.randomUUID().toString(), KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(ENCRYPTION_BLOCK_MODE)
                    .setEncryptionPaddings(ENCRYPTION_PADDING)
                    .setIsStrongBoxBacked(true)
                    .setRandomizedEncryptionRequired(true)
                    .setKeySize(256)
                    .build()

                generateKey(keyGenParameterSpec)
            } catch (e: ProviderException) {
                mIsStrongBox = false
            }
        }
        // 標注已驗證硬體功能
        this.mIsValidationByGeneratorKey = true
        return mIsStrongBox
    }

    override fun hasSecretKey(): Boolean {
        return mSecretKey != null
    }


    @Throws(InvalidAlgorithmParameterException::class, NoSuchAlgorithmException::class, NoSuchProviderException::class, StrongBoxUnavailableException::class)
    override fun reset() {
        mSecretKey = generateSecretKey(mIsStrongBox)
    }

    override fun getEncryptCipher(): Cipher {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, mSecretKey)
        return cipher
    }

    override fun getDecryptCipher(iv: ByteArray): Cipher {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, mSecretKey, GCMParameterSpec(128, iv))
        return cipher
    }

    @Throws(InvalidAlgorithmParameterException::class, NoSuchAlgorithmException::class, NoSuchProviderException::class, StrongBoxUnavailableException::class)
    private fun generateSecretKey(isStrongBox: Boolean): SecretKey {
        val builder = KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(ENCRYPTION_BLOCK_MODE)
            .setEncryptionPaddings(ENCRYPTION_PADDING)
            .setIsStrongBoxBacked(isStrongBox)
            .setRandomizedEncryptionRequired(true)
            .setKeySize(256)
        
        val keyGenParameterSpec = builder.build()

        return generateKey(keyGenParameterSpec)
    }

    private fun getKey(): SecretKey? {
        if (mSecretKey == null) {
            mSecretKey = try {
                mKeyStore.getEntry(KEY_NAME, null)?.let { it as? KeyStore.SecretKeyEntry }?.secretKey
            } catch (e: KeyStoreException) {
                null
            } catch (e: NoSuchAlgorithmException) {
                null
            } catch (e: UnrecoverableEntryException) {
                null
            }
        }
        return mSecretKey
    }

    @Throws(InvalidAlgorithmParameterException::class, NoSuchAlgorithmException::class, NoSuchProviderException::class, StrongBoxUnavailableException::class)
    private fun generateKey(keyGenParameterSpec: KeyGenParameterSpec): SecretKey {
        val keyGenerator: KeyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM, ANDROID_KEYSTORE)
        keyGenerator.init(keyGenParameterSpec)
        val secretKey = keyGenerator.generateKey()
        return secretKey
    }
}