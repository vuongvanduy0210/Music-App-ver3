package com.vuongvanduy.music_app.base.fragment

import android.util.Log
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    fun log(tag: String, message: String) {
        Log.e(tag, message)
    }
}