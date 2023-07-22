package com.vuongvanduy.music_app.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vuongvanduy.music_app.data.models.Category
import com.vuongvanduy.music_app.data.models.Song
import com.vuongvanduy.music_app.databinding.ItemCategoryBinding
import com.vuongvanduy.music_app.ui.common.myinterface.IClickSongListener


class CategoryAdapter constructor(
    private val context: Context,
    private val iClickCategoryListener: IClickCategoryListener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var listCategories: MutableList<Category>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: MutableList<Category>) {
        this.listCategories = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (!listCategories.isNullOrEmpty()) {
            listCategories!!.size
        } else 0
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        if (listCategories?.isEmpty() == true) {
            return
        }
        val category = listCategories?.get(position)
        if (category != null) {
            holder.binding.apply {
                tvNameCategory.text = category.name
                btViewAll.paintFlags = Paint.UNDERLINE_TEXT_FLAG

                val songCategoryAdapter = SongCategoryAdapter(category.listSongs,
                    object : IClickSongListener {
                        override fun onClickSong(song: Song) {
                            iClickCategoryListener.onClickSong(song, category.name)
                        }

                        override fun onClickAddFavourites(song: Song) {}

                        override fun onClickRemoveFavourites(song: Song) {}
                    })

                val manger = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                holder.binding.rcvSong.apply {
                    adapter = songCategoryAdapter
                    layoutManager = manger
                    btViewAll.setOnClickListener {
                        iClickCategoryListener.clickButtonViewAll(category.name)
                    }
                    tvNameCategory.setOnClickListener {
                        iClickCategoryListener.clickButtonViewAll(category.name)
                    }
                }
            }
        }
    }

    inner class CategoryViewHolder constructor(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}