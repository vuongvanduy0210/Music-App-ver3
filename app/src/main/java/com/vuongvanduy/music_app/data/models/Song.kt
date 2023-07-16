package com.vuongvanduy.music_app.data.models

import android.net.Uri
import java.io.Serializable

class Song constructor(
    val name: String? = null,
    val singer: String? = null,
    val resourceUri: String? = null,
    val imageUri: String? = null
) : Serializable {

    override fun toString(): String {
        return "Song{ name = $name, " +
                "singer = $singer, " +
                "resourceUri = $resourceUri, " +
                "imageUri = $imageUri"
    }
}