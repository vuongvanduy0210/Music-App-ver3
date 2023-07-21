package com.vuongvanduy.music_app.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.vuongvanduy.music_app.R
import com.vuongvanduy.music_app.base.fragment.BaseFragment
import com.vuongvanduy.music_app.common.ACTION_START
import com.vuongvanduy.music_app.common.FAVOURITE_SONGS_FRAGMENT_TAG
import com.vuongvanduy.music_app.common.HOME_FRAGMENT_TAG
import com.vuongvanduy.music_app.common.TITLE_DEVICE_SONGS
import com.vuongvanduy.music_app.common.TITLE_FAVOURITE_SONGS
import com.vuongvanduy.music_app.common.TITLE_ONLINE_SONGS
import com.vuongvanduy.music_app.common.sendDataToService
import com.vuongvanduy.music_app.common.sendListSongToService
import com.vuongvanduy.music_app.data.models.Song
import com.vuongvanduy.music_app.databinding.FragmentHomeBinding
import com.vuongvanduy.music_app.ui.transformer.DepthPageTransformer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var photosAdapter: PhotoViewPager2Adapter

    private lateinit var categoryAdapter: CategoryAdapter

    private var myHandler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        binding.slideImage.apply {
            val count = songViewModel.photos.value?.size
            if (count != null) {
                if (currentItem == count - 1) {
                    currentItem = 0
                } else {
                    currentItem += 1
                }
            }
        }
    }

    private val activityResultLauncherNotification =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                mainViewModel.currentSong.value?.let {song ->
                    mainViewModel.currentListName?.let { titleList ->
                        playMusic(song, titleList)
                    }
                }
            } else {
                Toast.makeText(
                    mainActivity,
                    "You need allow this app to send notification to start playing music",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun init() {
        super.init()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = songViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerViewCategory()

        registerObserver()

        setAutoSlideImage()
    }

    private fun registerObserver() {
        songViewModel.apply {
            onlineSongs.observe(mainActivity) {
                getListPhotos()
                categoryAdapter.setData(getListCategories())
            }
            favouriteSongs.observe(mainActivity) {
                if (it.isNullOrEmpty()) {
                    favouriteSongsShow.value = null
                }
                categoryAdapter.setData(getListCategories())
            }
            deviceSongs.observe(mainActivity) {
                getListPhotos()
                categoryAdapter.setData(getListCategories())
            }
        }
    }

    private fun setRecyclerViewCategory() {
        categoryAdapter = CategoryAdapter(
            mainActivity,
            object : IClickCategoryListener {
                override fun clickButtonViewAll(categoryName: String) {
                    gotoViewAll(categoryName)
                }

                override fun onClickSong(song: Song, categoryName: String) {
                    mainViewModel.currentSong.postValue(song)
                    playSong(song, categoryName)
                }
            })
        binding.rcvCategory.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = categoryAdapter
        }
    }

    private fun gotoViewAll(categoryName: String) {
        when (categoryName) {
            TITLE_ONLINE_SONGS -> {
                mainActivity.binding.bottomNav.selectedItemId = R.id.online
            }

            TITLE_FAVOURITE_SONGS -> {
                mainActivity.binding.bottomNav.selectedItemId = R.id.favourite
            }

            TITLE_DEVICE_SONGS -> {
                mainActivity.binding.bottomNav.selectedItemId = R.id.device
            }
        }
    }

    private fun playSong(song: Song, categoryName: String) {
        mainViewModel.currentListName = categoryName
        requestPermissionPostNotification(song, categoryName)
    }

    private fun requestPermissionPostNotification(song: Song, categoryName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (mainActivity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                playMusic(song, categoryName)
            } else {
                activityResultLauncherNotification.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            playMusic(song, categoryName)
        }
    }

    private fun playMusic(song: Song, categoryName: String) {
        mainViewModel.isShowMusicPlayer.postValue(true)
        mainViewModel.isServiceRunning.postValue(true)
        when (categoryName) {
            TITLE_ONLINE_SONGS -> {
                songViewModel.onlineSongs.value?.let { sendListSongToService(mainActivity, it) }
            }

            TITLE_FAVOURITE_SONGS -> {
                songViewModel.favouriteSongs.value?.let { sendListSongToService(mainActivity, it) }
            }

            TITLE_DEVICE_SONGS -> {
                songViewModel.deviceSongs.value?.let { sendListSongToService(mainActivity, it) }
            }
        }
        sendDataToService(mainActivity, song, ACTION_START)
    }

    private fun setAutoSlideImage() {
        Glide.with(mainActivity).load(R.drawable.img_home).into(binding.imgBg)
        songViewModel.photos.observe(mainActivity) {
            if (!it.isNullOrEmpty()) {
                binding.slideImage.visibility = View.VISIBLE
                photosAdapter = PhotoViewPager2Adapter(it, mainActivity)

                binding.slideImage.apply {
                    adapter = photosAdapter
                    setPageTransformer(DepthPageTransformer())

                    binding.circleIndicator.setViewPager(this)

                    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            myHandler.removeCallbacks(runnable)
                            myHandler.postDelayed(runnable, 2500)
                        }
                    })
                }
            } else {
                binding.slideImage.visibility = View.GONE
            }
        }
    }
}