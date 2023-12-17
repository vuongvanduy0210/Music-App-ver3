package com.vuongvanduy.music.activity

import android.content.Intent
import android.os.Bundle
import com.vuongvanduy.music.base.activity.BaseActivity
import com.vuongvanduy.music.common.hideKeyboard
import com.vuongvanduy.music.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    override val TAG = LoginActivity::class.java.simpleName.toString()

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.layoutMain.setOnClickListener {
            hideKeyboard(this, binding.root)
        }
    }

    fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}