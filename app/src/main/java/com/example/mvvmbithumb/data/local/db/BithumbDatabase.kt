package com.example.mvvmbithumb.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mvvmbithumb.data.local.db.entity.FavoriteSymbolEntity

@Database(entities = [FavoriteSymbolEntity::class], version = 1)
abstract class BithumbDatabase : RoomDatabase() {

    abstract fun bithumbDao(): BithumbDao

    companion object {

        private var INSTANCE: BithumbDatabase? = null

        private val lock = Any()

        private const val DB_FILE_NAME = "Bithumb.db"

        fun getInstance(context: Context): BithumbDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        BithumbDatabase::class.java, DB_FILE_NAME
                    )
                        .build()
                }
                return INSTANCE!!
            }
        }
    }

}