package tw.gov.moda.digitalwallet.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import tw.gov.moda.digitalwallet.common.AppConstants

@Entity(tableName = AppConstants.Database.TABLE_FAVORITE_SHOW_CREDENTIAL)
data class FavoriteShowCredential(
    @PrimaryKey(autoGenerate = true) var uid: Long = 0L,
    var walletId: Long,
    var vpUid: String? = "",
    var name: String? = "",
    var verifierModuleUrl: String? = "",
    var logoUrl: String? = "",
)
