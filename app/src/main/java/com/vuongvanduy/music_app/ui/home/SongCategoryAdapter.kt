package com.vuongvanduy.music_app.ui.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vuongvanduy.music_app.data.models.Song
import com.vuongvanduy.music_app.databinding.ItemSongInCategoryBinding
import com.vuongvanduy.music_app.ui.common.myinterface.IClickSongListener

class SongCategoryAdapter constructor(
    private var listSongShow: MutableList<Song>,
    private val listener: IClickSongListener
) : RecyclerView.Adapter<SongCategoryAdapter.SongCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongCategoryViewHolder {
        val binding = ItemSongInCategoryBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return SongCategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listSongShow.size

    }

    override fun onBindViewHolder(holder: SongCategoryViewHolder, position: Int) {
        val song = listSongShow[position]
        val imageUri = Uri.parse(song.imageUri)
        holder.binding.apply {
            Glide.with(this.root).load(imageUri).into(imgSong)
            tvNameSong.text = song.name
            tvSinger.text = song.singer
            layoutItemSong.setOnClickListener {
                listener.onClickSong(song)
            }
        }
    }

    inner class SongCategoryViewHolder(val binding: ItemSongInCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}