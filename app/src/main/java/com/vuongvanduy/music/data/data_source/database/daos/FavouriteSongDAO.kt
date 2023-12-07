package com.vuongvanduy.music.data.data_source.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vuongvanduy.music.data.data_source.database.entities.FavouriteSongEntity

@Dao
interface FavouriteSongDAO {

    @Query("select * from favourite_songs")
    suspend fun getAllSongs(): List<FavouriteSongEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<FavouriteSongEntity>)

    @Query("delete from favourite_songs")
    suspend fun deleteAllSongs()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: FavouriteSongEntity)

    @Delete
    suspend fun deleteSong(song: FavouriteSongEntity)
}