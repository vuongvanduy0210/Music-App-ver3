package com.vuongvanduy.music.ui.favourite_songs

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vuongvanduy.music.base.fragment.BaseFragment
import com.vuongvanduy.music.common.ACTION_START
import com.vuongvanduy.music.common.TITLE_FAVOURITE_SONGS
import com.vuongvanduy.music.common.hideKeyboard
import com.vuongvanduy.music.common.isSongExists
import com.vuongvanduy.music.common.sendDataToService
import com.vuongvanduy.music.common.sendListSongToService
import com.vuongvanduy.music.data.models.Song
import com.vuongvanduy.music.databinding.FragmentFavouriteSongsBinding
import com.vuongvanduy.music.ui.common.adapter.ExtendSongAdapter
import com.vuongvanduy.music.ui.common.myinterface.IClickSongListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouriteSongsFragment : BaseFragment() {

    private lateinit var binding: FragmentFavouriteSongsBinding

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                mainViewModel.currentSong.value?.let { playMusic(it) }
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
        binding = FragmentFavouriteSongsBinding.inflate(inflater, container, false)
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

        setRecyclerViewSongs()

        registerObserverFetchDataFinish()

        setOnClickBtSearchView()
    }

    private fun setRecyclerViewSongs() {
        extendSongAdapter = ExtendSongAdapter(object : IClickSongListener {
            override fun onClickSong(song: Song) {
                mainViewModel.currentSong.postValue(song)
                requestPermissionPostNotification(song)
            }

            override fun onClickExtendFavourites(song: Song) {
                songViewModel.removeSongFromFirebase(song)
            }

        }, TITLE_FAVOURITE_SONGS)
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.rcvListSongs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(decoration)
            adapter = extendSongAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        hideKeyboard(requireContext(), binding.root)
                    }
                }
            })
        }
    }

    private fun requestPermissionPostNotification(song: Song) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (mainActivity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                playMusic(song)
            } else {
                activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            playMusic(song)
        }
    }

    private fun playMusic(song: Song) {
        mainViewModel.apply {
            isShowMusicPlayer.postValue(true)
            isServiceRunning.postValue(true)
            currentListName = TITLE_FAVOURITE_SONGS
        }
        songViewModel.favouriteSongs.value?.let { sendListSongToService(mainActivity, it) }
        sendDataToService(mainActivity, song, ACTION_START)
    }

    private fun registerObserverFetchDataFinish() {
        songViewModel.favouriteSongs.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                songViewModel.isLoadingFavourite.postValue(false)
                extendSongAdapter.setData(it)
                if (mainViewModel.currentListName == TITLE_FAVOURITE_SONGS) {
                    sendListSongToService(mainActivity, it)
                }
            } else {
                songViewModel.isLoadingFavourite.postValue(true)

            }
        }

        songViewModel.favSong.observe(viewLifecycleOwner) {
            if (it != null) {
                if (songViewModel.favouriteSongs.value != null
                    && isSongExists(songViewModel.favouriteSongs.value!!, it)
                ) {
                    Toast.makeText(
                        mainActivity,
                        "This song is exist in favourites", Toast.LENGTH_SHORT
                    ).show()
                    return@observe
                }

                songViewModel.addSongToFavourites(it)

                Toast.makeText(
                    mainActivity,
                    "Add song to favourites success", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setOnClickBtSearchView() {
        binding.imgClear.apply {
            setOnClickListener {
                binding.edtSearch.setText("")
            }
        }
        binding.edtSearch.apply {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard(requireContext(), binding.root)
                    return@setOnEditorActionListener true
                }
                false
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    extendSongAdapter.filter.filter(s)
                }
            })
        }
    }
}