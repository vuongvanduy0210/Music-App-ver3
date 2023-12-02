package com.vuongvanduy.music.data.data_source.app_data

import com.vuongvanduy.music.common.KEY_THEME_MODE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataLocalManager @Inject constructor(
    private val sharedPreferences: MySharedPreferences
) {

    fun putStringThemeMode(value: String) {
        sharedPreferences.putStringValue(KEY_THEME_MODE, value)
    }

    fun getStringThemeMode(): String? {
        return sharedPreferences.getStringValue(KEY_THEME_MODE)
    }
}