package com.vuongvanduy.music_app.ui.home

import com.vuongvanduy.music_app.data.models.Song

interface IClickCategoryListener {

    fun clickButtonViewAll(categoryName: String)

    fun onClickSong(song: Song, categoryName: String)
}