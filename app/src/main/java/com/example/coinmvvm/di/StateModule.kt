package com.example.coinmvvm.di

import android.content.Context
import com.example.coinmvvm.util.NetworkStateLiveData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class StateModule {
    @Singleton
    @Provides
    fun provideNetworkStateLiveData(@ApplicationContext context: Context): NetworkStateLiveData {
        return NetworkStateLiveData(context)
    }
}