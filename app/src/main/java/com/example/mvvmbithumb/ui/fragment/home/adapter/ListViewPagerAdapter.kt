package com.example.mvvmbithumb.ui.fragment.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmbithumb.databinding.ItemTickerListsBinding

class ListViewPagerAdapter(
    private val mainAdapter: TickerAdapter,
    private val favoriteAdapter: TickerAdapter
) : RecyclerView.Adapter<ListViewPagerAdapter.TickerListsViewHolder>() {

    override fun getItemCount(): Int {
        return VIEW_LIST_COUNT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TickerListsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTickerListsBinding.inflate(layoutInflater, parent, false)
        return TickerListsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TickerListsViewHolder, position: Int) {
        when (position) {
            VIEW_LIST_MAIN -> holder.setListAdapter(mainAdapter)
            VIEW_LIST_FAVORITE -> holder.setListAdapter(favoriteAdapter)
        }
    }

    class TickerListsViewHolder(private val binding: ItemTickerListsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setListAdapter(adapter: TickerAdapter) {
            binding.tickerList.adapter = adapter
        }
    }

    companion object {
        @JvmStatic
        val VIEW_LIST_COUNT = 2
        const val VIEW_LIST_MAIN = 0
        const val VIEW_LIST_FAVORITE = 1
    }
}