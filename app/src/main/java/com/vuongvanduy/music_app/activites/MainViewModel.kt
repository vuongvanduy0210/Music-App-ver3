package com.vuongvanduy.music_app.activites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vuongvanduy.music_app.base.viewmodel.BaseViewModel

class MainViewModel : BaseViewModel() {

    val isShowMusicPlayer = MutableLiveData(false)
    val isServiceRunning = MutableLiveData(false)
    val isShowMiniPlayer = MutableLiveData(false)
    val isShowBottomNav = MutableLiveData(true)
    var isHome = false

    fun clearMusic() {
        isServiceRunning.postValue(false)
    }

    fun openMusicPlayer() {
        isShowMusicPlayer.postValue(true)
    }
}