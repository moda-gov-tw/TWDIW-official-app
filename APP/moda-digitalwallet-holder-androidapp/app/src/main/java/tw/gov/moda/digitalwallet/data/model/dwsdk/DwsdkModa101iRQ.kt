package tw.gov.moda.digitalwallet.data.model.dwsdk

import com.google.gson.annotations.SerializedName

data class DwsdkModa101iRQ<T>(
    @SerializedName("url")
    private val url: String,
    @SerializedName("type")
    private val type: String,
    @SerializedName("body")
    private val body: T
)