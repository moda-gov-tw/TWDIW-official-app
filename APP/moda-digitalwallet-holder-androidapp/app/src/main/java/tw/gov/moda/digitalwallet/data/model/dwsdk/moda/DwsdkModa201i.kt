package tw.gov.moda.digitalwallet.data.model.dwsdk.moda

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class DwsdkModa201i {

    @Keep
    data class Request(
        @SerializedName("url")
        val url: String,
        @SerializedName("payload")
        val payload: String,
        @SerializedName("didFile")
        val didFile: String
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