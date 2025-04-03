package com.example.healthassistant.core.di

import android.content.Context
import com.example.healthassistant.core.KtorClientProvider
import com.example.healthassistant.core.repositories.LoginRepository
import com.example.healthassistant.core.repositories.LoginRepositoryImpl
import com.example.healthassistant.core.stores.TokenStore
import com.example.healthassistant.core.stores.TokenStoreImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideHttpClient(): HttpClient {
        return KtorClientProvider.getClient(null) // no auth needed
    }

    @Provides
    fun provideLoginRepository(client: HttpClient): LoginRepository {
        return LoginRepositoryImpl(client)
    }

    @Provides
    fun provideTokenStore(@ApplicationContext context: Context): TokenStore {
        return TokenStoreImpl(context)
    }
}