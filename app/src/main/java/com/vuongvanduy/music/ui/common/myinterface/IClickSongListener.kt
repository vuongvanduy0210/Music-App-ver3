package com.vuongvanduy.music.ui.common.myinterface

import com.vuongvanduy.music.data.models.Song

interface IClickSongListener {

    fun onClickSong(song: Song)
    fun onLongClickSong(song: Song)
}