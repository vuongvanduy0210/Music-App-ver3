package com.vuongvanduy.music.ui.home

import com.vuongvanduy.music.data.models.Song

interface IClickCategoryListener {

    fun clickButtonViewAll(categoryName: String)

    fun onClickSong(song: Song, categoryName: String)
}