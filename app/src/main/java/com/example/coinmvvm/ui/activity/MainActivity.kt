package com.example.coinmvvm.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.coinmvvm.R
import com.example.coinmvvm.databinding.ActivityMainBinding
import com.example.coinmvvm.ui.base.BaseActivity
import com.example.coinmvvm.ui.fragment.home.HomeFragment
import com.example.coinmvvm.ui.fragment.setting.SettingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val _mainViewModel: MainViewModel by viewModels()

    companion object {
        private const val TAG_FRAGMENT_HOME = "TAG_FRAGMENT_HOME"
        private const val TAG_FRAGMENT_MY_ASSET = "TAG_FRAGMENT_MY_ASSET"
        private const val TAG_FRAGMENT_SETTING = "TAG_FRAGMENT_SETTING"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding.vm = _mainViewModel

        _mainViewModel.currentMenuId.observe(this) {
            when (it) {
                R.id.home_fragment -> showHomeFragment()
                R.id.my_asset_fragment -> showMyAssetFragment()
                R.id.setting_fragment -> showSettingFragment()
            }
        }
    }

    private fun showHomeFragment() {
        showFragment(TAG_FRAGMENT_HOME) {
            HomeFragment()
        }
    }

    private fun showMyAssetFragment() {
        showFragment(TAG_FRAGMENT_MY_ASSET) {
            HomeFragment()
        }
    }

    private fun showSettingFragment() {
        showFragment(TAG_FRAGMENT_SETTING) {
            SettingFragment()
        }
    }

    private fun showFragment(tag: String, fragment: () -> Fragment) {
        supportFragmentManager.run {
            val transaction = beginTransaction()

            if (findFragmentByTag(tag) == null) {
                transaction.add(dataBinding.navHostFragment.id, fragment.invoke(), tag)
            }

            fragments.forEach {
                if (it.tag != tag) {
                    transaction.hide(it)
                } else {
                    transaction.show(it)
                }
            }

            transaction.commit()
        }
    }
}