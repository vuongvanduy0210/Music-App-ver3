package com.vuongvanduy.music.data.repositories

import androidx.lifecycle.LiveData
import com.vuongvanduy.music.data.models.Song
import com.vuongvanduy.music.data.services.SongLocalService
import com.vuongvanduy.music.data.services.SongRemoteService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SongRepository @Inject constructor(
    private val songRemoteService: SongRemoteService,
    private val songLocalService: SongLocalService
) {

    fun getOnlineSongs(): LiveData<List<Song>> {
        return songRemoteService.getAllSongsFromFirebase()
    }

    suspend fun getDeviceSongs() = withContext(Dispatchers.IO) {
        songLocalService.getLocalMusic()
    }

    fun pushSongToFavourites(email: String, song: Song) {
        songRemoteService.pushSongToFirebase(email, song)
    }

    fun removeSongOnFavourites(email: String, song: Song) {
        songRemoteService.removeSongOnFirebase(email, song)
    }
}