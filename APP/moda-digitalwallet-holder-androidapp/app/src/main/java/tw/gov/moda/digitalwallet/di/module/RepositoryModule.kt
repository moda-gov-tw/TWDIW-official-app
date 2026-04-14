package tw.gov.moda.digitalwallet.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tw.gov.moda.digitalwallet.core.repository.barcode.BarcodeRepository
import tw.gov.moda.digitalwallet.core.repository.barcode.BarcodeRepositoryImpl
import tw.gov.moda.digitalwallet.core.repository.verifiable.VerifiablePresentationRepository
import tw.gov.moda.digitalwallet.core.repository.verifiable.VerifiablePresentationRepositoryImpl
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun providerWalletRepository(): WalletRepository {
        return WalletRepositoryImpl()
    }

    @Provides
    @Singleton
    fun providerVerifiablePresentationRepository(): VerifiablePresentationRepository {
        return VerifiablePresentationRepositoryImpl()
    }

    @Provides
    @Singleton
    fun providerBarcodeRepository(): BarcodeRepository {
        return BarcodeRepositoryImpl()
    }
}