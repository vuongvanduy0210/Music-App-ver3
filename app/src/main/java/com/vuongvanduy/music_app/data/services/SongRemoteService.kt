package com.vuongvanduy.music_app.data.services

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.vuongvanduy.music_app.data.common.*
import com.vuongvanduy.music_app.data.models.Song
import java.lang.Exception
import javax.inject.Inject

class SongRemoteService @Inject constructor() {

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

    fun getListFavouriteSongs(): LiveData<List<Song>> {
        val songsLiveData = MutableLiveData<List<Song>>()
        val list = mutableListOf<Song>()

        val email = FirebaseAuth.getInstance().currentUser?.email?.substringBefore(".")
//        val email = "duyconbn7@gmail"
        val database = Firebase.database
        val myRef = email?.let { database.getReference("favourite_songs").child(it) }
        myRef?.addValueEventListener(object : ValueEventListener {
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
}