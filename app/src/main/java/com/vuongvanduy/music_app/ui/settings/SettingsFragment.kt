package com.vuongvanduy.music_app.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vuongvanduy.music_app.R
import com.vuongvanduy.music_app.base.fragment.BaseFragment
import com.vuongvanduy.music_app.common.SETTINGS_FRAGMENT_TAG


class SettingsFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        log(SETTINGS_FRAGMENT_TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onPause() {
        super.onPause()
        log(SETTINGS_FRAGMENT_TAG, "onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        log(SETTINGS_FRAGMENT_TAG, "onDestroy")
    }

    override fun onResume() {
        super.onResume()
        log(SETTINGS_FRAGMENT_TAG, "onResume")
    }
}