package tw.gov.moda.digitalwallet.exception

data class SDKException(val msg: String, val returnCode: String? = null) : Exception()