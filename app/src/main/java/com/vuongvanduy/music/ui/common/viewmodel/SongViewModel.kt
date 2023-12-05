package com.vuongvanduy.music.ui.common.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.vuongvanduy.music.base.viewmodel.BaseViewModel
import com.vuongvanduy.music.common.TITLE_DEVICE_SONGS
import com.vuongvanduy.music.common.TITLE_FAVOURITE_SONGS
import com.vuongvanduy.music.common.TITLE_ONLINE_SONGS
import com.vuongvanduy.music.data.common.Response
import com.vuongvanduy.music.data.common.sortListAscending
import com.vuongvanduy.music.data.common.toSongModel
import com.vuongvanduy.music.data.models.Category
import com.vuongvanduy.music.data.models.Photo
import com.vuongvanduy.music.data.models.Song
import com.vuongvanduy.music.data.repositories.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val songRepository: SongRepository
) : BaseViewModel() {

    val onlineSongs = MutableLiveData<List<Song>>()

    val favouriteSongs = MutableLiveData<List<Song>>()

    val deviceSongs = MutableLiveData<List<Song>>()

    val photos = MutableLiveData<List<Photo>>()

    private val onlineSongsShow = MutableLiveData<List<Song>>()

    val favouriteSongsShow = MutableLiveData<List<Song>>()

    private val deviceSongsShow = MutableLiveData<List<Song>>()

    val optionSong = MutableLiveData<Song>()

    init {
        getAllSongFromRemote()
    }

    private fun getAllSongFromRemote() {
        viewModelScope.launch(exceptionHandler) {
            val response = songRepository.getAllSongsFromLocal()
            if (response is Response.Success) {
                val songs = response.data?.map { it.toSongModel() }
                sortListAscending(songs as MutableList<Song>)
                songs.let {
                    onlineSongs.value = it
                }
            }
            songRepository.getOnlineSongs {
                onlineSongs.value = it
                viewModelScope.launch {
                    songRepository.insertSongsToLocal(it)
                }
            }
        }
    }

    fun getFavouriteSongs() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            songRepository.getFavouriteSongs {
                favouriteSongs.value = it
            }
        }
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
            val listSongs = onlineSongs.value!!.take(5)
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
            list = songs.take(10) as MutableList<Song>
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