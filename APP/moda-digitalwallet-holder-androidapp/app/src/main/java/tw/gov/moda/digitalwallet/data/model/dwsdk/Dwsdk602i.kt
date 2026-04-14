package tw.gov.moda.digitalwallet.data.model.dwsdk

import androidx.annotation.Keep
import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName


@Keep
class Dwsdk602i {
    @Keep
    data class Request(
        @SerializedName("vcs")
        val vcs: List<String>,
        @SerializedName("vcList")
        val vcList: JsonArray
    )

    @Keep
    data class Response(
        val data: LinkedHashMap<String, Array<StatusList>?>?
    ){
        data class StatusList(
            @SerializedName("statusList") val statusList: String
        )
    }
}


