package com.example.coinmvvm.databinding

import android.widget.SearchView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.coinmvvm.R
import com.example.coinmvvm.constant.enums.PriceState
import com.example.coinmvvm.constant.enums.SortCategory
import com.example.coinmvvm.constant.enums.SortModel
import com.example.coinmvvm.constant.enums.SortType
import com.example.coinmvvm.ui.custom.SortButton
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
        view: BottomNavigationView, function: (Int) -> Unit
    ) {
        view.setOnItemSelectedListener { item ->
            function(item.itemId)
            true
        }
    }

    @JvmStatic
    @BindingAdapter("onQueryTextListener")
    fun bindOnQueryTextListener(
        view: SearchView, function: (String) -> Unit
    ) {
        view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(text: String?): Boolean {
                text?.let {
                    function(it)
                }
                return true
            }
        })
    }

    @JvmStatic
    @BindingAdapter("onSortChangedListener")
    fun bindOnSortChangedListener(
        view: SortButton, function: (SortModel) -> Unit
    ) {
        view.setOnSortChangedListener(object : SortButton.OnSortChangedListener {
            override fun onChanged(sortCategory: SortCategory, sortType: SortType) {
                function(SortModel(sortCategory, sortType))
            }
        })
    }

    @JvmStatic
    @BindingAdapter("sortArrow")
    fun bindSortArrowDrawable(
        view: SortButton, res: Int
    ) {
        view.setArrowDrawable(res)
    }
}