package com.example.training.di

import com.example.training.data.NetworkManagerImpl
import com.example.training.data.RequestsImpl
import com.example.training.domain.NetworkManager
import com.example.training.domain.Requests
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SingletonComponent {
    @Binds
    @Singleton
    abstract fun provideNetworkManager(networkManagerImpl: NetworkManagerImpl): NetworkManager

    @Binds
    @Singleton
    abstract fun provideRequests(requestsImpl: RequestsImpl): Requests
}