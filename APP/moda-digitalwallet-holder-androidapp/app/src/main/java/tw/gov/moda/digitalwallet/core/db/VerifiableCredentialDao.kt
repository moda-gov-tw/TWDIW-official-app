package tw.gov.moda.digitalwallet.core.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import tw.gov.moda.digitalwallet.data.db.VerifiableCredential
import tw.gov.moda.digitalwallet.data.db.Wallet

@Dao
interface VerifiableCredentialDao {
    @Query("SELECT * FROM verifiable_credential WHERE walletId = :walletId ORDER By uid DESC")
    suspend fun getAll(walletId: Long? = 0L): List<VerifiableCredential>

    @Query("SELECT * FROM verifiable_credential WHERE walletId = :walletId ORDER By uid ASC")
    suspend fun getAllByAscending(walletId: Long? = 0L): List<VerifiableCredential>
    
    @Query("SELECT * FROM verifiable_credential")
    suspend fun getAllItems(): List<VerifiableCredential>

    @Query("SELECT * FROM verifiable_credential WHERE uid = :uid")
    suspend fun getItem(uid: Long? = 0L): VerifiableCredential?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(array: List<VerifiableCredential>)

    @Insert
    suspend fun insert(item: VerifiableCredential): Long

    @Delete
    suspend fun delete(item: VerifiableCredential)

    @Query("DELETE FROM verifiable_credential WHERE uid = :walletId")
    suspend fun deleteByWalletId(walletId: Long)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: VerifiableCredential): Int
}