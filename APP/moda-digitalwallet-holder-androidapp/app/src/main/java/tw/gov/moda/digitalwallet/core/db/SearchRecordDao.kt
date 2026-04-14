package tw.gov.moda.digitalwallet.core.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import tw.gov.moda.digitalwallet.data.db.CardRecord
import tw.gov.moda.digitalwallet.data.db.SearchRecord
import tw.gov.moda.digitalwallet.data.element.PageEnum

@Dao
interface SearchRecordDao {

    @Query("SELECT * FROM search_record WHERE walletId = :walletId AND sourceType =:type ORDER BY recordTime DESC")
    suspend fun getAllByType(walletId: Long? = 0L, type: PageEnum): List<SearchRecord>

    @Query("SELECT * FROM search_record")
    suspend fun getAll(): List<SearchRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SearchRecord): Long

    @Query("DELETE FROM search_record WHERE uid NOT IN ( SELECT uid FROM search_record WHERE sourceType =:type ORDER BY recordTime DESC LIMIT 5)")
    suspend fun deleteOlderByType(type: PageEnum)

    @Delete
    suspend fun delete(item: SearchRecord)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: SearchRecord): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(array: List<SearchRecord>)

    @Query("SELECT COUNT(*) FROM search_record WHERE sourceType =:type")
    suspend fun countAllByType(type: PageEnum): Int
}