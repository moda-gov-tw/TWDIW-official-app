package tw.gov.moda.digitalwallet.di.module

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.core.biometric.ModaBiometricManager
import tw.gov.moda.digitalwallet.core.biometric.ModaBiometricManagerImpl
import tw.gov.moda.digitalwallet.core.db.DigitalWalletDB
import tw.gov.moda.digitalwallet.core.deeplink.DeeplinkManager
import tw.gov.moda.digitalwallet.core.deeplink.DeeplinkManagerImpl
import tw.gov.moda.digitalwallet.core.identifier.IdentifierManager
import tw.gov.moda.digitalwallet.core.identifier.KxIdentifierManagerImpl
import tw.gov.moda.digitalwallet.core.keystore.ModaKeyStoreManager
import tw.gov.moda.digitalwallet.core.keystore.ModaKeyStoreManagerImpl
import tw.gov.moda.digitalwallet.core.network.NetworkManager
import tw.gov.moda.digitalwallet.core.network.NetworkManagerImpl
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.core.resource.ResourceProviderImpl
import tw.gov.moda.digitalwallet.core.verifiable.VerifiableManager
import tw.gov.moda.digitalwallet.core.verifiable.VerifiableManagerImpl
import tw.gov.moda.digitalwallet.data.model.PushMessage
import tw.gov.moda.digitalwallet.extension.getBytes
import tw.gov.moda.digitalwallet.ui.main.controller.DeeplinkController
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    fun providerVerifiableManager(
        @ApplicationContext context: Context, pref: ModaSharedPreferences, gson: Gson,
        walletRepository: WalletRepository, identifierManager: IdentifierManager
    ): VerifiableManager {
        return VerifiableManagerImpl(context, pref, gson, walletRepository, identifierManager)
    }

    @Singleton
    @Provides
    fun providerResourceProvicer(@ApplicationContext context: Context): ResourceProvider {
        return ResourceProviderImpl(context)
    }

    @Singleton
    @Provides
    fun providerIden(@ApplicationContext context: Context, gson: Gson, methodChannel: MethodChannel, walletRepository: WalletRepository): IdentifierManager {
        val identifierManager: IdentifierManager = KxIdentifierManagerImpl(methodChannel, context, gson, walletRepository)
        return identifierManager
    }

    @Singleton
    @Provides
    fun providerSharedPreferences(@ApplicationContext context: Context, modaKeyStoreManager: ModaKeyStoreManager): ModaSharedPreferences {
        val pref = ModaSharedPreferences(context, modaKeyStoreManager)
        return pref
    }

    @Singleton
    @Provides
    fun providerFlutterEngine(@ApplicationContext context: Context): FlutterEngine {
        val flutterEngine = FlutterEngine(context)
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )
        return flutterEngine
    }

    @Singleton
    @Provides
    fun providerMethodChannel(flutterEngine: FlutterEngine): MethodChannel {
        return MethodChannel(flutterEngine.dartExecutor.binaryMessenger, AppConstants.DID.CHANNEL_NAME)
    }

    @Singleton
    @Provides
    fun providerKeyStoreManager(@ApplicationContext context: Context): ModaKeyStoreManager {
        return ModaKeyStoreManagerImpl(context)
    }

    @Singleton
    @Provides
    fun providerModaBiometricManager(@ApplicationContext context: Context, keyStoreManager: ModaKeyStoreManager): ModaBiometricManager {
        return ModaBiometricManagerImpl(context, keyStoreManager)
    }

    @Singleton
    @Provides
    fun providerDeeplinkController(): DeeplinkController {
        return DeeplinkController()
    }


    @Singleton
    @Provides
    fun providerDeeplinkManager(
        walletRepository: WalletRepository, pref: ModaSharedPreferences, database: DigitalWalletDB,
        resourceProvider: ResourceProvider, verifiableManager: VerifiableManager, viewController: DeeplinkController,
        gson: Gson
    ): DeeplinkManager {
        return DeeplinkManagerImpl(walletRepository, verifiableManager, pref, gson, database, resourceProvider, viewController)
    }

    @Provides
    fun providerGson(): Gson {
        return GsonBuilder().create()
    }

    @Singleton
    @Provides
    fun providerUnregisterVerifiableCredentialLiveData(): MutableLiveData<PushMessage?> {
        return MutableLiveData<PushMessage?>()
    }

    @Singleton
    @Provides
    fun providerDatabase(@ApplicationContext context: Context, pref: ModaSharedPreferences): DigitalWalletDB {
        val db = Room.databaseBuilder(
            context,
            DigitalWalletDB::class.java, AppConstants.Database.NAME
        )
            .openHelperFactory(SupportOpenHelperFactory(pref.getOrNewRandomSalt().getBytes()))
            .addMigrations(AppConstants.Database.MIGRATION_FROM_7, AppConstants.Database.MIGRATION_FROM_8, AppConstants.Database.MIGRATION_FROM_9, AppConstants.Database.MIGRATION_FROM_10)
            .fallbackToDestructiveMigration()
            .build()
        return db
    }

    @Singleton
    @Provides
    fun providerNetworkManager(@ApplicationContext context: Context): NetworkManager {
        return NetworkManagerImpl(context)
    }

}