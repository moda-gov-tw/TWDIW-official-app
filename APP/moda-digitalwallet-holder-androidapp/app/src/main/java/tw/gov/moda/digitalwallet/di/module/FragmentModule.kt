package tw.gov.moda.digitalwallet.di.module

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import tw.gov.moda.digitalwallet.core.db.DigitalWalletDB
import tw.gov.moda.digitalwallet.core.identifier.IdentifierManager
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.verifiable.VerifiableManager
import tw.gov.moda.digitalwallet.core.wallet.WalletManager
import tw.gov.moda.digitalwallet.core.wallet.WalletManagerImpl


@Module
@InstallIn(ViewModelComponent::class)
object FragmentModule {

    @Provides
    fun providerWalletManager(identifierManager: IdentifierManager, verifiableManager: VerifiableManager, pref: ModaSharedPreferences, database: DigitalWalletDB, gson: Gson): WalletManager {
        return WalletManagerImpl(verifiableManager, pref, identifierManager, gson, database)
    }
}