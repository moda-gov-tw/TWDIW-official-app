package tw.gov.moda.digitalwallet.core.pref

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.core.keystore.ModaKeyStoreManager
import tw.gov.moda.digitalwallet.extension.base64
import java.security.SecureRandom
import javax.inject.Inject

/**
 * App 的 SharedPreferences
 *
 * @constructor [Context]
 */
class ModaSharedPreferences @Inject constructor(@ApplicationContext context: Context, modaKeyStoreManager: ModaKeyStoreManager) : SharedPreferencesDelegate(context, modaKeyStoreManager) {
    override fun getSharedPreferencesName(): String = AppConstants.PREF.TABLE_NAME

    var randomSalt by string()

    var issuerList by string()

    var verifiableCredentialList by string()

    var logDate by string()

    var skipGuideline by boolean()

    var openURL by string()

    var openURLTitle by string()

    var isDisplayedCreateWalletExplanation by boolean()

    var showCardStateDatetime by long()

    var defaultWalletId by long(0L)

    var dbName by string()

    var isMigrationDatabaseForRecords by boolean(false)

    fun getOrNewRandomSalt(): String {
        var newValue = randomSalt ?: ""
        if (newValue.isBlank()) {
            val random = SecureRandom()
            val bytes = ByteArray(20)
            random.nextBytes(bytes)
            newValue = bytes.base64()
            randomSalt = newValue
        }
        return newValue
    }
}