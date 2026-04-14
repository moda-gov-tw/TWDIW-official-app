package tw.gov.moda.digitalwallet.core.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import tw.gov.moda.digitalwallet.data.db.Issuer
import tw.gov.moda.digitalwallet.data.db.Wallet

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet")
    suspend fun getAll(): List<Wallet>

    @Query("SELECT * FROM wallet WHERE nickname LIKE :nickname LIMIT 1")
    suspend fun findByName(nickname: String): Wallet?

    @Query("SELECT * FROM wallet WHERE uid LIKE :uid LIMIT 1")
    suspend fun findByUID(uid: Long): Wallet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(array: List<Wallet>)

    @Insert
    suspend fun insert(item: Wallet): Long

    @Delete
    suspend fun delete(item: Wallet)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: Wallet): Int
}