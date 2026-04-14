package tw.gov.moda.digitalwallet.data.model

import com.google.gson.annotations.SerializedName

data class Purpose(
    @SerializedName("client")
    val client: String?,
    @SerializedName("terms_uri")
    val termsUri: String?,
    @SerializedName("scenario")
    val scenario: String?,
    @SerializedName("purpose")
    val purpose: String?
)