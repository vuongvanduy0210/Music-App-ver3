package com.vuongvanduy.music_app.base.fragment

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.vuongvanduy.music_app.activites.MainActivity
import com.vuongvanduy.music_app.activites.MainViewModel
import com.vuongvanduy.music_app.ui.common.adapter.ExtendSongAdapter
import com.vuongvanduy.music_app.ui.common.adapter.SongAdapter
import com.vuongvanduy.music_app.ui.common.viewmodel.SongViewModel

open class BaseFragment : Fragment() {

    lateinit var mainViewModel: MainViewModel

    lateinit var songViewModel: SongViewModel

    lateinit var extendSongAdapter: ExtendSongAdapter

    lateinit var songAdapter: SongAdapter

    lateinit var mainActivity: MainActivity

    fun log(tag: String, message: String) {
        Log.e(tag, message)
    }

    open fun init() {
        mainActivity = requireActivity() as MainActivity
        songViewModel = ViewModelProvider(mainActivity)[SongViewModel::class.java]
        mainViewModel = ViewModelProvider(mainActivity)[MainViewModel::class.java]
    }
}