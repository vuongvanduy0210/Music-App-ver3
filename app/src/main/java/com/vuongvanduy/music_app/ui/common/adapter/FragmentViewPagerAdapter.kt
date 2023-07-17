package com.vuongvanduy.music_app.ui.common.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vuongvanduy.music_app.ui.device_songs.DeviceSongsFragment
import com.vuongvanduy.music_app.ui.favourite_songs.FavouriteSongsFragment
import com.vuongvanduy.music_app.ui.home.HomeFragment
import com.vuongvanduy.music_app.ui.online_songs.OnlineSongsFragment
import com.vuongvanduy.music_app.ui.settings.SettingsFragment

class FragmentViewPagerAdapter constructor(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> OnlineSongsFragment()
            2 -> FavouriteSongsFragment()
            3 -> DeviceSongsFragment()
            else -> SettingsFragment()
        }
    }
}