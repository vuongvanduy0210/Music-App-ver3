package com.vuongvanduy.music.base.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.vuongvanduy.music.activity.MainActivity
import com.vuongvanduy.music.ui.common.adapter.ExtendSongAdapter
import com.vuongvanduy.music.ui.common.adapter.SongAdapter
import com.vuongvanduy.music.ui.common.viewmodel.MainViewModel
import com.vuongvanduy.music.ui.common.viewmodel.SongViewModel
import com.vuongvanduy.music.ui.settings.account.AccountViewModel

open class BaseFragment : Fragment() {

    protected open val TAG = ""

    lateinit var mainViewModel: MainViewModel

    lateinit var songViewModel: SongViewModel

    lateinit var accountViewModel: AccountViewModel

    lateinit var extendSongAdapter: ExtendSongAdapter

    lateinit var songAdapter: SongAdapter

    lateinit var mainActivity: MainActivity

    fun log(tag: String, message: String) {
        Log.e(tag, message)
    }

    open fun init() {
        mainActivity = requireActivity() as MainActivity
        songViewModel = ViewModelProvider(mainActivity)[SongViewModel::class.java]
        mainViewModel = ViewModelProvider(mainActivity)[MainViewModel::class.java]
        accountViewModel = ViewModelProvider(mainActivity)[AccountViewModel::class.java]
    }

    fun isFragmentInBackStack(destinationId: Int) =
        try {
            findNavController().getBackStackEntry(destinationId)
            true
        } catch (e: Exception) {
            false
        }

    // LIFECYCLE
    override fun onAttach(context: Context) {
        super.onAttach(context)
        logLifecycle("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logLifecycle("onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logLifecycle("onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logLifecycle("onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        logLifecycle("onStart")
    }

    override fun onResume() {
        super.onResume()
        logLifecycle("onResume")
    }

    override fun onPause() {
        super.onPause()
        logLifecycle("onPause")
    }

    override fun onStop() {
        super.onStop()
        logLifecycle("onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logLifecycle("onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        logLifecycle("onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        logLifecycle("onDetach")
    }

    open fun logLifecycle(msg: String) {
        Log.e(TAG, msg)
    }
}