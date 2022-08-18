package com.example.coinmvvm.constant.enums

enum class SortType {
    NO,
    DESC,
    ASC
}

enum class SortCategory(val id: Int) {
    NAME(0),
    PRICE(1),
    RATE(2),
    VOLUME(3)
}

data class SortModel(
    val category: SortCategory,
    val type: SortType = SortType.NO
)