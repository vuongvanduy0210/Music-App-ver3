package com.vuongvanduy.music.base.fragment

import androidx.lifecycle.ViewModelProvider
import com.vuongvanduy.music.activity.MainActivity
import com.vuongvanduy.music.ui.common.viewmodel.MainViewModel
import com.vuongvanduy.music.ui.common.viewmodel.SongViewModel
import com.vuongvanduy.music.ui.settings.account.AccountViewModel

open class BaseMainFragment : BaseFragment() {

    lateinit var mainActivity: MainActivity

    open fun init() {
        mainActivity = requireActivity() as MainActivity
        songViewModel = ViewModelProvider(mainActivity)[SongViewModel::class.java]
        mainViewModel = ViewModelProvider(mainActivity)[MainViewModel::class.java]
        accountViewModel = ViewModelProvider(mainActivity)[AccountViewModel::class.java]
    }
}