package com.vuongvanduy.music_app.base.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog

class ConfirmDialog constructor(
    context: Context,
    val title: String,
    val message: String?
) : AlertDialog(context) {

}