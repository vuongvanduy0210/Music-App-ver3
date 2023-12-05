package com.vuongvanduy.music.data.data_source.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    val singer: String,
    @ColumnInfo("resource_uri")
    val resourceUri: String,
    @ColumnInfo("image_uri")
    val imageUri: String
)
