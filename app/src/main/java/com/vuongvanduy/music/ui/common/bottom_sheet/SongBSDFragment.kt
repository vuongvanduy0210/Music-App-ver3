package com.vuongvanduy.music.ui.common.bottom_sheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.vuongvanduy.music.R
import com.vuongvanduy.music.common.isSongExists
import com.vuongvanduy.music.data.models.Song
import com.vuongvanduy.music.databinding.LayoutSongBottomSheetBinding
import com.vuongvanduy.music.ui.common.viewmodel.SongViewModel

class SongBSDFragment : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutSongBottomSheetBinding

    private lateinit var songViewModel: SongViewModel

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_song_bottom_sheet, null)
        binding = LayoutSongBottomSheetBinding.bind(inflater)
        songViewModel = ViewModelProvider(requireActivity())[SongViewModel::class.java]
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
        }
    }

    private fun onClickAddFavourites() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            Toast.makeText(
                context,
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
}