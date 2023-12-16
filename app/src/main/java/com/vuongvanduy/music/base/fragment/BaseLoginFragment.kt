package com.vuongvanduy.music.base.fragment

import androidx.lifecycle.ViewModelProvider
import com.vuongvanduy.music.activity.LoginActivity
import com.vuongvanduy.music.ui.settings.account.AccountViewModel

open class BaseLoginFragment : BaseFragment() {

    lateinit var loginActivity: LoginActivity

    open fun init() {
        loginActivity = requireActivity() as LoginActivity
        accountViewModel = ViewModelProvider(loginActivity)[AccountViewModel::class.java]
    }
}