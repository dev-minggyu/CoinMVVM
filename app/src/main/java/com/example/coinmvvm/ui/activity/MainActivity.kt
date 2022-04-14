package com.example.coinmvvm.ui.activity

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.coinmvvm.R
import com.example.coinmvvm.databinding.ActivityMainBinding
import com.example.coinmvvm.ui.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private lateinit var _navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupNavigation()
    }

    private fun setupNavigation() {
        _navController = findNavController(R.id.nav_host_fragment)
        dataBinding.navBottom.setupWithNavController(_navController)
    }
}