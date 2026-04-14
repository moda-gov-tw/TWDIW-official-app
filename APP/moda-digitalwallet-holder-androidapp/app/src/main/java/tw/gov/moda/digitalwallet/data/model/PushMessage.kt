package tw.gov.moda.digitalwallet.data.model

import com.google.gson.annotations.SerializedName

data class PushMessage(
    @SerializedName("Param") val param: String,
    @SerializedName("Msg") val message: String,
    @SerializedName("MsgTitle") val title: String
)
