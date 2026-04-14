package tw.gov.moda.digitalwallet.data.model

import com.google.gson.annotations.SerializedName

data class Description(
    @SerializedName("issuer_url")
    val issuerUrl: String?,
    @SerializedName("ial")
    val ial: String?
)
