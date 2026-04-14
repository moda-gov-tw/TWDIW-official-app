package tw.gov.moda.digitalwallet.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.data.db.CardRecord
import tw.gov.moda.digitalwallet.data.db.CredentialRecord
import tw.gov.moda.digitalwallet.data.db.FavoriteShowCredential
import tw.gov.moda.digitalwallet.data.db.Issuer
import tw.gov.moda.digitalwallet.data.db.OperationRecord
import tw.gov.moda.digitalwallet.data.db.PresentationRecord
import tw.gov.moda.digitalwallet.data.db.SearchRecord
import tw.gov.moda.digitalwallet.data.db.VerifiableCredential
import tw.gov.moda.digitalwallet.data.db.Wallet

@Database(
    entities = [Wallet::class, VerifiableCredential::class, Issuer::class,
        CardRecord::class, FavoriteShowCredential::class, SearchRecord::class,
        CredentialRecord::class, OperationRecord::class, PresentationRecord::class], version = AppConstants.Database.DB_VERSION, exportSchema = false
)
@TypeConverters(DBTypeConverter::class)
abstract class DigitalWalletDB : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun verifiableCredentialDao(): VerifiableCredentialDao
    abstract fun issuerDao(): IssuerDao
    abstract fun cardRecordDao(): CardRecordDao
    abstract fun credentialRecordDao(): CredentialRecordDao
    abstract fun operationRecordDao(): OperationRecordDao
    abstract fun presentationRecordDao(): PresentationRecordDao
    abstract fun combinedRecordDao(): CombinedRecordDao
    abstract fun favoriteShowCredentialDao(): FavoriteShowCredentialDao
    abstract fun searchRecordDao(): SearchRecordDao
}