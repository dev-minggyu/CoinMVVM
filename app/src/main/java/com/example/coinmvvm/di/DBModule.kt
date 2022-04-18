package com.example.coinmvvm.di

import android.content.Context
import androidx.room.Room
import com.example.coinmvvm.data.local.db.CoinDao
import com.example.coinmvvm.data.local.db.CoinDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DBModule {
    @Singleton
    @Provides
    fun provideCoinDatabase(@ApplicationContext context: Context): CoinDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            CoinDatabase::class.java, CoinDatabase.DB_FILE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideCoinDao(coinDB: CoinDatabase): CoinDao {
        return coinDB.coinDao()
    }
}