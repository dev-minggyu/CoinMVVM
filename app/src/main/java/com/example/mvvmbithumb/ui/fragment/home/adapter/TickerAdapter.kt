package com.example.mvvmbithumb.ui.fragment.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmbithumb.data.model.Ticker
import com.example.mvvmbithumb.databinding.ItemTickerBinding
import com.example.mvvmbithumb.data.model.TickerContent

class TickerAdapter : ListAdapter<Ticker, TickerAdapter.ViewHolder>(TransactionDiffCallback()) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTickerBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    class ViewHolder(private val binding: ItemTickerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Ticker) {
            binding.ticker = item
            binding.executePendingBindings()
        }
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
