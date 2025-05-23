package com.example.healthassistant.core.di

import android.content.Context
import com.example.healthassistant.consent.ConsentDatabase
import com.example.healthassistant.consent.ConsentRepository
import com.example.healthassistant.consent.ConsentRepositoryImpl
import com.example.healthassistant.consent.GetConsentUseCase
import com.example.healthassistant.consent.LocalConsentStore
import com.example.healthassistant.consent.LocalConsentStoreImpl
import com.example.healthassistant.consent.MockConsentDatabase
import com.example.healthassistant.core.network.KtorClientProvider
import com.example.healthassistant.core.repositories.LoginRepository
import com.example.healthassistant.core.repositories.LoginRepositoryImpl
import com.example.healthassistant.core.stores.TokenStore
import com.example.healthassistant.core.stores.TokenStoreImpl
import com.example.healthassistant.login.LoginUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideKtorClient(tokenStore: TokenStore): HttpClient {
        return KtorClientProvider.getClient(tokenStore) // Inject the TokenStore
    }

    @Provides
    @Singleton
    fun provideLoginRepository(client: HttpClient): LoginRepository {
        return LoginRepositoryImpl(client)
    }

    @Provides
    @Singleton
    fun provideTokenStore(@ApplicationContext context: Context): TokenStore {
        return TokenStoreImpl(context)
    }

    @Provides
    @Singleton
    fun provideConsentDatabase(): ConsentDatabase {
        return MockConsentDatabase()
    }

    @Provides
    @Singleton
    fun provideLocalConsentStore(database: ConsentDatabase): LocalConsentStore {
        return LocalConsentStoreImpl(database)
    }

    @Provides
    @Singleton
    fun provideConsentRepository(client: HttpClient): ConsentRepository {
        return ConsentRepositoryImpl(client)
    }

    @Provides
    fun provideLoginUseCase(
        loginRepository: LoginRepository,
        consentRepository: ConsentRepository,
        tokenStore: TokenStore,
        localConsentStore: LocalConsentStore
    ): LoginUseCase {
        return LoginUseCase(
            loginRepo = loginRepository,
            consentRepo = consentRepository,
            tokenStore = tokenStore,
            localConsentStore = localConsentStore
        )
    }

    @Provides
    @Singleton
    fun provideGetConsentUseCase(localConsentStore: LocalConsentStore): GetConsentUseCase {
        return GetConsentUseCase(localConsentStore)
    }


}