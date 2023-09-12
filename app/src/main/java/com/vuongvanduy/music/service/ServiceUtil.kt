package com.vuongvanduy.music.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.vuongvanduy.music.activity.main.MainActivity
import com.vuongvanduy.music.broadcast_receiver.MyReceiver
import com.vuongvanduy.music.common.ACTION_MUSIC_NAME

fun getPendingIntent(context: Context, action: Int): PendingIntent? {
    val intent = Intent(context, MyReceiver::class.java)
    intent.putExtra(ACTION_MUSIC_NAME, action)
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        PendingIntent.getBroadcast(
            context.applicationContext,
            action, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    } else {
        PendingIntent.getBroadcast(
            context.applicationContext,
            action, intent, PendingIntent.FLAG_IMMUTABLE
        )
    }
}

fun getPendingIntentClickNotification(context: Context): PendingIntent {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    } else {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}