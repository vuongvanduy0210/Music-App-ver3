package com.vuongvanduy.music_app.activites

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.vuongvanduy.music_app.R
import com.vuongvanduy.music_app.common.*
import com.vuongvanduy.music_app.data.models.Song
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

    private val serviceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                mainViewModel.receiveDataFromReceiver(intent)
            }
        }
    }

    private val currentTimeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                mainViewModel.receiveCurrentTime(intent)
            }
        }
    }

    inner class UpdateSeekBar : Runnable {
        override fun run() {
            val currentTime = mainViewModel.currentTime.value
            if (currentTime != null) {
                binding.progressBar.progress = currentTime
            }
            binding.progressBar.isEnabled = false

            Handler(Looper.myLooper()!!).postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        init()

        setContentView(binding.root)

        setBottomNavigationWithViewPager()

        registerButtonListener()

        registerObserver()

        onBackPressCallBack()
    }

    private fun init() {
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(serviceReceiver, IntentFilter(SEND_DATA))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(currentTimeReceiver, IntentFilter(SEND_CURRENT_TIME))
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

    private fun registerButtonListener() {
        binding.imgBack.setOnClickListener {
            popMusicPlayer()
        }
        binding.imgPlay.setOnClickListener {
            if (mainViewModel.isPlaying.value == true) {
                sendActionToService(this, ACTION_PAUSE)
            } else {
                sendActionToService(this, ACTION_RESUME)
            }
        }
        binding.imgPrevious.setOnClickListener {
            sendActionToService(this, ACTION_PAUSE)
        }
        binding.imgNext.setOnClickListener {
            sendActionToService(this, ACTION_NEXT)
        }
        binding.imgClear.setOnClickListener {
            sendActionToService(this, ACTION_CLEAR)
        }
    }

    @SuppressLint("CommitTransaction")
    private fun registerObserver() {
        mainViewModel.apply {
            isServiceRunning.observe(this@MainActivity) { isRunning ->
                isShowMiniPlayer.postValue(isRunning && isShowMusicPlayer.value == false)
            }

            isShowMusicPlayer.observe(this@MainActivity) { isShow ->
                if (!isShow) {
                    if (isServiceRunning.value == true) {
                        isShowMiniPlayer.postValue(true)
                    } else {
                        isShowMiniPlayer.postValue(false)
                    }
                    supportFragmentManager.popBackStack()
                    setToolbarTitle()
                } else {
                    //replace fragment
                    replaceMusicPlayer()
                    isShowMiniPlayer.postValue(false)
                    binding.toolBarTitle.text = TITLE_MUSIC_PLAYER
                }
            }

            actionMusic.observe(this@MainActivity) { value ->
                when (value) {
                    ACTION_START, ACTION_NEXT, ACTION_PREVIOUS ->
                        currentSong.value?.let {
                            setLayoutMiniPlayer(it)
                        }

                    ACTION_CLEAR -> {
                        isPlaying.postValue(false)
                        isServiceRunning.postValue(false)
                        popMusicPlayer()
                    }

                    ACTION_RELOAD_DATA -> {
                        currentSong.value?.let {
                            setLayoutMiniPlayer(it)
                        }
                        isShowMiniPlayer.postValue(true)
                    }

                    ACTION_OPEN_MUSIC_PLAYER -> {
                        isShowMusicPlayer.postValue(true)
                    }

                    else -> {}
                }
            }

            isPlaying.observe(this@MainActivity) {
                if (it) {
                    binding.imgPlay.setImageResource(R.drawable.ic_pause)
                } else {
                    binding.imgPlay.setImageResource(R.drawable.ic_play)
                }
            }
        }
    }

    private fun setToolbarTitle() {
        when (binding.bottomNav.selectedItemId) {
            R.id.home -> binding.toolBarTitle.text = TITLE_HOME
            R.id.online -> binding.toolBarTitle.text = TITLE_ONLINE_SONGS
            R.id.favourite -> binding.toolBarTitle.text = TITLE_FAVOURITE_SONGS
            R.id.device -> binding.toolBarTitle.text = TITLE_DEVICE_SONGS
            R.id.settings -> binding.toolBarTitle.text = TITLE_SETTINGS
        }
    }

    private fun setLayoutMiniPlayer(song: Song) {

        val imageUri = Uri.parse(song.imageUri)
        // set layout
        binding.apply {
            Glide.with(this@MainActivity).load(imageUri).into(imgMusic)
            tvMusicName.isSelected = true
            tvSinger.isSelected = true
            Glide.with(this@MainActivity).load(imageUri).into(imgBgMiniPlayer)
        }

        val updateSeekBar = UpdateSeekBar()
        Handler(Looper.myLooper()!!).post(updateSeekBar)
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

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(currentTimeReceiver)
    }
}