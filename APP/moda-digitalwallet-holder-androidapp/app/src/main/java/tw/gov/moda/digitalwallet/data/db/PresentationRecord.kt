package tw.gov.moda.digitalwallet.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import tw.gov.moda.digitalwallet.common.AppConstants

@Entity(tableName = AppConstants.Database.TABLE_PRESENTATION_RECORD)
data class PresentationRecord(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("uid") var uid: Long = 0L,
    @SerializedName("walletId") var walletId: Long,
    @SerializedName("text") var text: String = "",
    @SerializedName("vcIds") var vcIds: String,
    @SerializedName("vcNames") var vcNames: String,
    @SerializedName("authorizationUnit") var authorizationUnit: String = "",
    @SerializedName("authorizationPurpose") var authorizationPurpose: String = "",
    @SerializedName("authorizationFields") var authorizationFields: String = "",
    @SerializedName("datetime") var datetime: Long = System.currentTimeMillis()
)