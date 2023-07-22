package com.vuongvanduy.music_app.ui.common.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vuongvanduy.music_app.common.isSongExists
import com.vuongvanduy.music_app.data.models.Song
import com.vuongvanduy.music_app.databinding.ItemSongBinding
import com.vuongvanduy.music_app.ui.common.myinterface.IClickSongListener

class SongAdapter constructor(private val iClickSongListener: IClickSongListener) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>(), Filterable {

    private var songs: List<Song>? = null
    private var listSongsOld: List<Song>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Song>) {
        this.songs = list
        this.listSongsOld = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding: ItemSongBinding =
            ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (!songs.isNullOrEmpty()) {
            songs!!.size
        } else 0
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        if (songs?.isEmpty() == true) {
            return
        }
        val song = songs?.get(position)
        if (song != null) {
            holder.binding.apply {
                Glide.with(holder.binding.root)
                    .load(Uri.parse(song.imageUri))
                    .into(imgMusicInList)
                tvMusicNameInList.text = song.name
                tvSingerInList.text = song.singer
                layoutItem.setOnClickListener {
                    iClickSongListener.onClickSong(song)
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val strSearch = constraint.toString()
                songs = if (strSearch.isEmpty()) {
                    listSongsOld
                } else {
                    val list = ArrayList<Song>()
                    listSongsOld?.forEach {
                        if ((it.name?.lowercase()
                                ?.contains(strSearch.lowercase()) == true) && !isSongExists(
                                list,
                                it
                            )
                        ) {
                            list.add(it)
                        }
                        if ((it.singer?.lowercase()
                                ?.contains(strSearch.lowercase()) == true) && !isSongExists(
                                list,
                                it
                            )
                        ) {
                            list.add(it)
                        }
                    }
                    list
                }

                val filterResult = FilterResults()
                filterResult.values = songs
                return filterResult
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results?.values != null) {
                    songs = results.values as List<Song>
                }
                notifyDataSetChanged()
            }
        }
    }

    inner class SongViewHolder constructor(val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root)

}