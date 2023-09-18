package com.vuongvanduy.music.ui.common.bottom_sheet

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.vuongvanduy.music.R
import com.vuongvanduy.music.activity.main.MainActivity
import com.vuongvanduy.music.common.ACTION_START
import com.vuongvanduy.music.common.TITLE_FAVOURITE_SONGS
import com.vuongvanduy.music.common.TITLE_ONLINE_SONGS
import com.vuongvanduy.music.common.isSongExists
import com.vuongvanduy.music.common.sdk33AndUp
import com.vuongvanduy.music.common.sendDataToService
import com.vuongvanduy.music.common.sendListSongToService
import com.vuongvanduy.music.data.models.Song
import com.vuongvanduy.music.databinding.LayoutSongBottomSheetBinding
import com.vuongvanduy.music.ui.common.viewmodel.MainViewModel
import com.vuongvanduy.music.ui.common.viewmodel.SongViewModel

class SongBSDFragment : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutSongBottomSheetBinding

    private lateinit var songViewModel: SongViewModel

    private lateinit var mainViewModel: MainViewModel

    private lateinit var mainActivity: MainActivity

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                songViewModel.optionSong.value?.let { playMusic(it) }
            } else {
                Toast.makeText(
                    requireContext(),
                    "You need allow this app to send notification to start playing music",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_song_bottom_sheet, null)
        binding = LayoutSongBottomSheetBinding.bind(inflater)

        mainActivity = requireActivity() as MainActivity

        songViewModel = ViewModelProvider(mainActivity)[SongViewModel::class.java]
        mainViewModel = ViewModelProvider(mainActivity)[MainViewModel::class.java]


        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setContentView(binding.root)

        setData()

        return bottomSheetDialog
    }

    private fun setData() {
        binding.apply {
            songViewModel.optionSong.value?.let { itSong ->
                song = itSong
                Glide.with(this@SongBSDFragment)
                    .load(Uri.parse(itSong.imageUri))
                    .into(imgSong)
            }
        }
        setUIAddFavourites()
    }

    @SuppressLint("SetTextI18n")
    private fun setUIAddFavourites() {
        binding.apply {
            if (
                isSongExists(
                    songViewModel.favouriteSongs.value,
                    songViewModel.optionSong.value
                )
            ) {
                tvAddFavourites.text = "Remove this song from your favourites"
                imgFavourite.setImageResource(R.drawable.ic_favourite_red)
            } else {
                tvAddFavourites.text = "Add this song to your favourites"
                imgFavourite.setImageResource(R.drawable.ic_favourite_bored)
            }

            layoutAddFavourites.setOnClickListener {
                onClickAddFavourites()
            }

            layoutPlay.setOnClickListener {
                songViewModel.optionSong.value?.let {
                    sdk33AndUp {
                        requestPermissionPostNotification(it)
                    } ?: playMusic(it)
                }
            }
        }
    }

    private fun onClickAddFavourites() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            Toast.makeText(
                mainActivity,
                "You must sign in to use this feature.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        songViewModel.apply {
            if (
                isSongExists(
                    favouriteSongs.value,
                    optionSong.value
                )
            ) {
                optionSong.value?.let {
                    removeSongFromFirebase(it)
                    dismiss()
                }
            } else {
                optionSong.value?.let {
                    addSongToFavourites(it)
                    dismiss()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissionPostNotification(song: Song) {
        if (mainActivity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            playMusic(song)
        } else {
            activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun playMusic(song: Song) {
        dismiss()
        mainViewModel.apply {
            isShowMusicPlayer.postValue(true)
            isServiceRunning.postValue(true)
        }

        when (mainActivity.binding.bottomNav.selectedItemId) {
            R.id.online -> {
                mainViewModel.currentListName = TITLE_ONLINE_SONGS
                songViewModel.onlineSongs.value?.let { sendListSongToService(mainActivity, it) }
            }


            R.id.favourite -> {
                mainViewModel.currentListName = TITLE_FAVOURITE_SONGS
                songViewModel.favouriteSongs.value?.let { sendListSongToService(mainActivity, it) }
            }

        }

        sendDataToService(mainActivity, song, ACTION_START)
    }
}