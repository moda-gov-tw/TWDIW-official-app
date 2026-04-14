package tw.gov.moda.digitalwallet.data.model

import com.google.gson.annotations.SerializedName

data class RequireVerifiablePresentationField (
    @SerializedName("index")
    var index: Int,
    @SerializedName("ename")
    var ename: String,
    @SerializedName("cname")
    var cname: String,
    @SerializedName("value")
    var value: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("regex")
    val regex: String?,
    @SerializedName("isHidden")
    var isHidden: Boolean = false,
    @SerializedName("isError")
    var isError: Boolean = false
)