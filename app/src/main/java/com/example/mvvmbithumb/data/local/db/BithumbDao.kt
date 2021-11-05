package com.example.mvvmbithumb.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvvmbithumb.data.local.db.entity.FavoriteSymbolEntity

@Dao
interface BithumbDao {
    /**
     * @return all tasks.
     */
    @Query("SELECT * FROM favorite_symbol")
    suspend fun getFavoriteSymbols(): List<FavoriteSymbolEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavoriteSymbol(symbol: FavoriteSymbolEntity)

    /**
     * @return the number of favorite deleted. This should always be 1.
     */
    @Query("DELETE FROM favorite_symbol WHERE symbol = :symbol")
    suspend fun deleteFavoriteSymbol(symbol: String): Int

    @Query("DELETE FROM favorite_symbol")
    suspend fun deleteAllFavorite(): Int
}