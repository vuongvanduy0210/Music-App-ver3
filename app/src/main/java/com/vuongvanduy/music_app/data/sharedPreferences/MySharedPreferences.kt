package com.vuongvanduy.music_app.data.sharedPreferences

import android.annotation.SuppressLint
import android.content.Context
import com.vuongvanduy.music_app.common.MY_SHARED_PREFERENCES
import com.vuongvanduy.music_app.common.SYSTEM_MODE

class MySharedPreferences constructor(private val context: Context) {

    @SuppressLint("CommitPrefEdits")
    fun putStringValue(key: String, value: String) {
        val sharedPreferences =
            context.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getStringValue(key: String): String? {
        val sharedPreferences =
            context.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, SYSTEM_MODE)
    }
}