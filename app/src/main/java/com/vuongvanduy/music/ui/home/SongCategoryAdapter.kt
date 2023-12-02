package com.vuongvanduy.music.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vuongvanduy.music.R
import com.vuongvanduy.music.common.getBitmapFromUri
import com.vuongvanduy.music.data.models.Song
import com.vuongvanduy.music.databinding.ItemSongInCategoryBinding
import com.vuongvanduy.music.ui.common.myinterface.IClickSongListener

class SongCategoryAdapter(
    private val context: Context,
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

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: SongCategoryViewHolder, position: Int) {
        val song = listSongShow[position]

        holder.bind(song)

        if (song.imageUri != null) {
            val imageUri = Uri.parse(song.imageUri)
            holder.binding.apply {
                Glide.with(this.root).load(imageUri).into(imgSong)
            }
        } else {
            val bitmap = getBitmapFromUri(context, song.resourceUri)
            Glide.with(context).load(bitmap).into(holder.binding.imgSong).onLoadFailed(
                context.getDrawable(R.drawable.icon_app)
            )
        }

    }

    inner class SongCategoryViewHolder(val binding: ItemSongInCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            binding.song = song
            binding.listener = listener
            binding.executePendingBindings()
        }
    }
}