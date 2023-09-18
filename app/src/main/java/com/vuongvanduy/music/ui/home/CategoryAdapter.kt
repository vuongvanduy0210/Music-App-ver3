package com.vuongvanduy.music.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vuongvanduy.music.data.models.Category
import com.vuongvanduy.music.data.models.Song
import com.vuongvanduy.music.databinding.ItemCategoryBinding
import com.vuongvanduy.music.ui.common.myinterface.IClickSongListener


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

            holder.bind(category)

            holder.binding.apply {
                btViewAll.paintFlags = Paint.UNDERLINE_TEXT_FLAG

                val songCategoryAdapter = SongCategoryAdapter(context, category.listSongs,
                    object : IClickSongListener {
                        override fun onClickSong(song: Song) {
                            iClickCategoryListener.onClickSong(song, category.name)
                        }

                        override fun onLongClickSong(song: Song) {

                        }
                    })

                val manger = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                holder.binding.rcvSong.apply {
                    adapter = songCategoryAdapter
                    layoutManager = manger
                }
            }
        }
    }

    inner class CategoryViewHolder constructor(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.category = category
            binding.listener = iClickCategoryListener
            binding.executePendingBindings()
        }
    }
}