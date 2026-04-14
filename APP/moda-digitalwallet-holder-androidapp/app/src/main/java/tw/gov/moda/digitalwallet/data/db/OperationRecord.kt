package tw.gov.moda.digitalwallet.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.data.element.OperationEnum

@Entity(tableName = AppConstants.Database.TABLE_OPERATION_RECORD)
data class OperationRecord(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("uid") var uid: Long = 0L,
    @SerializedName("walletId") var walletId: Long,
    @SerializedName("vcId") var vcId: Long?,
    @SerializedName("text") var text: String,
    @SerializedName("status") var status: OperationEnum,
    @SerializedName("datetime") var datetime: Long = System.currentTimeMillis()
)