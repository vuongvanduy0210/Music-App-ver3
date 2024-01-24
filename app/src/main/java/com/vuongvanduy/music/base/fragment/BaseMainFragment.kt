package com.vuongvanduy.music.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.vuongvanduy.music.activity.MainActivity
import com.vuongvanduy.music.ui.common.viewmodel.MainViewModel
import com.vuongvanduy.music.ui.common.viewmodel.SongViewModel
import com.vuongvanduy.music.ui.settings.account.AccountViewModel

abstract class BaseMainFragment<T : ViewDataBinding> : BaseFragment() {

    var mainActivity: MainActivity? = null
    var binding: T? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        init()
        return binding?.root
    }

    abstract val layoutRes: Int

    open fun init() {
        mainActivity = requireActivity() as MainActivity
        mainActivity?.let {
            songViewModel = ViewModelProvider(it)[SongViewModel::class.java]
            mainViewModel = ViewModelProvider(it)[MainViewModel::class.java]
            accountViewModel = ViewModelProvider(it)[AccountViewModel::class.java]
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity = null
        binding = null
    }
}