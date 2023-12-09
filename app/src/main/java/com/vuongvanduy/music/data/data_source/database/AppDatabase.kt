package com.vuongvanduy.music.data.data_source.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vuongvanduy.music.common.DATABASE_NAME
import com.vuongvanduy.music.data.data_source.database.daos.FavouriteSongDAO
import com.vuongvanduy.music.data.data_source.database.daos.SongDAO
import com.vuongvanduy.music.data.data_source.database.entities.FavouriteSongEntity
import com.vuongvanduy.music.data.data_source.database.entities.SongEntity

@Database(entities = [SongEntity::class, FavouriteSongEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun songDao(): SongDAO

    abstract fun favouriteSongDao(): FavouriteSongDAO

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            instance ?: synchronized(this) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
            }
            return instance!!
        }
    }
}