package com.vuongvanduy.music.base.service

import android.database.sqlite.SQLiteException
import com.bumptech.glide.load.HttpException
import com.vuongvanduy.music.data.common.Response
import java.io.IOException
import java.net.SocketTimeoutException

open class BaseService {

    suspend fun <T> safeCallApi(call: suspend () -> T): Response<T> {
        return try {
            val response = call()
            Response.Success(response)
        } catch (e: HttpException) {
            Response.Error(e.message ?: "Something went wrong")
        } catch (e: IOException) {
            Response.Error(e.message!!)
        } catch (e: Exception) {
            Response.Error(e.message ?: "Something went wrong")
        } catch (e: SocketTimeoutException) {
            Response.Error(e.message ?: "Something went wrong")
        }
    }

    suspend fun <T> safeCallDao(call: suspend () -> T): Response<T> {
        return try {
            val response = call()
            Response.Success(response)
        } catch (e: SQLiteException) {
            val message = e.message.toString()
            Response.Error(message)
        }
    }
}