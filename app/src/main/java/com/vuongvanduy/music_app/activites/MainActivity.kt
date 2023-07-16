package com.vuongvanduy.music_app.activites

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vuongvanduy.music_app.R
import com.vuongvanduy.music_app.common.*
import com.vuongvanduy.music_app.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBottomNavigation()
    }

    private fun setBottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.apply {
                when (destination.id) {
                    R.id.homeFragment -> setLayout(getColor(R.color.teal_200), TITLE_HOME)
                    R.id.onlineSongsFragment -> setLayout(
                        getColor(R.color.orange),
                        TITLE_ONLINE_SONGS
                    )

                    R.id.favouriteSongsFragment -> setLayout(
                        getColor(R.color.red),
                        TITLE_FAVOURITE_SONGS
                    )

                    R.id.deviceSongsFragment -> setLayout(
                        getColor(R.color.blueLight),
                        TITLE_DEVICE_SONGS
                    )

                    R.id.settingsFragment -> setLayout(
                        getColor(R.color.purple_200),
                        TITLE_SETTINGS
                    )
                }
            }
        }
        binding.bottomNav.setupWithNavController(navController)
    }

    private fun setLayout(color: Int, title: String) {
        binding.bottomNav.setBackgroundColor(color)
        binding.toolBarTitle.text = title
    }

}