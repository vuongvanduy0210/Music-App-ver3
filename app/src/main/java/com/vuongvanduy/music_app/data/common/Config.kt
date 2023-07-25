package com.vuongvanduy.music_app.data.common

import com.vuongvanduy.music_app.data.models.Song
import java.text.Collator
import java.text.Normalizer
import java.util.Locale


fun sortListAscending(list: MutableList<Song>?) {
    val collator = Collator.getInstance(Locale("vi"))
    if (!list.isNullOrEmpty()) {
        list.sortWith { obj1, obj2 ->
            collator.compare(obj1.name?.lowercase(), obj2.name?.lowercase())
        }
    }
}

fun isListSortedAscending(list: MutableList<Song>): Boolean {
    val collator = Collator.getInstance(Locale("vi"))
    for (i in 1 until list.size) {
        if (collator.compare(list[i].name!!, list[i - 1].name!!) < 0) {
            return false
        }
    }
    return true
}

fun containsIgnoreCaseWithDiacritics(mainString: String, subString: String): Boolean {
    val mainNormalized = removeDiacritics(mainString).lowercase()
    val subNormalized = removeDiacritics(subString).lowercase()
    return mainNormalized.contains(subNormalized)
}

fun removeDiacritics(input: String): String {
    val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
    return normalized.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
}