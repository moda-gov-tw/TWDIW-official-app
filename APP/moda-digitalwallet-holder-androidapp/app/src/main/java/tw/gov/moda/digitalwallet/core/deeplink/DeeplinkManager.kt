package tw.gov.moda.digitalwallet.core.deeplink

interface DeeplinkManager {
    suspend fun parseDeeplink(deeplink: String)
}