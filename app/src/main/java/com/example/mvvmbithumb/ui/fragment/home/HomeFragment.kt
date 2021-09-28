package com.example.mvvmbithumb.ui.fragment.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.mvvmbithumb.R
import com.example.mvvmbithumb.databinding.FragmentHomeBinding
import com.example.mvvmbithumb.ui.base.BaseFragment
import com.example.mvvmbithumb.ui.repository.DataRepository
import com.example.mvvmbithumb.ui.repository.websocket.WSProvider
import com.example.mvvmbithumb.ui.viewmodel.ViewModelFactory

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private lateinit var _homeViewModel: HomeViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory(DataRepository(), WSProvider())
        _homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        _homeViewModel.subscribeToSocketEvents()
    }
}