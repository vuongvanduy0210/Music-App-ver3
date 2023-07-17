package com.vuongvanduy.music_app.ui.common.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vuongvanduy.music_app.base.viewmodel.BaseViewModel
import com.vuongvanduy.music_app.data.models.Song
import com.vuongvanduy.music_app.data.repositories.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(private val songRepository: SongRepository) :
    BaseViewModel() {

    var onlineSongs = MutableLiveData<List<Song>>()
        private set

    var favouriteSongs = MutableLiveData<List<Song>>()
        private set

    var deviceSongs = MutableLiveData<List<Song>>()
        private set

    fun getListOnline() {
        onlineSongs = songRepository.getOnlineSongs() as MutableLiveData<List<Song>>
    }

    fun getFavouriteSongs() {
        favouriteSongs = songRepository.getFavouriteSongs() as MutableLiveData<List<Song>>
    }

    fun getLocalData() {
        job = viewModelScope.launch(exceptionHandler) {
            val list = songRepository.getDeviceSongs()
            if (list.isNotEmpty()) {
                deviceSongs.postValue(list)
            }
        }
    }

    fun fetchData() {
        getListOnline()
        getFavouriteSongs()
    }
}