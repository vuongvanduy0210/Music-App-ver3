package com.vuongvanduy.music_app.activites

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vuongvanduy.music_app.MusicService
import com.vuongvanduy.music_app.base.viewmodel.BaseViewModel
import com.vuongvanduy.music_app.common.*
import com.vuongvanduy.music_app.data.models.Song
import java.io.Serializable

class MainViewModel : BaseViewModel() {

    var currentSong = MutableLiveData<Song>()
    var currentListName: String? = null

    val isShowMusicPlayer = MutableLiveData(false)
    val isServiceRunning = MutableLiveData(false)
    val isShowMiniPlayer = MutableLiveData(false)
    val isShowBtPlayAll = MutableLiveData(false)
    var isPlaying = MutableLiveData(false)
    var isLooping = MutableLiveData(false)
    var isShuffling = MutableLiveData(false)
    var actionMusic = MutableLiveData(0)
    val finalTime = MutableLiveData(0)
    val currentTime = MutableLiveData(0)
    var isHome = false

    fun openMusicPlayer() {
        isShowMusicPlayer.postValue(true)
    }

    fun receiveDataFromReceiver(intent: Intent) {
        val bundle = intent.extras ?: return
        currentSong.postValue(bundle.getSerializable(KEY_SONG) as Song?)
        isPlaying.postValue(bundle.getBoolean(KEY_STATUS_MUSIC))
        isLooping.postValue(bundle.getBoolean(KEY_STATUS_LOOP))
        isShuffling.postValue(bundle.getBoolean(KEY_STATUS_SHUFFLE))
        actionMusic.postValue(bundle.getInt(KEY_ACTION))
        finalTime.postValue(bundle.getInt(KEY_FINAL_TIME))
    }

    fun receiveCurrentTime(intent: Intent) {
        if (intent.action == SEND_CURRENT_TIME) {
            val bundle = intent.extras
            if (bundle != null) {
                currentTime.postValue(bundle.getInt(KEY_CURRENT_TIME))
            }
        }
    }
}