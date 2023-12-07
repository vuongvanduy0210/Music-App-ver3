package com.vuongvanduy.music.data.common

import com.vuongvanduy.music.data.data_source.database.entities.FavouriteSongEntity
import com.vuongvanduy.music.data.data_source.database.entities.SongEntity
import com.vuongvanduy.music.data.models.Song

fun SongEntity.toSongModel() = Song(
    name,
    singer,
    resourceUri,
    imageUri
)

fun Song.toSongEntity() = SongEntity(
    id = resourceUri.hashCode(),
    name = name!!,
    singer = singer!!,
    resourceUri = resourceUri!!,
    imageUri = imageUri!!
)

fun FavouriteSongEntity.toSongModel() = Song(
    name,
    singer,
    resourceUri,
    imageUri
)

fun Song.toFavouriteSongEntity() = FavouriteSongEntity(
    id = resourceUri.hashCode(),
    name = name!!,
    singer = singer!!,
    resourceUri = resourceUri!!,
    imageUri = imageUri!!
)