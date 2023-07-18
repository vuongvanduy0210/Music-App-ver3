package com.vuongvanduy.music_app.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vuongvanduy.music_app.data.models.Photo
import com.vuongvanduy.music_app.databinding.ItemPhotoBinding

class PhotoViewPager2Adapter constructor(
    private val photos: List<Photo>,
    private val context: Context
) : RecyclerView.Adapter<PhotoViewPager2Adapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]

        Glide.with(context).load(photo.imageUri).into(holder.binding.imgPhoto)
    }

    inner class PhotoViewHolder constructor(val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root)
}