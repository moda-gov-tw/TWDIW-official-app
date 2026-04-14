package tw.gov.moda.digitalwallet.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.data.element.OperationEnum

@Entity(tableName = AppConstants.Database.TABLE_CARD_RECORD)
data class CardRecord(
    @PrimaryKey(autoGenerate = true) @SerializedName("uid") var uid: Long = 0L,
    @SerializedName("vcId") var vcId: Long,
    @SerializedName("text") var text: String = "",
    @SerializedName("authorizationUnit") var authorizationUnit: String = "",
    @SerializedName("authorizationPurpose") var authorizationPurpose: String = "",
    @SerializedName("authorizationField") var authorizationField: String = "",
    @SerializedName("datetime") var datetime: Long = System.currentTimeMillis(),
    // 0: 一般, 1: 加入卡片
    @SerializedName("status") var status: OperationEnum
)
