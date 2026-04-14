package tw.gov.moda.digitalwallet.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.data.element.OperationEnum

data class CombinedCredentialOperationRecord(
    @SerializedName("uid") var uid: String,
    @SerializedName("walletId") var walletId: Long,
    @SerializedName("vcId") var vcId: Long,
    @SerializedName("text") var text: String,
    @SerializedName("authorizationUnit") var authorizationUnit: String?,
    @SerializedName("authorizationPurpose") var authorizationPurpose: String?,
    @SerializedName("authorizationField") var authorizationField: String?,
    @SerializedName("status") var status: OperationEnum?,
    @SerializedName("datetime") var datetime: Long
)
