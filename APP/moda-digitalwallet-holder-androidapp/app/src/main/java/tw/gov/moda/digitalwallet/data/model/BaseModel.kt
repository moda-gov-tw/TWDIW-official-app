package tw.gov.moda.digitalwallet.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BaseModel<T>(
    @SerializedName("code") val code: String? = "",
    @SerializedName("message") val message: String? = "",
    @SerializedName("data") val data: T? = null
)
