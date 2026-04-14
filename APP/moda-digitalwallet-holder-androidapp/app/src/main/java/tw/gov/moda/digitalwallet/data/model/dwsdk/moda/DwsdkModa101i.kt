package tw.gov.moda.digitalwallet.data.model.dwsdk.moda

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class DwsdkModa101i {

    @Keep
    data class Request<T>(
        @SerializedName("url")
        private val url: String,
        @SerializedName("type")
        private val type: String,
        @SerializedName("body")
        private val body: T
    )

    @Keep
    data class Response<T>(
        @SerializedName("code")
        val code: String? = "",
        @SerializedName("message")
        val message: String? = "",
        @SerializedName("data")
        val data: T? = null
    )
}
