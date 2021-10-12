package com.example.mvvmbithumb.ui.activity

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mvvmbithumb.R
import com.example.mvvmbithumb.databinding.ActivityMainBinding
import com.example.mvvmbithumb.ui.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private lateinit var _navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupNavigation()
    }

    private fun setupNavigation() {
        _navController = findNavController(R.id.nav_host_fragment)
        _dataBinding.navBottom.setupWithNavController(_navController)
    }
}