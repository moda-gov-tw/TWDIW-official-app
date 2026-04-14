package tw.gov.moda.digitalwallet.ui.splash

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.core.db.DigitalWalletDB
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.data.db.CredentialRecord
import tw.gov.moda.digitalwallet.data.db.OperationRecord
import tw.gov.moda.digitalwallet.data.element.OperationEnum
import tw.gov.moda.digitalwallet.extension.getBytes
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val mWalletRepository: WalletRepository,
    private val mResourceProvider: ResourceProvider,
    private val mPref: ModaSharedPreferences,
    private val mDatabase: DigitalWalletDB
) : BaseViewModel() {
    private val mLaunchWelcomeFragment = MutableLiveData<Boolean>()
    val launchWelcomeFragment: LiveData<Boolean> get() = mLaunchWelcomeFragment
    private val mLaunchLoginFragment = MutableLiveData<Boolean>()
    val launchLoginFragment: LiveData<Boolean> get() = mLaunchLoginFragment
    private val mMigrationDatabaseForEncryption = MutableLiveData<Boolean>()
    val migrationDatabaseForEncryption: LiveData<Boolean> get() = mMigrationDatabaseForEncryption
    private val mMigrationDatabaseForRecords = MutableLiveData<Boolean>()
    val migrationDatabaseForRecords: LiveData<Boolean> get() = mMigrationDatabaseForRecords
    private val mAlertRebootApplication = MutableLiveData<Boolean>()
    val alertRebootApplication: LiveData<Boolean> get() = mAlertRebootApplication

    init {
        migrationDatabase()
    }

    private fun launchNextPage() {
        viewModelScope.launch(getExceptionHandler()) {
            mWalletRepository.setLoginStatus(false)
            mDatabase.walletDao().apply {
                getAll().takeIf { it.isNotEmpty() }?.firstOrNull()?.also { wallet ->
                    mLaunchLoginFragment.postValue(true)
                } ?: run {
                    mLaunchWelcomeFragment.postValue(true)
                }
            }
        }
    }

    private fun migrationDatabase() {
        viewModelScope.launch(getExceptionHandler()) {
            // 確認舊的Database是否存在，有則進行移轉。
            if (mResourceProvider.existDatavase(AppConstants.Database.OLD_NAME)) {
                mMigrationDatabaseForEncryption.postValue(true)
            } else if (!mPref.isMigrationDatabaseForRecords) {
                mMigrationDatabaseForRecords.postValue(true)
            } else {
                launchNextPage()
            }
        }
    }

    fun encryptRoomDatabase(context: Context, retryCount: Int = 0) {
        viewModelScope.launch(getExceptionHandler { _, throwable ->
            encryptRoomDatabase(context, retryCount + 1)
        }) {
            // 重試還是失敗則直接刪除舊的資料庫
            if (retryCount >= 2) {
                mPref.dbName = AppConstants.Database.NAME
                context.deleteDatabase(AppConstants.Database.OLD_NAME)
                launchNextPage()
                return@launch
            }

            // Step 1. 開啟舊資料庫
            val oldDatabase = Room.databaseBuilder(
                context,
                DigitalWalletDB::class.java, AppConstants.Database.OLD_NAME
            )
                .addMigrations(
                    AppConstants.Database.MIGRATION_FROM_5,
                    AppConstants.Database.MIGRATION_FROM_6,
                    AppConstants.Database.MIGRATION_FROM_7,
                    AppConstants.Database.MIGRATION_FROM_8,
                    AppConstants.Database.MIGRATION_FROM_9,
                    AppConstants.Database.MIGRATION_FROM_10
                )
                .openHelperFactory(if (retryCount == 1) SupportOpenHelperFactory(mPref.getOrNewRandomSalt().getBytes()) else SupportOpenHelperFactory(null))
                .build()

            // Step 2. 複製資料
            val walletList = oldDatabase.walletDao().getAll()
            mDatabase.walletDao().insertAll(walletList)

            val vcList = oldDatabase.verifiableCredentialDao().getAllItems()
            mDatabase.verifiableCredentialDao().insertAll(vcList)

            val issuerList = oldDatabase.issuerDao().getAll()
            mDatabase.issuerDao().insertAll(issuerList)

            val recordList = oldDatabase.cardRecordDao().getAll()
            mDatabase.cardRecordDao().insertAll(recordList)

            val searchRecordList = oldDatabase.searchRecordDao().getAll()
            mDatabase.searchRecordDao().insertAll(searchRecordList)

            // Step 3. 刪除舊資料，替換預載DB名稱
            mPref.dbName = AppConstants.Database.NAME
            context.deleteDatabase(AppConstants.Database.OLD_NAME)

            // Step 4. 檢查是否需要 migrationDatabaseForRecords
            if (mPref.isMigrationDatabaseForRecords) {
                launchNextPage()
            } else {
                migrationDatabaseForRecords()
            }
        }
    }

    fun migrationDatabaseForRecords() {
        viewModelScope.launch(getExceptionHandler()) {
            mDatabase.cardRecordDao().getAll().forEach { cardRecord ->
                if (cardRecord.status == OperationEnum.AUTHORIZATION_CARD) {
                    mDatabase.verifiableCredentialDao().getItem(cardRecord.vcId)?.also { verifiableCredential ->
                        val credentialRecord = CredentialRecord(
                            0L,
                            verifiableCredential.walletId,
                            cardRecord.vcId,
                            cardRecord.text,
                            cardRecord.authorizationUnit,
                            cardRecord.authorizationPurpose,
                            cardRecord.authorizationField,
                            cardRecord.datetime
                        )
                        mDatabase.credentialRecordDao().insert(credentialRecord)
                    }
                } else {
                    mDatabase.verifiableCredentialDao().getItem(cardRecord.vcId)?.also { verifiableCredential ->
                        val operationRecord = OperationRecord(
                            0L,
                            verifiableCredential.walletId,
                            cardRecord.vcId,
                            cardRecord.text,
                            cardRecord.status,
                            cardRecord.datetime
                        )
                        mDatabase.operationRecordDao().insert(operationRecord)
                    }
                }
            }
            mPref.isMigrationDatabaseForRecords = true
            launchNextPage()
        }
    }
}