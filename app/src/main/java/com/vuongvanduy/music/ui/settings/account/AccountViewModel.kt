package com.vuongvanduy.music.ui.settings.account

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.vuongvanduy.music.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor() : BaseViewModel() {

    val user = MutableLiveData<FirebaseUser>()

    val isShowSignOut = MutableLiveData(false)
}