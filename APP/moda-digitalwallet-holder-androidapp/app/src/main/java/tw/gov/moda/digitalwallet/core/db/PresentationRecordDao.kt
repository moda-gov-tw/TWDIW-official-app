package tw.gov.moda.digitalwallet.core.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import tw.gov.moda.digitalwallet.data.db.CardRecord
import tw.gov.moda.digitalwallet.data.db.CredentialRecord
import tw.gov.moda.digitalwallet.data.db.Issuer
import tw.gov.moda.digitalwallet.data.db.OperationRecord
import tw.gov.moda.digitalwallet.data.db.PresentationRecord
import tw.gov.moda.digitalwallet.data.element.OperationEnum

@Dao
interface PresentationRecordDao {
    @Query("SELECT * FROM presentation_record")
    suspend fun getAll(): List<PresentationRecord>

    @Query("SELECT * FROM presentation_record ORDER BY datetime ASC LIMIT :limit OFFSET :offset")
    suspend fun getItemsAsc(limit: Int, offset: Int): List<PresentationRecord>

    @Query("SELECT * FROM presentation_record ORDER BY datetime DESC LIMIT :limit OFFSET :offset")
    suspend fun getItemsDesc(limit: Int, offset: Int): List<PresentationRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PresentationRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(array: List<PresentationRecord>)

    @Delete
    suspend fun delete(item: PresentationRecord)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: PresentationRecord): Int
}