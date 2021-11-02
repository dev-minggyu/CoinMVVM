package com.example.mvvmbithumb.databinding

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.mvvmbithumb.R
import com.example.mvvmbithumb.constant.enums.PriceState

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("priceColor")
    fun bindPriceColor(
        view: TextView,
        priceState: PriceState
    ) {
        val color: Int = when (priceState) {
            PriceState.SAME -> {
                ContextCompat.getColor(view.context, R.color.color_price_same)
            }
            PriceState.UP -> {
                ContextCompat.getColor(view.context, R.color.color_price_up)
            }
            PriceState.DOWN -> {
                ContextCompat.getColor(view.context, R.color.color_price_down)
            }
        }
        view.setTextColor(color)
    }
}