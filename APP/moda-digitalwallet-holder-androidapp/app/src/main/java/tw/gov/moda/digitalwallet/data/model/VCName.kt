package tw.gov.moda.digitalwallet.data.model

import com.google.gson.annotations.SerializedName

data class VCName(
    @SerializedName("org_tw_name")
    val orgTwName: String?,
    @SerializedName("vc_name")
    val vcName: String?,
    @SerializedName("issuer_url")
    val issuerUrl: String?,
)