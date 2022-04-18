package com.example.coinmvvm.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.coinmvvm.data.local.db.entity.FavoriteSymbolEntity

@Database(entities = [FavoriteSymbolEntity::class], version = 1, exportSchema = false)
abstract class CoinDatabase : RoomDatabase() {

    abstract fun coinDao(): CoinDao

    companion object {
        const val DB_FILE_NAME = "Coin.db"
    }
}