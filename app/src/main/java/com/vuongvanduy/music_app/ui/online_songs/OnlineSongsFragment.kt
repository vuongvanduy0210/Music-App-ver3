package com.vuongvanduy.music_app.ui.online_songs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.vuongvanduy.music_app.R
import com.vuongvanduy.music_app.base.fragment.BaseFragment
import com.vuongvanduy.music_app.data.common.SongViewModel
import com.vuongvanduy.music_app.databinding.FragmentOnlineSongsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnlineSongsFragment : BaseFragment() {

    private lateinit var binding: FragmentOnlineSongsBinding

    private val songViewModel by viewModels<SongViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        songViewModel.fetchData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnlineSongsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = songViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerObserverFetchDataFinish()
    }

    private fun registerObserverFetchDataFinish() {
        songViewModel.onlineSongs.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                songViewModel.isLoadingOnline.postValue(false)
            }
        }
    }
}