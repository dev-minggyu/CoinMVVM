package com.example.coinmvvm.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.example.coinmvvm.R
import com.example.coinmvvm.constant.enums.SortCategory
import com.example.coinmvvm.constant.enums.SortType
import com.example.coinmvvm.databinding.ButtonSortBinding
import com.example.coinmvvm.extension.getEnum

class SortButton(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private lateinit var _dataBinding: ButtonSortBinding

    private lateinit var _sortCategory: SortCategory

    private var _onSortChangedListener: OnSortChangedListener? = null

    private var _sortType = SortType.NO

    init {
        init(context)
        getAttrs(attrs)
        setListener()
    }

    private fun init(context: Context?) {
        _dataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.button_sort,
            this,
            false
        )
        addView(_dataBinding.root)
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SortButton)

        _sortCategory = typedArray.getEnum(R.styleable.SortButton_sortCategory)
        _dataBinding.tvSortName.apply {
            text = when (_sortCategory) {
                SortCategory.NAME -> context.getString(R.string.sort_coin_name)
                SortCategory.PRICE -> context.getString(R.string.sort_coin_price)
                SortCategory.RATE -> context.getString(R.string.sort_coin_rate)
                SortCategory.VOLUME -> context.getString(R.string.sort_coin_volume)
            }
        }

        typedArray.recycle()
    }

    private fun setListener() {
        setOnClickListener {
            _dataBinding.ivSortArrow.apply {
                _sortType = when (_sortType) {
                    SortType.NO -> SortType.DESC
                    SortType.DESC -> SortType.ASC
                    SortType.ASC -> SortType.NO
                }
                _onSortChangedListener?.onChanged(_sortCategory, _sortType)
            }
        }
    }

    fun setArrowDrawable(res: Int) {
        _dataBinding.ivSortArrow.setImageResource(res)
    }

    fun setOnSortChangedListener(listener: OnSortChangedListener) {
        _onSortChangedListener = listener
    }

    interface OnSortChangedListener {
        fun onChanged(sortCategory: SortCategory, sortType: SortType)
    }
}