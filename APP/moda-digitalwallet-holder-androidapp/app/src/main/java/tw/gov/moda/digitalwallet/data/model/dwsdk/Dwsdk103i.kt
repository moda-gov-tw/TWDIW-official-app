package tw.gov.moda.digitalwallet.data.model.dwsdk

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

class Dwsdk103i {
    @Keep
    data class Request(
        @SerializedName("keyTag") val keyTag: String,
        @SerializedName("type") val type: String,
        @SerializedName("PIN") val pin: String?
    )
}