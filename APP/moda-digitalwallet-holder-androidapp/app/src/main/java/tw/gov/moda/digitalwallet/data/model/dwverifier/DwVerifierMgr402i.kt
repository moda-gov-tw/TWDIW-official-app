package tw.gov.moda.digitalwallet.data.model.dwverifier

import android.view.View
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import tw.gov.moda.digitalwallet.data.annotation.APIMethod

class DwVerifierMgr402i {
    companion object {
        const val URL_PATH = "/api/ext/offline/getEncryptionData"
        const val METHOD = APIMethod.POST
    }

    @Keep
    data class Request(
        @SerializedName("transactionId")
        val transactionId: String,
    )

    @Keep
    data class Response(
        @SerializedName("qrcode")
        val qrcode: String,
        @SerializedName("totptimeout")
        val totptimeout: String // 秒數
    )
}