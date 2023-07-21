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

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.vuongvanduy.music_app.R
import com.vuongvanduy.music_app.common.*
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
                Toast.makeText(
                    this,
                    "You need allow this app to access music and audio to get music on device",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private val activityResultLauncherNotification =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                playMusic()
            } else {
                Toast.makeText(
                    this,
                    "You need allow this app to send notification to start playing music",
                    Toast.LENGTH_LONG
                ).show()
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
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO)
                == PackageManager.PERMISSION_GRANTED
            ) {
                songViewModel.getLocalData()
            } else {
                activityResultLauncherGetData.launch(Manifest.permission.READ_MEDIA_AUDIO)
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
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
                R.id.settings -> setLayout(4, getColor(R.color.yellow), TITLE_SETTINGS)
            }
            true
        }
        binding.bottomNav.selectedItemId = R.id.home
    }

    private fun setLayout(item: Int, color: Int, title: String) {
        binding.viewPager.currentItem = item
        binding.bottomNav.setBackgroundColor(color)
        binding.toolBarTitle.text = title
        when (item) {
            1, 2, 3 -> mainViewModel.isShowBtPlayAll.postValue(true)
            else -> mainViewModel.isShowBtPlayAll.postValue(false)
        }
    }

    private fun registerButtonListener() {
        binding.apply {
            imgBack.setOnClickListener {
                onBackPressedCallback.handleOnBackPressed()
            }

            imgPlay.setOnClickListener {
                if (mainViewModel.isPlaying.value == true) {
                    sendActionToService(this@MainActivity, ACTION_PAUSE)
                } else {
                    sendActionToService(this@MainActivity, ACTION_RESUME)
                }
            }

            imgPrevious.setOnClickListener {
                sendActionToService(this@MainActivity, ACTION_PREVIOUS)
            }

            imgNext.setOnClickListener {
                sendActionToService(this@MainActivity, ACTION_NEXT)
            }

            imgClear.setOnClickListener {
                sendActionToService(this@MainActivity, ACTION_CLEAR)
            }

            miniPlayer.setOnClickListener {
                mainViewModel.isShowMusicPlayer.postValue(true)
                if (mainViewModel.isPlaying.value == false) {
                    sendActionToService(this@MainActivity, ACTION_RESUME)
                }
            }

            btPlayAll.setOnClickListener {
                requestPermissionPostNotification()
            }

            layoutMain.setOnClickListener {
                hideKeyboard(this@MainActivity, binding.root)
            }
        }
    }

    private fun requestPermissionPostNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
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

    @SuppressLint("CommitTransaction")
    private fun registerObserver() {
        mainViewModel.apply {
            isServiceRunning.observe(this@MainActivity) { isRunning ->
                isShowMiniPlayer.postValue(isRunning && isShowMusicPlayer.value == false)
            }

            isShowMusicPlayer.observe(this@MainActivity) { isShow ->
                if (!isShow) {

                    isShowMiniPlayer.postValue(isServiceRunning.value)

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
                    isShowBtPlayAll.postValue(false)

                    binding.toolBarTitle.text = TITLE_MUSIC_PLAYER

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
                if (it) {
                    binding.imgPlay.setImageResource(R.drawable.ic_pause)
                } else {
                    binding.imgPlay.setImageResource(R.drawable.ic_play)
                }
            }

            currentTime.observe(this@MainActivity) {
                val current = mainViewModel.currentTime.value
                if (current != null) {
                    binding.progressBar.progress = current
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
            R.id.settings -> setToolbarTitleSettings()
        }
    }

    private fun setToolbarTitleSettings() {
        if (isFragmentInBackStack(R.id.accountFragment)) {
            binding.toolBarTitle.text = TITLE_ACCOUNT
        } else if (isFragmentInBackStack(R.id.appearanceFragment)) {
            binding.toolBarTitle.text = TITLE_APPEARANCE
        } else if (isFragmentInBackStack(R.id.appInfoFragment)) {
            binding.toolBarTitle.text = TITLE_APP_INFO
        } else if (isFragmentInBackStack(R.id.contactFragment)) {
            binding.toolBarTitle.text = TITLE_CONTACT
        } else {
            binding.toolBarTitle.text = TITLE_SETTINGS
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
        binding.progressBar.isEnabled = false
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
                } else if (binding.bottomNav.selectedItemId != R.id.home) {

                    if (isSettingsFragment()) {

                        val navController = findNavController(R.id.nav_host_fragment)
                        if (!navController.popBackStack()) {
                            binding.bottomNav.selectedItemId = R.id.home
                        } else {
                            if (isSettingOptionsFragment()) {
                                binding.toolBarTitle.text = TITLE_SETTINGS
                            }
                        }
                    } else {
                        binding.bottomNav.selectedItemId = R.id.home
                    }
                } else {
                    finish()
                }
            }
        }
    }

    private fun isSettingsFragment() = binding.bottomNav.selectedItemId == R.id.settings

    private fun isSettingOptionsFragment(): Boolean {
        if (!isSettingsFragment()) {
            return false
        }
        val navController = findNavController(R.id.nav_host_fragment)
        return (navController.currentDestination
                == navController.findDestination(R.id.settingOptionsFragment))
    }

    private fun isFragmentInBackStack(destinationId: Int) =
        try {
            findNavController(R.id.nav_host_fragment).getBackStackEntry(destinationId)
            true
        } catch (e: Exception) {
            false
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