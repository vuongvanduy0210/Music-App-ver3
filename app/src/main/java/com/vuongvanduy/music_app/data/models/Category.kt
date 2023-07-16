package com.vuongvanduy.music_app.data.models

class Category constructor(
    val name: String,
    val listSongs: MutableList<Song>
) {
}