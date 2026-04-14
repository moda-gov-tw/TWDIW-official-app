package tw.gov.moda.digitalwallet.data.model.dwsdk

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
class Dwsdk401i {
    @Keep
    data class Request(
        @SerializedName("qrCode") val qrCode: String,
        @SerializedName("frontUrl") val frontUrl: String? = null,
    )

    @Keep
    data class Response(
        @SerializedName("request_token") val requestToken: String?,
        @SerializedName("request_data") val requestDataArray: Array<RequestDataGroup>?
    ) {

        @Keep
        data class RequestDataGroup(
            @SerializedName("name")
            val name: String?,
            @SerializedName("group")
            val group: String?,
            @SerializedName("rule")
            val rule: String?,
            @SerializedName(value = "max", alternate = ["count"])
            val count: Int?,
            @SerializedName("cards")
            val cards: List<RequestData>
        ) {
            @Keep
            data class RequestData(
                @SerializedName("card")
                val card: String?,
                @SerializedName("card_id")
                val cardId: String?,
                @SerializedName("name")
                val name: String?,
                @SerializedName("fields")
                val fields: List<String>?,
            )
        }
    }


}