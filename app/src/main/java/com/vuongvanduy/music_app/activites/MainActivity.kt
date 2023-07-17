package com.vuongvanduy.music_app.activites

import android.annotation.SuppressLint
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.vuongvanduy.music_app.R
import com.vuongvanduy.music_app.common.*
import com.vuongvanduy.music_app.databinding.ActivityMainBinding
import com.vuongvanduy.music_app.ui.common.adapter.FragmentViewPagerAdapter
import com.vuongvanduy.music_app.ui.music_player.MusicPlayerFragment
import com.vuongvanduy.music_app.ui.transformer.ZoomOutPageTransformer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var mainViewModel: MainViewModel

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        init()
        setContentView(binding.root)

        setBottomNavigationWithViewPager()

        registerObserver()

        onBackPressCallBack()
    }

    private fun init() {
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this
    }

    private fun setBottomNavigationWithViewPager() {
        val viewPagerAdapter = FragmentViewPagerAdapter(this)
        binding.viewPager.apply {
            adapter = viewPagerAdapter
            isUserInputEnabled = false
            setPageTransformer(ZoomOutPageTransformer())
        }


        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> setLayout(0, getColor(R.color.teal_200), TITLE_HOME)
                R.id.online -> setLayout(1, getColor(R.color.orange), TITLE_ONLINE_SONGS)
                R.id.favourite -> setLayout(2, getColor(R.color.red), TITLE_FAVOURITE_SONGS)
                R.id.device -> setLayout(3, getColor(R.color.blueLight), TITLE_DEVICE_SONGS)
                R.id.settings -> setLayout(4, getColor(R.color.purple_200), TITLE_SETTINGS)
            }
            true
        }
        binding.bottomNav.selectedItemId = R.id.home
    }

    private fun setLayout(item: Int, color: Int, title: String) {
        mainViewModel.isHome = item == 0
        binding.viewPager.currentItem = item
        binding.bottomNav.setBackgroundColor(color)
        binding.toolBarTitle.text = title
    }

    @SuppressLint("CommitTransaction")
    private fun registerObserver() {
        mainViewModel.apply {
            isServiceRunning.observe(this@MainActivity) {
                if (it) {
                    if (isShowMusicPlayer.value == true) {
                        isShowMiniPlayer.postValue(false)
                    } else {
                        isShowMiniPlayer.postValue(true)
                    }
                } else {
                    isShowMiniPlayer.postValue(false)
                }
            }
            isShowMusicPlayer.observe(this@MainActivity) {
                isShowBottomNav.postValue(!it)
                if (!it) {
                    if (isServiceRunning.value == true) {
                        isShowMiniPlayer.postValue(true)
                    } else {
                        isShowMiniPlayer.postValue(false)
                    }
                    supportFragmentManager.popBackStack()
                } else {
                    //replace fragment
                    replaceMusicPlayer()
                }
            }
        }
    }

    private fun replaceMusicPlayer() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_right)
            .replace(R.id.layout_music_player, MusicPlayerFragment())
            .addToBackStack("MusicPlayerFragment")
            .commit()
    }

    private fun onBackPressCallBack() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mainViewModel.isShowMusicPlayer.value == true) {
                    popMusicPlayer()
                } else if (!mainViewModel.isHome) {
                    binding.bottomNav.selectedItemId = R.id.home
                } else {
                    finish()
                }
            }
        }
    }

    private fun popMusicPlayer() {
        val slideOutAnimation =
            AnimationUtils.loadAnimation(
                this@MainActivity,
                R.anim.slide_out_right
            )
        slideOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                mainViewModel.isShowMusicPlayer.postValue(false)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        binding.layoutMusicPlayer.startAnimation(slideOutAnimation)
    }

    override fun onResume() {
        super.onResume()
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onPause() {
        super.onPause()
        onBackPressedCallback.remove()
    }
}