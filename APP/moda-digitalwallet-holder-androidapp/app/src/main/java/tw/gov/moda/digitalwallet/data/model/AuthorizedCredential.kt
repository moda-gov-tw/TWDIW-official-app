package tw.gov.moda.digitalwallet.data.model

import com.google.gson.annotations.SerializedName

data class AuthorizedCredential(
    @SerializedName("card")
    val card: String,
    @SerializedName("orgName")
    val orgName: String,
    @SerializedName("vcName")
    val vcName: String,
    @SerializedName("issuerUrl")
    val issuerUrl: String
)
