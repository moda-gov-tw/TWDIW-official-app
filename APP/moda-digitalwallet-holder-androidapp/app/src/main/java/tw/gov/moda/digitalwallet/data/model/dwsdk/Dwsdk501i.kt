package tw.gov.moda.digitalwallet.data.model.dwsdk

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class Dwsdk501i {
    @Keep
    data class Request(
        @SerializedName("credential")
        val credential: String
    )


    @Keep
    data class Response(
        @SerializedName("sub") val sub: String?,
        @SerializedName("nbf") val nbf: Long?,
        @SerializedName("iss") val iss: String?,
        @SerializedName("cnf") val cnf: Cnf?,
        @SerializedName("exp") val exp: Long?,
        @SerializedName("vc") val vc: VC?,
        @SerializedName("nonce") val nonce: String?,
        @SerializedName("jti") val jti: String?
    ) {
        data class CredentialSubject(
            @SerializedName("_sd") val sd: Array<String>?,
            @SerializedName("_sd_alg") val sd_alg: String?,
            @SerializedName("field") val field: SubjectField?
        )

        data class CredentialSchema(
            @SerializedName("id")
            val id: String?,
            @SerializedName("type")
            val type: String?,
        )

        data class SubjectField(
            @SerializedName("code")
            val code: Int?,
            @SerializedName("data")
            val data: LinkedHashMap<String, String?>?
        )

        data class VC(
            @SerializedName("@context")
            val context: List<String>?,
            @SerializedName("type")
            val type: List<String>?,
            @SerializedName("credentialSubject")
            val credentialSubject: CredentialSubject?,
            @SerializedName("credentialSchema")
            val credentialSchema: CredentialSchema?,
        )

        data class Cnf(
            @SerializedName("crv")
            val crv: String?,
            @SerializedName("kty")
            val kty: String?,
            @SerializedName("x")
            val x: String?,
            @SerializedName("y")
            val y: String?
        )
    }


}