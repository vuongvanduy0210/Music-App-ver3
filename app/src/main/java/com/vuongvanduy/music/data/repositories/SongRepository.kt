package com.vuongvanduy.music.data.repositories

import com.vuongvanduy.music.data.common.toSongEntity
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

    fun getOnlineSongs(callback: (List<Song>) -> Unit) {
        songRemoteService.getAllSongsFromFirebase(callback)
    }

    fun getFavouriteSongs(callback: (List<Song>) -> Unit) {
        songRemoteService.getFavouriteSongsFromFirebase(callback)
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

    // cache data
    suspend fun getAllSongsFromLocal() = withContext(Dispatchers.IO) {
        songLocalService.getAllSongs()
    }

    suspend fun insertSongsToLocal(list: List<Song>) = withContext(Dispatchers.IO) {
        songLocalService.insertAllSongs(list.map {
            it.toSongEntity()
        })
    }
}