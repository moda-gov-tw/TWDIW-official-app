package tw.gov.moda.digitalwallet.data.model.dwmoda

import com.google.gson.annotations.SerializedName

class DwModa301i {
    companion object {
        const val URL_PATH = "/api/moda/dwapp/apply/vcList"
    }

    data class Request(
        @SerializedName("page")
        val page: Int?,
        @SerializedName("size")
        val size: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("sort")
        val sort: String?
    )

    data class Response(
        @SerializedName("vcItems")
        val vcItems: List<VCItem>?,
        @SerializedName("currentPage")
        val currentPage: Int?,
        @SerializedName("pageSize")
        val pageSize: Int?,
        @SerializedName("totalItems")
        val totalItems: Int?,
        @SerializedName("totalPages")
        val totalPages: Int?,
    ) {
        data class VCItem(
            @SerializedName("vcUid")
            val vcUid: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("type")
            val type: Int?,
            @SerializedName("issuerServiceUrl")
            val issuerServiceUrl: String?,
            @SerializedName("logoUrl")
            val logoUrl: String?
        )
    }
}