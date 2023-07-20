package com.vuongvanduy.music_app.ui.home

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
    private val listCategories: MutableList<Category>,
    private val context: Context,
    private val iClickCategoryListener: IClickCategoryListener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listCategories.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = listCategories[position]

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

    inner class CategoryViewHolder constructor(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}