package tw.gov.moda.digitalwallet.core.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import tw.gov.moda.digitalwallet.data.db.FavoriteShowCredential

@Dao
interface FavoriteShowCredentialDao {
    @Query("SELECT * FROM favorite_show_credential WHERE walletId = :walletId")
    suspend fun getAll(walletId: Long? = 0L): List<FavoriteShowCredential>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FavoriteShowCredential): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: FavoriteShowCredential): Int

    @Query("DELETE FROM favorite_show_credential WHERE vpUid = :vpUid")
    suspend fun deleteByVpUid(vpUid: String)
}