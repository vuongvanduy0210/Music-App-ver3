package com.vuongvanduy.music.base.service

import android.database.sqlite.SQLiteException
import com.google.firebase.FirebaseException
import com.vuongvanduy.music.data.common.Response

open class BaseService {

    suspend fun <T> safeCallDao(call: suspend () -> T): Response<T> {
        return try {
            val response = call()
            Response.Success(response)
        } catch (e: SQLiteException) {
            val message = e.message.toString()
            Response.Error(message)
        }
    }

    fun <T> safeCallFirebase(call: () -> T): Response<T> {
        return try {
            val response = call()
            Response.Success(response)
        } catch (e: FirebaseException) {
            val message = e.message.toString()
            Response.Error(message)
        }
    }
}