package tw.gov.moda.digitalwallet.data.model

import com.google.gson.annotations.SerializedName

data class DIDData(
    @SerializedName("@context")
    val context: List<String>,
    @SerializedName("id") val id: String,
    @SerializedName("verificationMethod") val verificationMethod: List<VerificationMethod>,
)

data class VerificationMethod(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("controller") val controller: String,
    @SerializedName("publicKeyJwk") val publicKeyJwk: PublicKeyJwk,
)

data class PublicKeyJwk(
    @SerializedName("crv") val crv: String,
    @SerializedName("kty") val kty: String,
    @SerializedName("x") val x: String,
    @SerializedName("y") val y: String,
)