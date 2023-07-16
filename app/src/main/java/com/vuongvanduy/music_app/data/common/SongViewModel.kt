package com.vuongvanduy.music_app.data.common

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.vuongvanduy.music_app.base.viewmodel.BaseViewModel
import com.vuongvanduy.music_app.data.models.Song
import com.vuongvanduy.music_app.data.repositories.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class SongViewModel @Inject constructor(private val songRepository: SongRepository) :
    BaseViewModel() {

    var onlineSongs = MutableLiveData<List<Song>>()
        private set

    var favouriteSongs = MutableLiveData<List<Song>>()
        private set

    var deviceSongs = MutableLiveData<List<Song>>()
        private set

    fun fetchData() {
        //get online songs
        onlineSongs = songRepository.getOnlineSongs() as MutableLiveData<List<Song>>
        //get favourite songs
        favouriteSongs = songRepository.getFavouriteSongs() as MutableLiveData<List<Song>>
        //get songs from device
        job = viewModelScope.launch(exceptionHandler) {
            val list = songRepository.getDeviceSongs()
            if (list.isNotEmpty()) {
                deviceSongs.postValue(list)
            }
        }
    }

}