package tw.gov.moda.digitalwallet.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import tw.gov.moda.digitalwallet.common.AppConstants

@Entity(tableName = AppConstants.Database.TABLE_ISSUER)
data class Issuer(
    @PrimaryKey @SerializedName("did") val did: String,
    @SerializedName("org") val org: IssuerOrg?,
    @SerializedName("status") val status: Int,
    @SerializedName("createdAt") val createdAt: Long,
    @SerializedName("updatedAt") val updatedAt: Long,
)

data class IssuerOrg(
    @SerializedName("name") val name: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("zh-tw") val zhtw: String?,
    @SerializedName("en") val en: String?,
    @SerializedName("info") val info: String?,
    @SerializedName("createAt") val createAt: Long?,
    @SerializedName("updateAt") val updateAt: Long?,
    @SerializedName("taxId") val taxId: String?
)