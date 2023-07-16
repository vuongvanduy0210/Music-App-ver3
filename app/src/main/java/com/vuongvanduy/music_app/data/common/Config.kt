package com.vuongvanduy.music_app.data.common

import com.vuongvanduy.music_app.data.models.Song
import java.text.Collator
import java.util.Locale

fun isSongExists(songList: List<Song>, song: Song): Boolean {
    for (s in songList) {
        if (s.resourceUri == song.resourceUri) {
            return true
        }
    }
    return false
}

fun sortListAscending(list: MutableList<Song>?) {
    val collator = Collator.getInstance(Locale("vi"))
    if (!list.isNullOrEmpty()) {
        list.sortWith { obj1, obj2 ->
            collator.compare(obj1.name?.lowercase(), obj2.name?.lowercase())
        }
    }
}