package com.vuongvanduy.music.ui.common.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vuongvanduy.music.ui.device_songs.DeviceSongsFragment
import com.vuongvanduy.music.ui.favourite_songs.FavouriteSongsFragment
import com.vuongvanduy.music.ui.home.HomeFragment
import com.vuongvanduy.music.ui.online_songs.OnlineSongsFragment
import com.vuongvanduy.music.ui.settings.SettingsFragment

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