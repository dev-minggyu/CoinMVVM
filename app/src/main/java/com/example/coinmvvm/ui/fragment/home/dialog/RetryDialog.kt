package com.example.coinmvvm.ui.fragment.home.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.coinmvvm.R
import com.example.coinmvvm.databinding.DialogRetryBinding
import com.example.coinmvvm.ui.base.BaseDialogFragment
import com.example.coinmvvm.ui.fragment.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RetryDialog(private val reason: String) :
    BaseDialogFragment<DialogRetryBinding>(R.layout.dialog_retry) {
    private val _sharedViewModel: HomeViewModel by viewModels(ownerProducer = { requireParentFragment() })

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