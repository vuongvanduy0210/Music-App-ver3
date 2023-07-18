package com.vuongvanduy.music_app.activites.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.vuongvanduy.music_app.R
import com.vuongvanduy.music_app.common.*
import com.vuongvanduy.music_app.data.common.sortListAscending
import com.vuongvanduy.music_app.data.models.Song
import com.vuongvanduy.music_app.databinding.ActivityMainBinding
import com.vuongvanduy.music_app.ui.common.adapter.FragmentViewPagerAdapter
import com.vuongvanduy.music_app.ui.common.viewmodel.SongViewModel
import com.vuongvanduy.music_app.ui.music_player.MusicPlayerFragment
import com.vuongvanduy.music_app.ui.transformer.ZoomOutPageTransformer
import dagger.hilt.android.AndroidEntryPoint
import java.util.Random

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var mainViewModel: MainViewModel

    private lateinit var songViewModel: SongViewModel

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

    private val activityResultLauncherGetData =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                songViewModel.getLocalData()
                Toast.makeText(
                    this,
                    "Get music from your phone success",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Log.e("FRAGMENT_NAME", "Permission denied")
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

        checkServiceIsRunning()

        setBottomNavigationWithViewPager()

        registerButtonListener()

        registerObserver()

        onBackPressCallBack()
    }

    private fun init() {
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        songViewModel = ViewModelProvider(this)[SongViewModel::class.java]
        songViewModel.fetchData()
        requestPermissionReadStorage()
        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(serviceReceiver, IntentFilter(SEND_DATA))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(currentTimeReceiver, IntentFilter(SEND_CURRENT_TIME))
    }

    private fun requestPermissionReadStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                // get list currentSong from device and send to music device fragment
                songViewModel.getLocalData()
            } else {
                activityResultLauncherGetData.launch(Manifest.permission.READ_MEDIA_AUDIO)
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // get list currentSong from device and send to music device fragment
                songViewModel.getLocalData()
            } else {
                activityResultLauncherGetData.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun checkServiceIsRunning() {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)
        for (serviceInfo in runningServices) {
            if (serviceInfo.service.className
                == "com.vuongvanduy.music_app.MusicService"
            ) {
                sendActionToService(this, ACTION_RELOAD_DATA)
                break
            }
        }
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
        when (item) {
            1, 2, 3 -> mainViewModel.isShowBtPlayAll.postValue(true)
            else -> mainViewModel.isShowBtPlayAll.postValue(false)
        }
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
            sendActionToService(this, ACTION_PREVIOUS)
        }
        binding.imgNext.setOnClickListener {
            sendActionToService(this, ACTION_NEXT)
        }
        binding.imgClear.setOnClickListener {
            sendActionToService(this, ACTION_CLEAR)
        }
        binding.miniPlayer.setOnClickListener {
            mainViewModel.isShowMusicPlayer.postValue(true)
            if (mainViewModel.isPlaying.value == false) {
                sendActionToService(this, ACTION_RESUME)
            }
        }
        binding.btPlayAll.setOnClickListener {
            requestPermissionPostNotification()
        }
    }

    private fun requestPermissionPostNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                playMusic()
            } else {
                activityResultLauncherNotification.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            playMusic()
        }
    }

    private fun playMusic() {
        when (binding.bottomNav.selectedItemId) {
            R.id.online -> {

                if (!songViewModel.onlineSongs.value.isNullOrEmpty()) {
                    mainViewModel.currentListName = TITLE_ONLINE_SONGS
                    val list = songViewModel.onlineSongs.value as MutableList<Song>
                    playList(list)
                }
            }

            R.id.favourite -> {
                if (!songViewModel.favouriteSongs.value.isNullOrEmpty()) {
                    mainViewModel.currentListName = TITLE_FAVOURITE_SONGS
                    val list = songViewModel.favouriteSongs.value as MutableList<Song>
                    playList(list)
                }
            }

            R.id.device -> {
                if (!songViewModel.deviceSongs.value.isNullOrEmpty()) {
                    mainViewModel.currentListName = TITLE_DEVICE_SONGS
                    val list = songViewModel.deviceSongs.value as MutableList<Song>
                    playList(list)
                }
            }

            else -> {
            }
        }
    }

    private fun playList(list: MutableList<Song>) {
        val index: Int = if (mainViewModel.isShuffling.value == true) {
            Random().nextInt(list.size)
        } else {
            0
        }
        sendListSongToService(this, list)
        sendDataToService(this, list[index], ACTION_START)
        mainViewModel.isShowMusicPlayer.postValue(true)
        mainViewModel.isServiceRunning.postValue(true)
    }

    private val activityResultLauncherNotification =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                playMusic()
            } else {
                Log.e("FRAGMENT_NAME", "Permission denied")
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
                    when (binding.bottomNav.selectedItemId) {
                        R.id.online, R.id.favourite, R.id.device ->
                            isShowBtPlayAll.postValue(true)
                    }
                } else {
                    //replace fragment
                    replaceMusicPlayer()
                    isShowMiniPlayer.postValue(false)
                    binding.toolBarTitle.text = TITLE_MUSIC_PLAYER
                    isShowBtPlayAll.postValue(false)
                    hideKeyboard(this@MainActivity, binding.root)
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
                        isServiceRunning.postValue(true)
                    }

                    else -> {}
                }
            }

            isPlaying.observe(this@MainActivity) {
                Log.e(MAIN_ACTIVITY_TAG, "isPlaying $it")
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