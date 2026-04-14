package tw.gov.moda.digitalwallet.exception

data class IdentifierException(val code: String?, override val message: String?) : RuntimeException()