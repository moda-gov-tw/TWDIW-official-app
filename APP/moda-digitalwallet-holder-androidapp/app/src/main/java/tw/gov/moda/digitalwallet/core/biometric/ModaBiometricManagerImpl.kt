package tw.gov.moda.digitalwallet.core.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import tw.gov.moda.digitalwallet.core.keystore.ModaKeyStoreManager
import tw.gov.moda.diw.R
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.NoSuchPaddingException
import javax.inject.Inject

/**
 * 生物辨識管理器
 *
 * @constructor [Context]
 */
class ModaBiometricManagerImpl @Inject constructor(
    @ApplicationContext val mContext: Context,
    val mKeyStoreManager: ModaKeyStoreManager
) : ModaBiometricManager {


    /**
     * 判斷是否可以使用生物辨識
     *
     * @return [BiometricManager.BIOMETRIC_SUCCESS]
     */
    override fun canEnableBiometric(): Int {
        val biometricManager: BiometricManager = BiometricManager.from(mContext)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
    }

    /**
     * 建立加密用生物辨識
     */
    override fun register(activity: FragmentActivity, callback: ModaBiometricManager.OnBiometricCallback) {
        mKeyStoreManager.initSecretKey()

        val biometricPrompt: BiometricPrompt? = show(activity, callback)
        if (biometricPrompt == null) {
            callback.onFail()
            return
        }

        val promptInfo = PromptInfo.Builder()
            .setTitle(activity.getString(R.string.msg_register_biometric_title))
            .setSubtitle(activity.getString(R.string.msg_register_biometric_content))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .setConfirmationRequired(true)
            .build()


        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: NoSuchPaddingException) {
            callback.onFail()
        } catch (e: NoSuchAlgorithmException) {
            callback.onFail()
        } catch (e: InvalidKeyException) {
            callback.onReset()
            mKeyStoreManager.reset()
        }
    }

    /**
     * 建立解密用生物辨識
     */
    override fun authenticate(activity: FragmentActivity, iv: ByteArray, callback: ModaBiometricManager.OnBiometricCallback) {
        val biometricPrompt: BiometricPrompt? = show(activity, callback)
        if (biometricPrompt == null) {
            callback.onFail()
            return
        }

        val promptInfo = PromptInfo.Builder()
            .setTitle(activity.getString(R.string.msg_login_biometric_title))
            .setSubtitle(activity.getString(R.string.msg_login_biometric_content))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .setConfirmationRequired(true)
            .build()

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: NoSuchPaddingException) {
            callback.onFail()
        } catch (e: NoSuchAlgorithmException) {
            callback.onFail()
        } catch (e: InvalidKeyException) {
            callback.onReset()
            mKeyStoreManager.reset()
        }
    }

    /**
     * 顯示生物辨識視窗
     */
    private fun show(activity: FragmentActivity?, callback: ModaBiometricManager.OnBiometricCallback): BiometricPrompt? {
        if (activity != null && mKeyStoreManager.hasSecretKey()) {
            val biometricPrompt = BiometricPrompt(activity,
                ContextCompat.getMainExecutor(activity), object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        if (errorCode == BiometricPrompt.ERROR_CANCELED) {
                        } else if (errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                        } else if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        } else if (errorCode == BiometricPrompt.ERROR_LOCKOUT || errorCode == BiometricPrompt.ERROR_LOCKOUT_PERMANENT) {
                            callback.onLock()
                        } else {
                            callback.onFail()
                        }
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        callback.onSuccess(null)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        callback.onFail()
                    }
                })

            return biometricPrompt
        }
        return null
    }
}