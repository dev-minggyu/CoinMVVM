package com.example.mvvmbithumb.ui.fragment.home.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mvvmbithumb.R
import com.example.mvvmbithumb.databinding.DialogRetryBinding
import com.example.mvvmbithumb.extension.getViewModelFactory
import com.example.mvvmbithumb.ui.base.BaseDialogFragment
import com.example.mvvmbithumb.ui.fragment.home.HomeViewModel

class RetryDialog(private val reason: String) :
    BaseDialogFragment<DialogRetryBinding>(R.layout.dialog_retry) {
    private val _sharedViewModel: HomeViewModel by viewModels(
        { requireParentFragment() },
        { getViewModelFactory() }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.tvReason.text = reason

        dataBinding.btnCancel.setOnClickListener { dismiss() }

        dataBinding.btnRetry.setOnClickListener {
            _sharedViewModel.doRetryListenPrice()
            dismiss()
        }
    }
}