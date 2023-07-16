package com.vuongvanduy.music_app.ui.device_songs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.vuongvanduy.music_app.DataBinderMapperImpl
import com.vuongvanduy.music_app.R
import com.vuongvanduy.music_app.base.fragment.BaseFragment
import com.vuongvanduy.music_app.common.DEVICE_SONGS_FRAGMENT_TAG
import com.vuongvanduy.music_app.common.SETTINGS_FRAGMENT_TAG
import com.vuongvanduy.music_app.data.common.SongViewModel
import com.vuongvanduy.music_app.databinding.FragmentDeviceSongsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeviceSongsFragment : BaseFragment() {

    private lateinit var binding: FragmentDeviceSongsBinding

    private val songViewModel by viewModels<SongViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        songViewModel.fetchData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeviceSongsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = songViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerObserverFetchDataFinish()
    }

    private fun registerObserverFetchDataFinish() {
        songViewModel.deviceSongs.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                songViewModel.isLoadingDevice.postValue(false)
                log(DEVICE_SONGS_FRAGMENT_TAG, songViewModel.deviceSongs.value.toString())
            }
        }
    }
}