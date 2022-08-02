package com.example.coinmvvm.ui.fragment.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.coinmvvm.App
import com.example.coinmvvm.R
import com.example.coinmvvm.databinding.ItemTickerListBinding

class CoinListPagerAdapter(
    private val mainAdapter: TickerAdapter,
    private val favoriteAdapter: TickerAdapter
) : RecyclerView.Adapter<CoinListPagerAdapter.TickerListsViewHolder>() {

    override fun getItemCount(): Int {
        return VIEW_LIST_COUNT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TickerListsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTickerListBinding.inflate(layoutInflater, parent, false)

        binding.tickerList.apply {
            itemAnimator = null
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }

        return TickerListsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TickerListsViewHolder, position: Int) {
        when (position) {
            VIEW_LIST_MAIN -> holder.setListAdapter(mainAdapter)
            VIEW_LIST_FAVORITE -> holder.setListAdapter(favoriteAdapter)
        }
    }

    fun getListTitle(position: Int): String {
        return when (position) {
            VIEW_LIST_MAIN -> App.getString(R.string.tab_coinlist_all)
            VIEW_LIST_FAVORITE -> App.getString(R.string.tab_coinlist_favorite)
            else -> ""
        }
    }

    class TickerListsViewHolder(private val binding: ItemTickerListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setListAdapter(adapter: TickerAdapter) {
            binding.tickerList.adapter = adapter
        }
    }

    companion object {
        const val VIEW_LIST_MAIN = 0
        const val VIEW_LIST_FAVORITE = 1
        const val VIEW_LIST_COUNT = 2
    }
}