package tw.gov.moda.digitalwallet.core.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import tw.gov.moda.digitalwallet.data.db.CardRecord
import tw.gov.moda.digitalwallet.data.element.OperationEnum

@Dao
interface CardRecordDao {
    @Query("SELECT * FROM card_record WHERE vcId =:vcId")
    suspend fun getAllByVCId(vcId: Long): List<CardRecord>

    @Query("SELECT * FROM card_record")
    suspend fun getAll(): List<CardRecord>

    @Query("SELECT * FROM card_record WHERE vcId =:vcId AND status =:statusEnum")
    suspend fun getItem(vcId: Long, statusEnum: OperationEnum): CardRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CardRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(array: List<CardRecord>)

    @Delete
    suspend fun delete(item: CardRecord)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: CardRecord): Int
}