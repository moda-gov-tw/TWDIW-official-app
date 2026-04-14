package tw.gov.moda.digitalwallet.data.model.dwverifier

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import tw.gov.moda.digitalwallet.data.annotation.APIMethod

class DwVerifierMgr401i {
    companion object {
        const val URL_PATH = "/api/ext/offline/qrcode/"
        const val METHOD = APIMethod.GET
    }

    @Keep
    data class Request(
        @SerializedName("vpUid")
        val vpUid: String
    )

    @Keep
    data class Response(
        @SerializedName("transactionId")
        val transactionId: String,
        @SerializedName("deepLink")
        val deepLink: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("fields")
        val fields: List<Field>,
    ) {
        @Keep
        data class Field(
            @SerializedName("cname")
            val cname: String,
            @SerializedName("ename")
            val ename: String,
            @SerializedName("regex")
            val regex: String
        )
    }
}