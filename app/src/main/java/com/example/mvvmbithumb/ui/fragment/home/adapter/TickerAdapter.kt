package com.example.mvvmbithumb.ui.fragment.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmbithumb.data.model.Ticker
import com.example.mvvmbithumb.databinding.ItemTickerBinding

class TickerAdapter(val clickListener: TickerClickListener) :
    ListAdapter<Ticker, TickerAdapter.TickerViewHolder>(TransactionDiffCallback()) {
    override fun onBindViewHolder(holder: TickerViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TickerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTickerBinding.inflate(layoutInflater, parent, false)
        return TickerViewHolder(binding)
    }

    inner class TickerViewHolder(private val binding: ItemTickerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Ticker) {
            binding.ticker = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    override fun submitList(list: List<Ticker>?) {
        super.submitList(list?.map { it.copy() })
    }
}

class TransactionDiffCallback : DiffUtil.ItemCallback<Ticker>() {
    override fun areItemsTheSame(oldItem: Ticker, newItem: Ticker): Boolean {
        return oldItem.symbol == newItem.symbol
    }

    override fun areContentsTheSame(oldItem: Ticker, newItem: Ticker): Boolean {
        return oldItem.currentPrice == newItem.currentPrice
    }
}

interface TickerClickListener {
    fun onFavorite(symbol: String, isChecked: Boolean)
}
