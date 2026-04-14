package tw.gov.moda.digitalwallet.data.model

import com.google.gson.annotations.SerializedName

data class VerifiableCredentialCheck(
    @SerializedName("title")
    val title: String,
    @SerializedName("field")
    val field: String,
    @SerializedName("isChecked")
    var isChecked: Boolean
)