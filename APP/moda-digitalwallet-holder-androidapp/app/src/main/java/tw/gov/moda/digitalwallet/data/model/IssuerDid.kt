package tw.gov.moda.digitalwallet.data.model

import com.google.gson.annotations.SerializedName
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk101i

data class IssuerDid(
    @SerializedName("id") val id: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("controller") val controller: String?,
    @SerializedName("publicKeyJwk") val publicKeyJwk: Dwsdk101i.Response.PublicKey?
)