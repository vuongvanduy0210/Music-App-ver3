package com.vuongvanduy.music_app.base.fragment

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.vuongvanduy.music_app.activites.MainActivity

open class BaseFragment : Fragment() {

    fun log(tag: String, message: String) {
        Log.e(tag, message)
    }
}