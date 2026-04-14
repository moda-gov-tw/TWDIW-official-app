package tw.gov.moda.digitalwallet.data.model.dwverifier

import com.google.gson.annotations.SerializedName
import tw.gov.moda.digitalwallet.data.annotation.APIMethod

class DwVerifierMock101i {
    companion object {
        const val METHOD = APIMethod.GET
    }

    data class Request(
        @SerializedName("vpUid")
        val vpUid: String
    )

    data class Response(
        @SerializedName("deepLink")
        val deepLink: String?
    )
}
