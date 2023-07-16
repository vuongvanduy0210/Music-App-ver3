package com.vuongvanduy.music_app.data.models

import android.net.Uri

class Photo constructor(private val imageUri: Uri) {

    fun getImageUri() = imageUri
}