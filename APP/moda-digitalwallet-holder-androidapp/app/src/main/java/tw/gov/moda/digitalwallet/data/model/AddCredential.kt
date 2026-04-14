package tw.gov.moda.digitalwallet.data.model

data class AddCredential(
    val vcItems: List<VCItem>?,
    val currentPage: Int?,
    val pageSize: Int?,
    val totalItems: Int?,
    val totalPages: Int?
) {
    data class VCItem(
        val vcUid: String?,
        val name: String?,
        val type: Int?,
        val issuerServiceUrl: String?,
        val logoUrl: String?
    )
}

