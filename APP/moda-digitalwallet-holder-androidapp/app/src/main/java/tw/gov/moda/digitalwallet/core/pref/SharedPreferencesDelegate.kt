package tw.gov.moda.digitalwallet.core.pref

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import tw.gov.moda.digitalwallet.core.keystore.ModaKeyStoreManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * SharedPreferences 的代理器
 */
abstract class SharedPreferencesDelegate(context: Context, modaKeyStoreManager: ModaKeyStoreManager) {
    val isStrongBox = modaKeyStoreManager.detectHardwareSecurity()
    private val masterKey: MasterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .setRequestStrongBoxBacked(isStrongBox)
        .build()

    /**
     * 初始化 preferences
     */
    private val preferences: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            getSharedPreferencesName(),
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }


    /**
     * 儲存 string 型態
     */
    fun string(defaultValue: String? = null) = object : ReadWriteProperty<Any, String?> {
        override fun getValue(thisRef: Any, property: KProperty<*>): String? {
            return preferences.getString(property.name, defaultValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
            val isSuccessful = preferences.edit().putString(property.name, value).commit()
            if (!isSuccessful) {
                throw RuntimeException("SharedPreferences setValue Fail.")
            }
        }
    }

    /**
     * 儲存 boolean 型態
     */
    fun boolean(defaultValue: Boolean = false) = object : ReadWriteProperty<Any, Boolean> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
            return preferences.getBoolean(property.name, defaultValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
            val isSuccessful = preferences.edit().putBoolean(property.name, value).commit()
            if (!isSuccessful) {
                throw RuntimeException("SharedPreferences setValue Fail.")
            }
        }
    }

    /**
     * 儲存 long 型態
     */
    fun long(defaultValue: Long = 0L) = object : ReadWriteProperty<Any, Long> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Long {
            return preferences.getLong(property.name, defaultValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
            val isSuccessful = preferences.edit().putLong(property.name, value).commit()
            if (!isSuccessful) {
                throw RuntimeException("SharedPreferences setValue Fail.")
            }
        }
    }

    /**
     * 設定 Preferences Name
     */
    abstract fun getSharedPreferencesName(): String

    fun getSharedPreferences(): SharedPreferences = preferences


    fun clearAll() {
        val isSuccessful = preferences.edit().clear().commit()

        if (!isSuccessful) {
            throw RuntimeException("SharedPreferences clear Fail.")
        }
    }
}