package tw.gov.moda.digitalwallet.data.model

import com.google.gson.annotations.SerializedName

data class PostMessage(
    @SerializedName("type")
    var type: String,
    @SerializedName("data")
    var data: Data
) {
    data class Data(
        @SerializedName("transactionId")
        var transactionId: String,
        @SerializedName("qrCode")
        var qrCode: String,
        @SerializedName("deeplink")
        var deeplink: String,
        @SerializedName("type")
        var type: String?,
    )
}
