package tw.gov.moda.digitalwallet.data.model

import com.google.gson.annotations.SerializedName

data class DownloadIssuers(
    @SerializedName("url")
    val url: String,
    @SerializedName("newFlag")
    val newFlag: Boolean
)
