package com.vuongvanduy.music_app.data.repositories

import androidx.lifecycle.LiveData
import com.vuongvanduy.music_app.data.models.Song
import com.vuongvanduy.music_app.data.services.SongLocalService
import com.vuongvanduy.music_app.data.services.SongRemoteService
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
}