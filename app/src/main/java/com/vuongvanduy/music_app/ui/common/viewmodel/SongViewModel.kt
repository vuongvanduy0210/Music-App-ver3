package com.vuongvanduy.music_app.ui.common.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.vuongvanduy.music_app.base.viewmodel.BaseViewModel
import com.vuongvanduy.music_app.common.*
import com.vuongvanduy.music_app.data.common.sortListAscending
import com.vuongvanduy.music_app.data.models.*
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

    var photos = MutableLiveData<List<Photo>>()
        private set

    private var onlineSongsShow = MutableLiveData<List<Song>>()

    var favouriteSongsShow = MutableLiveData<List<Song>>()

    private var deviceSongsShow = MutableLiveData<List<Song>>()

    fun getListOnline() {
        onlineSongs = songRepository.getOnlineSongs() as MutableLiveData<List<Song>>
    }

    fun getFavouriteSongs() {
        if (FirebaseAuth.getInstance().currentUser != null) {
//            favouriteSongs = songRepository.getFavouriteSongs() as MutableLiveData<List<Song>>
            getListFavouriteSongs()
        }
    }

    fun getListFavouriteSongs() {

        val list = mutableListOf<Song>()
        val email = FirebaseAuth.getInstance().currentUser?.email?.substringBefore(".")
//        val email = "duyconbn7@gmail"
        val database = Firebase.database
        val myRef = email?.let { database.getReference("favourite_songs").child(it) }
        myRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val song = postSnapshot.getValue<Song>()
                    if (song != null) {
                        if (!isSongExists(list, song)) {
                            list.add(song)
                        }
                    }
                }
                sortListAscending(list)
                favouriteSongs.value = list
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    fun getLocalData() {
        job = viewModelScope.launch(exceptionHandler) {
            val list = songRepository.getDeviceSongs()
            if (list.isNotEmpty()) {
                deviceSongs.postValue(list)
            }
        }
    }

    fun getListPhotos() {
        val list = mutableListOf<Photo>()
        if (onlineSongs.value != null) {
            for (i in 0 until onlineSongs.value?.size!!) {
                when (i) {
                    1, 2, 3, 4, 5 -> {
                        val photo = onlineSongs.value!![i].imageUri?.let { Photo(it) }
                        if (photo != null) {
                            list.add(photo)
                        }
                    }
                }
            }
            photos.value = list
        } else if (deviceSongs.value != null) {
            for (i in 0 until deviceSongs.value?.size!!) {
                val photo = deviceSongs.value!![i].imageUri?.let { Photo(it) }
                if (photo != null) {
                    list.add(photo)
                }
            }
            photos.value = list
        }
    }

    fun getListCategories(): MutableList<Category> {
        getOnlineSongsShow()
        getFavouriteSongsShow()
        getDeviceSongsShow()
        val list = mutableListOf<Category>()
        onlineSongsShow.value?.let { Category(TITLE_ONLINE_SONGS, it as MutableList<Song>) }
            ?.let { list.add(it) }
        if (!favouriteSongsShow.value.isNullOrEmpty()) {
            list.add(Category(TITLE_FAVOURITE_SONGS, favouriteSongsShow.value as MutableList<Song>))
        }
        deviceSongsShow.value?.let { Category(TITLE_DEVICE_SONGS, it as MutableList<Song>) }
            ?.let { list.add(it) }
        return list
    }

    private fun getOnlineSongsShow() {
        val list = mutableListOf<Song>()
        if (onlineSongs.value != null) {
            for (i in 0 until onlineSongs.value!!.size / 3) {
                val song = onlineSongs.value!![i]
                list.add(song)
            }
            onlineSongsShow.value = list
        }
    }

    private fun getFavouriteSongsShow() {
        val list = mutableListOf<Song>()
        if (favouriteSongs.value != null) {
            for (i in 0 until favouriteSongs.value!!.size / 3) {
                val song = favouriteSongs.value!![i]
                list.add(song)
            }
            favouriteSongsShow.value = list
        }
    }

    private fun getDeviceSongsShow() {
        val list = mutableListOf<Song>()
        if (deviceSongs.value != null) {
            for (i in 0 until deviceSongs.value!!.size) {
                val song = deviceSongs.value!![i]
                list.add(song)
            }
            deviceSongsShow.value = list
        }
    }
}