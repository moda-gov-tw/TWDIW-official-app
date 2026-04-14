package tw.gov.moda.digitalwallet.core.resource

import android.app.KeyguardManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import tw.gov.moda.digitalwallet.common.AppConstants

/**
 * 資源提供器
 *
 * 負責注入Resource
 *
 * @constructor [Context]
 */
class ResourceProviderImpl constructor(val mContext: Context) : ResourceProvider {

    /**
     * 取得字串
     */
    override fun getString(resId: Int): String {
        return mContext.getString(resId)
    }

    /**
     * 檢查設備是否支援裝置安全鎖
     *
     * @return 是否支援
     */
    override fun isDeviceSecure(): Boolean {
        val keyguardManager = mContext.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        return if (keyguardManager != null) {
            return keyguardManager.isDeviceSecure && keyguardManager.isKeyguardSecure
        } else {
            false
        }
    }

    /**
     * 檢查設備 API 是否高於或等於 30
     *
     * @return 是否高於或等於
     */
    override fun isAboveOrEqualAPI30(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    /**
     * 檢查手機是否透過行動網路或 Wi-Fi 連上網路
     *
     * @return true 表示有網路連線 (行動網路或 Wi-Fi)
     */
    override fun isConnectedToInternet(): Boolean {
        val connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        // 有網路能力，且是 Wi-Fi 或行動數據連線
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    override fun deleteDatabase() {
        mContext.deleteDatabase(AppConstants.Database.NAME)
    }

    override fun existDatavase(dbName: String): Boolean {
        val dbFile = mContext.getDatabasePath(dbName)
        return dbFile.exists()
    }

}