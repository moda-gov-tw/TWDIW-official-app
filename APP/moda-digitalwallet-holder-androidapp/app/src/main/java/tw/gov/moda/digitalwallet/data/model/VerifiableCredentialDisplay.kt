package tw.gov.moda.digitalwallet.data.model

import com.google.gson.annotations.SerializedName

data class VerifiableCredentialDisplay(
    @SerializedName("title")
    val title: String,
    @SerializedName("value")
    var value: String
)
