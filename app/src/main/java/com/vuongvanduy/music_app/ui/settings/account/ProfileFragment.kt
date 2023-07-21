package com.vuongvanduy.music_app.ui.settings.account

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.vuongvanduy.music_app.R
import com.vuongvanduy.music_app.base.fragment.BaseFragment
import com.vuongvanduy.music_app.common.TITLE_ACCOUNT
import com.vuongvanduy.music_app.databinding.FragmentProfileBinding


class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerObserver()
    }

    @SuppressLint("SetTextI18n")
    private fun registerObserver() {
        accountViewModel.user.observe(viewLifecycleOwner) {user ->
            accountViewModel.isShowSignOut.postValue(user != null)

            if (user != null) {
                binding.apply {
                    Glide.with(mainActivity).load(user.photoUrl).error(R.drawable.img_avatar_error)
                        .into(imgUser)
                    edtName.setText(user.displayName)
                    tvEmail.text = user.email
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.binding.toolBarTitle.text = TITLE_ACCOUNT
    }
}