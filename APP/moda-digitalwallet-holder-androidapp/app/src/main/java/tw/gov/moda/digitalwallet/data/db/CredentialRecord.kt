package tw.gov.moda.digitalwallet.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import tw.gov.moda.digitalwallet.common.AppConstants

@Entity(tableName = AppConstants.Database.TABLE_CREDENTIAL_RECORD)
data class CredentialRecord(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("uid") var uid: Long = 0L,
    @SerializedName("walletId") var walletId: Long,
    @SerializedName("vcId") var vcId: Long,
    @SerializedName("text") var text: String = "",
    @SerializedName("authorizationUnit") var authorizationUnit: String = "",
    @SerializedName("authorizationPurpose") var authorizationPurpose: String = "",
    @SerializedName("authorizationField") var authorizationField: String = "",
    @SerializedName("datetime") var datetime: Long = System.currentTimeMillis()
)
