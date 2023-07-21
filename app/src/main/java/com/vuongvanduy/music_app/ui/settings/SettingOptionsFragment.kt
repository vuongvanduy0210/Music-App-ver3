package com.vuongvanduy.music_app.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.vuongvanduy.music_app.R
import com.vuongvanduy.music_app.base.fragment.BaseFragment
import com.vuongvanduy.music_app.common.*
import com.vuongvanduy.music_app.databinding.FragmentSettingOptionsBinding

class SettingOptionsFragment : BaseFragment() {

    private lateinit var binding: FragmentSettingOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingOptionsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    @SuppressLint("CommitTransaction")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().enableOnBackPressed(true)

        binding.tvAccount.setOnClickListener {
            val action =
                SettingOptionsFragmentDirections.actionSettingOptionsFragmentToAccountFragment()
            goToFragment(action, TITLE_ACCOUNT)
        }

        binding.tvAppearance.setOnClickListener {
            val action =
                SettingOptionsFragmentDirections.actionSettingOptionsFragmentToAppearanceFragment()
            goToFragment(action, TITLE_APPEARANCE)
        }

        binding.tvAppInfo.setOnClickListener {
            val action =
                SettingOptionsFragmentDirections.actionSettingOptionsFragmentToAppInfoFragment()
            goToFragment(action, TITLE_APP_INFO)
        }

        binding.tvContact.setOnClickListener {
            val action =
                SettingOptionsFragmentDirections.actionSettingOptionsFragmentToContactFragment()
            goToFragment(action, TITLE_CONTACT)
        }
    }

    private fun goToFragment(action: NavDirections, title: String) {
        findNavController().navigate(action)
        mainActivity.binding.toolBarTitle.text = title
    }

    override fun onResume() {
        super.onResume()
    }
}