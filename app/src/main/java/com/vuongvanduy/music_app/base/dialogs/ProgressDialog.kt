package com.vuongvanduy.music_app.base.dialogs

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import com.vuongvanduy.music_app.R

class ProgressDialog constructor(context: Context, message: String) {

    private val dialog = Dialog(context)

    init {
        dialog.setContentView(R.layout.progress_dialog)
        val messageTextView = dialog.findViewById<TextView>(R.id.message_text_view)
        messageTextView.text = message
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

}