package tw.gov.moda.digitalwallet.data.model.dwmoda

import com.google.gson.annotations.SerializedName

class DwModa201i {
    companion object {
        const val URL_PATH = "/api/moda/dwapp/serviceUrl/"
    }

    data class Request(
        @SerializedName("mode")
        val mode: String
    )

    data class VPResponse(
        @SerializedName("mode")
        val mode: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("verifierServiceUrl")
        val verifierServiceUrl: String?,
        @SerializedName("logoUrl")
        val logoUrl: String?,
        @SerializedName("isStatic")
        val isStatic: String?,
        @SerializedName("isOffline")
        val isOffline: String?,
        @SerializedName("custom")
        val custom: VPCustom?
    )

    data class VPCustom(
        @SerializedName("fields")
        val fields: List<VPCustomField>?
    )

    data class VPCustomField(
        @SerializedName("cname")
        val cname: String?,
        @SerializedName("ename")
        val ename: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("value")
        val value: String?,
        @SerializedName("regex")
        val regex: String?,
    )

    data class VCResponse(
        @SerializedName("mode")
        val mode: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("issuerServiceUrl")
        val issuerServiceUrl: String?,
        @SerializedName("type")
        val type: Int?,
        @SerializedName("logoUrl")
        val logoUrl: String?
    )
}