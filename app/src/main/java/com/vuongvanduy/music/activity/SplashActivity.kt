package com.vuongvanduy.music.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.vuongvanduy.music.R
import com.vuongvanduy.music.activity.main.MainActivity
import com.vuongvanduy.music.common.*
import com.vuongvanduy.music.data.sharedPreferences.DataLocalManager
import com.vuongvanduy.music.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setThemeMode()

        addAnimation()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            nextActivity()
        } else {
            Handler(Looper.myLooper()!!).postDelayed({ nextActivity() }, 2500)
        }
    }

    private fun addAnimation() {
        val slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_splash)
        binding.imgIcon.startAnimation(slideInAnimation)
    }

    private fun setThemeMode() {
        when (DataLocalManager.getStringThemeMode()) {
            SYSTEM_MODE ->
                setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

            LIGHT_MODE ->
                setThemeMode(AppCompatDelegate.MODE_NIGHT_NO)

            DARK_MODE ->
                setThemeMode(AppCompatDelegate.MODE_NIGHT_YES)

            else ->
                setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun setThemeMode(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun nextActivity() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}