package tw.gov.moda.digitalwallet.core.resource

import androidx.annotation.StringRes

/**
 * 資源提供器
 */
interface ResourceProvider {
    fun getString(@StringRes resId: Int): String

    fun isDeviceSecure(): Boolean

    fun isAboveOrEqualAPI30(): Boolean

    fun isConnectedToInternet(): Boolean

    fun deleteDatabase()

    fun existDatavase(dbName: String): Boolean
}