package com.example.coinmvvm.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_symbol",
    indices = [Index(value = ["symbol"], unique = true)]
)
data class FavoriteSymbolEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "symbol")
    var symbol: String = ""
)