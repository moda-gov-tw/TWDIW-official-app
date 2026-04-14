package tw.gov.moda.digitalwallet.core.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import tw.gov.moda.digitalwallet.data.db.Issuer

@Dao
interface IssuerDao {
    @Query("SELECT * FROM issuer")
    suspend fun getAll(): List<Issuer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Issuer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(array: List<Issuer>)

    @Delete
    suspend fun delete(item: Issuer)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: Issuer): Int
}