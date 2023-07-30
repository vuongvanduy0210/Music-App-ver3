package com.vuongvanduy.music_app.ui.common.viewmodel

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
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(private val songRepository: SongRepository) :
    BaseViewModel() {

    var onlineSongs = MutableLiveData<List<Song>>()

    val favouriteSongs = MutableLiveData<List<Song>>()

    val deviceSongs = MutableLiveData<List<Song>>()

    val photos = MutableLiveData<List<Photo>>()

    private val onlineSongsShow = MutableLiveData<List<Song>>()

    val favouriteSongsShow = MutableLiveData<List<Song>>()

    private val deviceSongsShow = MutableLiveData<List<Song>>()

    val favSong = MutableLiveData<Song>()

    fun getListOnline() {
        onlineSongs = songRepository.getOnlineSongs() as MutableLiveData<List<Song>>
    }

    fun getFavouriteSongs() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            getListFavouriteSongs()
        }
    }

    private fun getListFavouriteSongs() {

        val list = mutableListOf<Song>()
        val email = FirebaseAuth.getInstance().currentUser?.email?.substringBefore(".")
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
        if (!onlineSongs.value.isNullOrEmpty()) {
            val listSongs = onlineSongs.value!!.shuffled().take(5)
            listSongs.forEach { song ->
                song.imageUri?.let { list.add(Photo(it)) }
            }
            photos.value = list
        } else if (!deviceSongs.value.isNullOrEmpty()) {
            val listSongs = deviceSongs.value!!.shuffled().take(5)
            listSongs.forEach { song ->
                song.imageUri?.let { list.add(Photo(it)) }
            }
            photos.value = list
        }
    }

    fun getListCategories(): MutableList<Category> {
        onlineSongsShow.value = getSongsShow(onlineSongs.value as MutableList<Song>?)
        favouriteSongsShow.value = getSongsShow(favouriteSongs.value as MutableList<Song>?)
        deviceSongsShow.value = getSongsShow(deviceSongs.value as MutableList<Song>?)
        val list = mutableListOf<Category>()
        if (!onlineSongsShow.value.isNullOrEmpty()) {
            list.add(Category(TITLE_ONLINE_SONGS, onlineSongsShow.value as MutableList<Song>))
        }
        if (!favouriteSongsShow.value.isNullOrEmpty()) {
            list.add(Category(TITLE_FAVOURITE_SONGS, favouriteSongsShow.value as MutableList<Song>))
        }
        if (!deviceSongsShow.value.isNullOrEmpty()) {
            list.add(Category(TITLE_DEVICE_SONGS, deviceSongsShow.value as MutableList<Song>))
        }
        return list
    }

    private fun getSongsShow(songs: MutableList<Song>?): MutableList<Song> {
        var list = mutableListOf<Song>()
        if (!songs.isNullOrEmpty()) {
            list = songs.shuffled().take(10) as MutableList<Song>
        }
        return list
    }

    fun addSongToFavourites(song: Song) {
        FirebaseAuth.getInstance().currentUser?.email?.let {
            songRepository.pushSongToFavourites(it, song)
        }
    }

    fun removeSongFromFirebase(song: Song) {
        FirebaseAuth.getInstance().currentUser?.email?.let {
            songRepository.removeSongOnFavourites(it, song)
        }
        getFavouriteSongs()
    }
}