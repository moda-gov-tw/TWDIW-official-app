package tw.gov.moda.digitalwallet.data.model.dwmoda

import com.google.gson.annotations.SerializedName

class DwModa401i {
    companion object {
        const val URL_PATH = "/api/moda/dwapp/offline/vpList"
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
        @SerializedName("vpItems")
        val vpItems: List<VPItem>?,
        @SerializedName("currentPage")
        val currentPage: Int?,
        @SerializedName("pageSize")
        val pageSize: Int?,
        @SerializedName("totalItems")
        val totalItems: Int?,
        @SerializedName("totalPages")
        val totalPages: Int?,
    ) {
        data class VPItem(
            @SerializedName("vpUid")
            val vpUid: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("verifierModuleUrl")
            val verifierModuleUrl: String?,
            @SerializedName("logoUrl")
            val logoUrl: String?
        )
    }
}