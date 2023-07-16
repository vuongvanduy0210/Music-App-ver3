package com.vuongvanduy.music_app.ui.favourite_songs

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
import androidx.fragment.app.viewModels
import com.vuongvanduy.music_app.base.fragment.BaseFragment
import com.vuongvanduy.music_app.data.common.SongViewModel
import com.vuongvanduy.music_app.databinding.FragmentFavouriteSongsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouriteSongsFragment : BaseFragment() {

    private lateinit var binding: FragmentFavouriteSongsBinding

    private val songViewModel by viewModels<SongViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        songViewModel.fetchData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteSongsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = songViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerObserverFetchDataFinish()
    }

    private fun registerObserverFetchDataFinish() {
        songViewModel.favouriteSongs.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                songViewModel.isLoadingFavourite.postValue(false)
            }
        }
    }
}