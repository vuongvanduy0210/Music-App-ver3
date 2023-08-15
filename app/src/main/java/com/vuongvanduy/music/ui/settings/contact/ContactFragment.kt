package com.vuongvanduy.music.ui.settings.contact

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.vuongvanduy.music.R
import com.vuongvanduy.music.base.fragment.BaseFragment
import com.vuongvanduy.music.common.*
import com.vuongvanduy.music.databinding.DialogMailBinding
import com.vuongvanduy.music.databinding.FragmentContactBinding

class ContactFragment : BaseFragment() {

    private lateinit var binding: FragmentContactBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawUI()

        setOnClickListener()
    }

    private fun drawUI() {
        Glide.with(mainActivity).load(R.drawable.img_fb).into(binding.imgFacebook)
        Glide.with(mainActivity).load(R.drawable.img_zalo).into(binding.imgZalo)
        Glide.with(mainActivity).load(R.drawable.img_gmail).into(binding.imgGmail)
        Glide.with(mainActivity).load(R.drawable.img_microsoft).into(binding.imgMicrosoft)
    }

    private fun setOnClickListener() {
        binding.apply {
            itemFacebook.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(URI_FB)
                startActivity(intent)
            }

            itemZalo.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(URI_ZALO)
                startActivity(intent)
            }

            itemGmail.setOnClickListener {
                showDialog(EMAIL_CONTACT)
            }

            itemMicrosoft.setOnClickListener {
                showDialog(MICROSOFT_CONTACT)
            }
        }
    }

    private fun showDialog(title: String) {
        val builder = AlertDialog.Builder(mainActivity)
        val dialogMailBinding = DialogMailBinding.inflate(layoutInflater)
        dialogMailBinding.tvEmail.text = title
        dialogMailBinding.tvEmail.isSelected = true
        dialogMailBinding.btCopyEmail.setOnClickListener {
            val clipboard =
                mainActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Email", dialogMailBinding.tvEmail.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(mainActivity, "Email copied", Toast.LENGTH_SHORT).show()
        }
        builder.setView(dialogMailBinding.root)
        val dialog = builder.create()
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        mainActivity.binding.toolBarTitle.text = TITLE_CONTACT
    }
}