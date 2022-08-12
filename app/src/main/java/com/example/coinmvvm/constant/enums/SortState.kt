package com.example.coinmvvm.constant.enums

import androidx.annotation.DrawableRes
import com.example.coinmvvm.R

enum class SortState {
    // Default
    NO,

    // 이름순 (내림차순, 오름차순)
    NAME_DESC,
    NAME_ASC,

    // 가격순 (내림차순, 오름차순)
    PRICE_DESC,
    PRICE_ASC,

    // 변동률순? (내림차순, 오름차순)
    RATE_DESC,
    RATE_ASC,

    // 거래량순 (내림차순, 오름차순)
    VOLUME_DESC,
    VOLUME_ASC;

    fun formattedTitle(): String {
        return when (this) {
            NAME_DESC, NAME_ASC ->
                "이름"

            PRICE_DESC, PRICE_ASC ->
                "가격"

            RATE_DESC, RATE_ASC ->
                "변동률"

            VOLUME_DESC, VOLUME_ASC ->
                "거래량"

            else ->
                ""
        }
    }

    fun isStateName(): Boolean = when (this) {
        NAME_DESC, NAME_ASC -> true
        else -> false
    }

    @DrawableRes
    fun getStateNameDrawable(): Int = when (this) {
        NAME_ASC -> R.drawable.ic_arrow_up
        else -> R.drawable.ic_arrow_down
    }

    fun isStatePrice(): Boolean = when (this) {
        PRICE_DESC, PRICE_ASC -> true
        else -> false
    }

    @DrawableRes
    fun getStatePriceDrawable(): Int = when (this) {
        PRICE_ASC -> R.drawable.ic_arrow_up
        else -> R.drawable.ic_arrow_down
    }

    fun isStateRate(): Boolean = when (this) {
        RATE_DESC, RATE_ASC -> true
        else -> false
    }

    @DrawableRes
    fun getStateRateDrawable(): Int = when (this) {
        RATE_ASC -> R.drawable.ic_arrow_up
        else -> R.drawable.ic_arrow_down
    }

    fun isStateVolume(): Boolean = when (this) {
        VOLUME_DESC, VOLUME_ASC -> true
        else -> false
    }

    @DrawableRes
    fun getStateVolumeDrawable(): Int = when (this) {
        VOLUME_ASC -> R.drawable.ic_arrow_up
        else -> R.drawable.ic_arrow_down
    }
}