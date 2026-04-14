package tw.gov.moda.digitalwallet.data.model

import com.google.gson.annotations.SerializedName
import tw.gov.moda.digitalwallet.data.db.VerifiableCredential
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk401i

data class RequireVerifiableCredential(
    @SerializedName("title")
    val title: String,
    @SerializedName("value")
    val value: String,
    @SerializedName("verifiableCredential")
    val verifiableCredential: VerifiableCredential,
    @SerializedName("requireData")
    val requireData: Dwsdk401i.Response.RequestDataGroup.RequestData,
    @SerializedName("isSelectedRequireFields")
    var isSelectedRequireFields: Boolean,
)
