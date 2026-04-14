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
interface OperationRecordDao {
    @Query("SELECT * FROM operation_record")
    suspend fun getAll(): List<OperationRecord>

    @Query("SELECT * FROM operation_record WHERE vcId =:vcId AND status =:statusEnum")
    suspend fun getItem(vcId: Long, statusEnum: OperationEnum): OperationRecord?

    @Query("SELECT * FROM operation_record ORDER BY datetime ASC LIMIT :limit OFFSET :offset")
    suspend fun getItemsAsc(limit: Int, offset: Int): List<OperationRecord>

    @Query("SELECT * FROM operation_record ORDER BY datetime DESC LIMIT :limit OFFSET :offset")
    suspend fun getItemsDesc(limit: Int, offset: Int): List<OperationRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: OperationRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(array: List<OperationRecord>)

    @Delete
    suspend fun delete(item: OperationRecord)

    @Query("DELETE FROM operation_record WHERE walletId = :walletId")
    suspend fun deletByWalletId(walletId: Long)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: OperationRecord): Int
}