package com.vuongvanduy.music.data.services

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.vuongvanduy.music.common.*
import com.vuongvanduy.music.data.common.*
import com.vuongvanduy.music.data.models.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.Exception
import javax.inject.Inject

class SongRemoteService @Inject constructor(@ApplicationContext private val context: Context) {

    fun getAllSongsFromFirebase(): LiveData<List<Song>> {
        val songsLiveData = MutableLiveData<List<Song>>()
        val list = mutableListOf<Song>()

        val database = Firebase.database
        val myRef = database.getReference("all_songs")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val song = postSnapshot.getValue<Song>()
                    if (song != null) {
                        if (!isSongExists(list, song)) {
                            list.add(song)
                        }
                    }
                }
                sortListAscending(list)
                songsLiveData.postValue(list)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                songsLiveData.postValue(list)
                throw Exception(databaseError.message)
            }
        })

        return songsLiveData
    }

    fun pushSongToFirebase(email: String, song: Song) {
        val database = Firebase.database
        val myRef = database.getReference("favourite_songs")
            .child(email.substringBefore("."))
        myRef.child(song.name!!).setValue(song)
    }

    fun removeSongOnFirebase(email: String, song: Song) {
        val database = Firebase.database
        val myRef = song.name?.let { name ->
            database.getReference("favourite_songs")
                .child(email.substringBefore(".")).child(name)
        }
        myRef?.removeValue { databaseError, _ ->
            if (databaseError == null) {
                // Removal successful
                Toast.makeText(
                    context,
                    "Remove song success",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Removal failed
                Toast.makeText(
                    context,
                    "Failed to remove song: ${databaseError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}