package com.example.coinmvvm.constant.enums

import com.example.coinmvvm.R

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
    val type: SortType
) {
    fun getArrowRes(category: SortCategory): Int {
        return when {
            this.category == category ->
                when (type) {
                    SortType.NO -> R.drawable.ic_arrow_normal
                    SortType.DESC -> R.drawable.ic_arrow_down
                    SortType.ASC -> R.drawable.ic_arrow_up
                }
            else ->
                R.drawable.ic_arrow_normal
        }
    }
}