package tw.gov.moda.digitalwallet.data.model

import com.google.gson.annotations.SerializedName
import tw.gov.moda.digitalwallet.data.element.OperationEnum

data class OperateRecordData(
    @SerializedName("id")
    var id: Double,
    @SerializedName("type")
    var type: OperationEnum,
    @SerializedName("title")
    var title: String,
    @SerializedName("updateTime")
    var updateTime: String,
    @SerializedName("vcContent")
    var vcContent: String?,
    @SerializedName("unitContent")
    var unitContent: String?,
    @SerializedName("purposeContent")
    var purposeContent: String?,
    @SerializedName("fieldContent")
    var fieldContent: String?
)
