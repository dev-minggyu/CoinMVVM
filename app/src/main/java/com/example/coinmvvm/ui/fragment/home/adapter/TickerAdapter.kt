package com.example.coinmvvm.ui.fragment.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.coinmvvm.data.model.Ticker
import com.example.coinmvvm.databinding.ItemTickerBinding

class TickerAdapter(val favoriteClickListener: FavoriteClickListener?) :
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
            binding.btnFavorite.setOnClickListener {
                if (binding.btnFavorite.isChecked) {
                    favoriteClickListener?.onAddFavorite(item.getSymbolName())
                } else {
                    favoriteClickListener?.onDeleteFavorite(item.getSymbolName())
                }
            }
            binding.executePendingBindings()
        }
    }

    override fun submitList(list: List<Ticker>?) {
        super.submitList(list?.map { it.copy() })
    }
}

class TransactionDiffCallback : DiffUtil.ItemCallback<Ticker>() {
    override fun areItemsTheSame(oldItem: Ticker, newItem: Ticker): Boolean {
        return oldItem.getSymbolName() == newItem.getSymbolName()
    }

    override fun areContentsTheSame(oldItem: Ticker, newItem: Ticker): Boolean {
        return oldItem == newItem
    }
}

interface FavoriteClickListener {
    // 즐겨찾기 등록
    fun onAddFavorite(symbol: String)
    // 즐겨찾기 해제
    fun onDeleteFavorite(symbol: String)
}
