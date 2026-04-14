package tw.gov.moda.digitalwallet.data.model

import com.google.gson.annotations.SerializedName

data class VerifiablePresentationResultData(
    @SerializedName("customData")
    var customData: List<RequireVerifiablePresentationField>?,
    @SerializedName("resultData")
    var resultData: List<RequireVerifiableCredentialGroup>?,
    @SerializedName("datetime")
    var datetime: Long?,
    @SerializedName("purpose")
    var unit: String?,
)
