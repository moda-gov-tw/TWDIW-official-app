package tw.gov.moda.digitalwallet.data.model.dwsdk

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import tw.gov.moda.digitalwallet.data.model.dwmoda.DwModa201i

@Keep
class Dwsdk402i {
    @Keep
    data class Request(
        @SerializedName("request_token") val requestToken: String,
        @SerializedName("didFile") val didFile: String,
        @SerializedName("vcs") val vcs: List<VPData>,
        @SerializedName("frontUrl") val frontUrl: String? = null,
        @SerializedName("custom_data") val customData: String? = null
    ) {
        data class VPData(
            @SerializedName("vc") val vc: String,
            @SerializedName("field") val fields: Array<String>,
            @SerializedName("card_id") val cardId: String
        )
    }

    data class CustomDataList(
        @SerializedName("customData") val list: List<CustomData>
    )

    data class CustomData(
        @SerializedName("cname") val cname: String,
        @SerializedName("ename") val ename: String,
        @SerializedName("value") val value: String
    )
}