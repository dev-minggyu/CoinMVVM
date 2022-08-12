package com.example.coinmvvm.databinding

import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.coinmvvm.App
import com.example.coinmvvm.R
import com.example.coinmvvm.constant.enums.PriceState
import com.example.coinmvvm.constant.enums.SortState
import com.google.android.material.bottomnavigation.BottomNavigationView

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
    @BindingAdapter("onItemSelectedListener")
    fun bindOnItemSelectedListener(
        view: BottomNavigationView, onNavigationItemSelected: (Int) -> Unit
    ) {
        view.setOnItemSelectedListener { item ->
            onNavigationItemSelected(item.itemId)
            true
        }
    }

    @JvmStatic
    @BindingAdapter("onQueryTextListener")
    fun bindOnQueryTextListener(
        view: SearchView, onQueryTextChange: (String) -> Unit
    ) {
        view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(text: String?): Boolean {
                text?.let {
                    onQueryTextChange(it)
                }
                return true
            }
        })
    }
}

@BindingAdapter("android:src")
fun ImageView.bindSrc(@DrawableRes src: Int) {
    this.post {
        setImageResource(src)
    }
}