package com.example.coinmvvm.databinding

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.coinmvvm.R
import com.example.coinmvvm.constant.enums.PriceState
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("priceColor")
    fun bindTickerColor(
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

    @JvmStatic
    @BindingAdapter("onNavigationItemSelected")
    fun bindOnNavigationItemSelectedListener(
        view: BottomNavigationView, listener: NavigationBarView.OnItemSelectedListener
    ) {
        view.setOnItemSelectedListener(listener)
    }
}