package tw.gov.moda.digitalwallet.core.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import tw.gov.moda.digitalwallet.data.db.CardRecord
import tw.gov.moda.digitalwallet.data.db.CredentialRecord
import tw.gov.moda.digitalwallet.data.element.OperationEnum

@Dao
interface CredentialRecordDao {
    @Query("SELECT * FROM credential_record WHERE vcId =:vcId")
    suspend fun getAll(vcId: Long): List<CredentialRecord>

    @Query("SELECT * FROM credential_record")
    suspend fun getAll(): List<CredentialRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CredentialRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(array: List<CredentialRecord>)

    @Delete
    suspend fun delete(item: CredentialRecord)

    @Query("DELETE FROM credential_record WHERE walletId = :walletId")
    suspend fun deleteByWalletId(walletId: Long)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: CredentialRecord): Int
}