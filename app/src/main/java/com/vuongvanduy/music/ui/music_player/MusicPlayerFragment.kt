package com.vuongvanduy.music.ui.music_player

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import com.bumptech.glide.Glide
import com.vuongvanduy.music.R
import com.vuongvanduy.music.base.fragment.BaseFragment
import com.vuongvanduy.music.common.*
import com.vuongvanduy.music.data.models.Song
import com.vuongvanduy.music.databinding.FragmentMusicPlayerBinding

class MusicPlayerFragment : BaseFragment() {

    private lateinit var binding: FragmentMusicPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun init() {
        super.init()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = mainViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerObserver()

        setClickButtonListener()
    }

    private fun registerObserver() {
        mainViewModel.apply {
            isPlaying.observe(viewLifecycleOwner) {
                if (it) {
                    startAnimation()
                } else {
                    stopAnimation()
                }
            }

            actionMusic.observe(mainActivity) {
                when (it) {
                    ACTION_CLEAR -> {}

                    else -> mainViewModel.currentSong.value?.let { song ->
                        setLayoutForMusicPlayer(song)
                    }
                }
            }

            isPlaying.observe(mainActivity) {
                if (it) {
                    binding.imgPlay.setImageResource(R.drawable.ic_pause)
                    startAnimation()
                } else {
                    binding.imgPlay.setImageResource(R.drawable.ic_play)
                    stopAnimation()
                }
            }

            isLooping.observe(mainActivity) {
                if (it) {
                    binding.btLoop.setImageResource(R.drawable.ic_is_looping)
                } else {
                    binding.btLoop.setImageResource(R.drawable.ic_loop)
                }
            }

            isShuffling.observe(mainActivity) {
                if (it) {
                    binding.btShuffle.setImageResource(R.drawable.ic_is_shuffling)
                } else {
                    binding.btShuffle.setImageResource(R.drawable.ic_shuffle)
                }
            }

            currentTime.observe(mainActivity) {
                val current = mainViewModel.currentTime.value
                if (current != null) {
                    binding.seekBarMusic.progress = current
                    val minutes: Int = current / 1000 / 60
                    val seconds: Int = current / 1000 % 60

                    @SuppressLint("DefaultLocale")
                    val str = String.format("%02d:%02d", minutes, seconds)
                    binding.tvCurrentTime.text = str
                }
            }

            finalTime.observe(mainActivity) {
                val final = mainViewModel.finalTime.value
                if (final != null) {
                    binding.seekBarMusic.max = final
                    val minutes: Int = final / 1000 / 60
                    val seconds: Int = final / 1000 % 60

                    @SuppressLint("DefaultLocale")
                    val str = String.format("%02d:%02d", minutes, seconds)
                    binding.tvFinalTime.text = str
                }
            }
        }
    }

    private fun setClickButtonListener() {
        binding.apply {
            imgPrevious.setOnClickListener {
                sendActionToService(requireContext(), ACTION_PREVIOUS)
            }
            imgPlay.setOnClickListener {
                if (mainViewModel.isPlaying.value == true) {
                    sendActionToService(requireContext(), ACTION_PAUSE)
                } else {
                    sendActionToService(requireContext(), ACTION_RESUME)
                }
            }

            imgNext.setOnClickListener {
                sendActionToService(requireContext(), ACTION_NEXT)
            }

            btLoop.setOnClickListener {
                sendActionToService(requireContext(), ACTION_LOOP)
            }

            btShuffle.setOnClickListener {
                sendActionToService(requireContext(), ACTION_SHUFFLE)
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setLayoutForMusicPlayer(song: Song) {
        binding.apply {
            tvMusicName.isSelected = true
            tvSinger.isSelected = true
            if (song.imageUri != null) {
                val imageUri = Uri.parse(song.imageUri)
                Glide.with(mainActivity).load(imageUri).into(circleImageView)
                Glide.with(mainActivity).load(imageUri).into(imgBackGround)
            } else {
                val bitmap = getBitmapFromUri(mainActivity, song.resourceUri)
                if (bitmap == null) {
                    binding.imgBackGround.setImageResource(R.drawable.icon_app)
                    binding.circleImageView.setImageResource(R.drawable.icon_app)
                } else {
                    binding.imgBackGround.setImageBitmap(bitmap)
                    binding.circleImageView.setImageBitmap(bitmap)
                }
            }

        }
        setSeekBarStatus()
    }

    private fun setSeekBarStatus() {
        binding.seekBarMusic.apply {

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        // gui current time lai cho service
                        sendCurrentTimeToService(mainActivity, progress)
                        if (mainViewModel.isPlaying.value == false) {
                            sendActionToService(mainActivity, ACTION_RESUME)
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun startAnimation() {
        val runnable: Runnable = object : Runnable {
            override fun run() {
                binding.circleImageView.animate()
                    .rotationBy(360f).withEndAction(this).setDuration(10000)
                    .setInterpolator(LinearInterpolator()).start()
            }
        }
        binding.circleImageView.animate()
            .rotationBy(360f).withEndAction(runnable).setDuration(10000)
            .setInterpolator(LinearInterpolator()).start()
    }

    private fun stopAnimation() {
        binding.circleImageView.animate().cancel()
    }
}