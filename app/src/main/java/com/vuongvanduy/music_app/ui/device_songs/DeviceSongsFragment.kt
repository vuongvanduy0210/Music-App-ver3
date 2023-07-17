package com.vuongvanduy.music_app.ui.device_songs

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vuongvanduy.music_app.base.fragment.BaseFragment
import com.vuongvanduy.music_app.common.hideKeyboard
import com.vuongvanduy.music_app.ui.common.viewmodel.SongViewModel
import com.vuongvanduy.music_app.data.models.Song
import com.vuongvanduy.music_app.databinding.FragmentDeviceSongsBinding
import com.vuongvanduy.music_app.ui.common.myinterface.IClickSongListener
import com.vuongvanduy.music_app.ui.common.adapter.SongAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeviceSongsFragment : BaseFragment() {

    private lateinit var binding: FragmentDeviceSongsBinding

    private val songViewModel by viewModels<SongViewModel>()

    private lateinit var songAdapter: SongAdapter

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                songViewModel.getLocalData()
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
        super.onCreate(savedInstanceState)
        requestPermissionReadStorage()
    }

    private fun requestPermissionReadStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                // get list song from device and send to music device fragment
                songViewModel.getLocalData()
            } else {
                activityResultLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
            }
        } else {
            if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // get list song from device and send to music device fragment
                songViewModel.getLocalData()
            } else {
                activityResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeviceSongsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = songViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerViewSongs()

        registerObserverFetchDataFinish()

        setOnClickBtSearchView()
    }

    private fun setRecyclerViewSongs() {
        songAdapter = SongAdapter(object : IClickSongListener {
            override fun onClickSong(song: Song) {
//                songViewModel.setSong(song)
//                playSong(song)
            }

            override fun onClickAddFavourites(song: Song) {}

            override fun onClickRemoveFavourites(song: Song) {}
        })
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.rcvListSongs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(decoration)
            adapter = songAdapter
        }
    }

    private fun registerObserverFetchDataFinish() {
        songViewModel.deviceSongs.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                songViewModel.isLoadingDevice.postValue(false)
                songAdapter.setData(it)
            }
        }
    }

    private fun setOnClickBtSearchView() {
        binding.imgClear.apply {
            setOnClickListener {
                binding.edtSearch.setText("")
            }
        }
        binding.edtSearch.apply {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard(requireContext(), binding.root)
                    return@setOnEditorActionListener true
                }
                false
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    songAdapter.filter.filter(s)
                }
            })
        }
    }
}