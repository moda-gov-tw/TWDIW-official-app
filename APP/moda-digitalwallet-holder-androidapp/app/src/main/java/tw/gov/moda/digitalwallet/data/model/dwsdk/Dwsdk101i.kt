package tw.gov.moda.digitalwallet.data.model.dwsdk

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
class Dwsdk101i {

    @Keep
    data class Response(
        @SerializedName("publicKey") val publicKey: PublicKey?,
        @SerializedName("privateKey") val privateKey: String?,
    ) {
        @Keep
        data class PublicKey(
            @SerializedName("kty") val kty: String?,
            @SerializedName("crv") val crv: String?,
            @SerializedName("x") val x: String?,
            @SerializedName("y") val y: String?,
        )
    }
}


