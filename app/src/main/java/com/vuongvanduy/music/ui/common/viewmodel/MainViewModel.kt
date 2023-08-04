package com.vuongvanduy.music.ui.common.viewmodel


import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.vuongvanduy.music.base.viewmodel.BaseViewModel
import com.vuongvanduy.music.common.*
import com.vuongvanduy.music.data.models.Song
import com.vuongvanduy.music.data.sharedPreferences.DataLocalManager

class MainViewModel : BaseViewModel() {

    var currentSong = MutableLiveData<Song>()
    var currentListName: String? = null

    val isShowMusicPlayer = MutableLiveData(false)
    val isServiceRunning = MutableLiveData(false)
    val isShowMiniPlayer = MutableLiveData(false)
    val isShowBtPlayAll = MutableLiveData(false)

    val isPlaying = MutableLiveData(false)
    val isLooping = MutableLiveData(false)
    val isShuffling = MutableLiveData(false)

    val actionMusic = MutableLiveData(0)
    val finalTime = MutableLiveData(0)
    val currentTime = MutableLiveData(0)
    var themeMode = MutableLiveData<String>(DataLocalManager.getStringThemeMode())

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