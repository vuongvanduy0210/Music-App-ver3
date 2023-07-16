package com.vuongvanduy.music_app.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.vuongvanduy.music_app.R
import com.vuongvanduy.music_app.base.fragment.BaseFragment
import com.vuongvanduy.music_app.common.HOME_FRAGMENT_TAG
import com.vuongvanduy.music_app.data.common.SongViewModel
import com.vuongvanduy.music_app.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding

    private val songViewModel by viewModels<SongViewModel>()

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                songViewModel.fetchData()
                Toast.makeText(
                    requireContext(),
                    "Get music from your phone success",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Log.e("FRAGMENT_NAME", "Permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        log(HOME_FRAGMENT_TAG, "onCreate")
        super.onCreate(savedInstanceState)
        requestPermissionReadStorage()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        log(HOME_FRAGMENT_TAG, "onCreateView")
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = songViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private fun requestPermissionReadStorage() {
        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (requireContext().checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        // get list song from device and send to music device fragment
                        songViewModel.fetchData()
                    } else {
                        activityResultLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
                    }
                } else {
                    if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        // get list song from device and send to music device fragment
                        songViewModel.fetchData()
                    } else {
                        activityResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }, 500)
        }
    }
}