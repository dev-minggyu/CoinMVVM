package com.example.coinmvvm.databinding

import android.widget.SearchView
import android.widget.TextView
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

    @JvmStatic
    @BindingAdapter("sortClick")
    fun bindSortClickListener(
        view: TextView, onTickerSortClick: (SortState) -> Unit
    ) {
        view.setOnClickListener {
            when (view.text.toString()) {
                App.getString(R.string.sort_coin_name_no) -> onTickerSortClick(SortState.NAME_DESC)
                App.getString(R.string.sort_coin_name_desc) -> onTickerSortClick(SortState.NAME_ASC)
                App.getString(R.string.sort_coin_name_asc) -> onTickerSortClick(SortState.NO)

                App.getString(R.string.sort_coin_price_no) -> onTickerSortClick(SortState.PRICE_DESC)
                App.getString(R.string.sort_coin_price_desc) -> onTickerSortClick(SortState.PRICE_ASC)
                App.getString(R.string.sort_coin_price_asc) -> onTickerSortClick(SortState.NO)

                App.getString(R.string.sort_coin_rate_no) -> onTickerSortClick(SortState.RATE_DESC)
                App.getString(R.string.sort_coin_rate_desc) -> onTickerSortClick(SortState.RATE_ASC)
                App.getString(R.string.sort_coin_rate_asc) -> onTickerSortClick(SortState.NO)

                App.getString(R.string.sort_coin_volume_no) -> onTickerSortClick(SortState.VOLUME_DESC)
                App.getString(R.string.sort_coin_volume_desc) -> onTickerSortClick(SortState.VOLUME_ASC)
                App.getString(R.string.sort_coin_volume_asc) -> onTickerSortClick(SortState.NO)

            }
        }
    }
}