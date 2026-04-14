package tw.gov.moda.digitalwallet.data.model.dwsdk

import androidx.annotation.Keep
import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName

@Keep
class Dwsdk301i {
    @Keep
    data class Request(
        @SerializedName("credential") val credential: String,
        @SerializedName("didFile") val didFile: String,
        @SerializedName("frontUrl") val frontUrl: String? = null,
        @SerializedName("issList") val issList: JsonArray? = null,
        @SerializedName("vcList") val vcList: JsonArray? = null
    )

    @Keep
    data class Response(
        @SerializedName("trust")
        val trust: Boolean?,
        @SerializedName("vc")
        val vc: Boolean?,
        @SerializedName("issuer")
        val issuer: Boolean?,
        @SerializedName("exp")
        val exp: Boolean,
        @SerializedName("holder")
        val holder: Boolean?,
        @SerializedName("trust_badge")
        val trust_badge: Boolean?
    )
}