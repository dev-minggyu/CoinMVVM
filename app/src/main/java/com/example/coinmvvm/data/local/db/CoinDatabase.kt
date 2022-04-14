package com.example.coinmvvm.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.coinmvvm.data.local.db.entity.FavoriteSymbolEntity

@Database(entities = [FavoriteSymbolEntity::class], version = 1)
abstract class CoinDatabase : RoomDatabase() {

    abstract fun coinDao(): CoinDao

    companion object {

        @Volatile
        private var INSTANCE: CoinDatabase? = null

        private const val DB_FILE_NAME = "Coin.db"

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CoinDatabase::class.java, DB_FILE_NAME
            ).build()
    }
}