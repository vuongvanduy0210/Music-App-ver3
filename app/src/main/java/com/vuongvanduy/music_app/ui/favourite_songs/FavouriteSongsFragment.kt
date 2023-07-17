package com.vuongvanduy.music_app.ui.favourite_songs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vuongvanduy.music_app.base.fragment.BaseFragment
import com.vuongvanduy.music_app.common.TITLE_FAVOURITE_SONGS
import com.vuongvanduy.music_app.common.hideKeyboard
import com.vuongvanduy.music_app.ui.common.viewmodel.SongViewModel
import com.vuongvanduy.music_app.data.models.Song
import com.vuongvanduy.music_app.databinding.FragmentFavouriteSongsBinding
import com.vuongvanduy.music_app.ui.common.adapter.ExtendSongAdapter
import com.vuongvanduy.music_app.ui.common.myinterface.IClickSongListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouriteSongsFragment : BaseFragment() {

    private lateinit var binding: FragmentFavouriteSongsBinding

    private val songViewModel by viewModels<SongViewModel>()

    private lateinit var songAdapter: ExtendSongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        songViewModel.getFavouriteSongs()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteSongsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = songViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerViewSongs()

        registerObserverFetchDataFinish()

        setOnClickBtSearchView()
    }

    private fun setRecyclerViewSongs() {
        songAdapter = ExtendSongAdapter(object : IClickSongListener {
            override fun onClickSong(song: Song) {
//                songViewModel.setSong(song)
//                playSong(song)
            }

            override fun onClickAddFavourites(song: Song) {
//                addToFavourites(song)
            }

            override fun onClickRemoveFavourites(song: Song) {}
        }, TITLE_FAVOURITE_SONGS)
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.rcvListSongs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(decoration)
            adapter = songAdapter
        }
    }

    private fun registerObserverFetchDataFinish() {
        songViewModel.favouriteSongs.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                songViewModel.isLoadingFavourite.postValue(false)
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