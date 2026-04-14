package tw.gov.moda.digitalwallet.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.data.element.AutoLogoutEnum

@Entity(tableName = AppConstants.Database.TABLE_WALLET)
data class Wallet(
    @PrimaryKey(autoGenerate = true) @SerializedName("uid") var uid: Long = 0L,
    @SerializedName("nickname") var nickname: String = "",
    @SerializedName("pincode") var pincode: String = "",
    @SerializedName("deviceSecure") var deviceSecure: String = "",
    @SerializedName("keyTag") val keyTag: String = "",
    @SerializedName("did") var did: String = "",
    @SerializedName("pair") var pair: String = "",
    @SerializedName("autoLogout") var autoLogout: AutoLogoutEnum = AutoLogoutEnum.NEVER,
    @SerializedName("autoRefreshCard") var autoRefreshCard: Boolean = true
)
