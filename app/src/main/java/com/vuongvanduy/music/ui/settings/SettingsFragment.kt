package com.vuongvanduy.music.ui.settings

import com.vuongvanduy.music.R
import com.vuongvanduy.music.base.fragment.BaseMainFragment
import com.vuongvanduy.music.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseMainFragment<FragmentSettingsBinding>() {

    override val layoutRes: Int
        get() = R.layout.fragment_settings
}