package tw.gov.moda.digitalwallet.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.data.element.PageEnum

@Entity(tableName = AppConstants.Database.TABLE_SEARCH_RECORD)
data class SearchRecord(
    @PrimaryKey(autoGenerate = true) var uid: Long = 0L,
    var walletId: Long,
    var keyword: String = "",
    var recordTime: Long = System.currentTimeMillis(),
    var sourceType : PageEnum = PageEnum.ShowCredential
)
