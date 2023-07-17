package com.vuongvanduy.music_app.ui.common.myinterface

import com.vuongvanduy.music_app.data.models.Song

interface IClickSongListener {

    fun onClickSong(song: Song)
    fun onClickAddFavourites(song: Song)
    fun onClickRemoveFavourites(song: Song)
}