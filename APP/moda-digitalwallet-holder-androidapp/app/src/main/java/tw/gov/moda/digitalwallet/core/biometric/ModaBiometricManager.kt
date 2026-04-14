package tw.gov.moda.digitalwallet.core.biometric

import androidx.fragment.app.FragmentActivity
import javax.crypto.Cipher

/**
 * 生物辨識管理器
 */
interface ModaBiometricManager {
    interface OnBiometricCallback {
        fun onSuccess(cipher: Cipher?)

        fun onFail()

        fun onReset()

        fun onLock()
    }
    fun canEnableBiometric(): Int

    fun register(activity: FragmentActivity, callback: OnBiometricCallback)

    fun authenticate(activity: FragmentActivity, iv: ByteArray, callback: OnBiometricCallback)

}