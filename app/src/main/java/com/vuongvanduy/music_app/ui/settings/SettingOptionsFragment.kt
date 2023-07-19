package com.vuongvanduy.music_app.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.vuongvanduy.music_app.R
import com.vuongvanduy.music_app.base.fragment.BaseFragment
import com.vuongvanduy.music_app.databinding.FragmentSettingOptionsBinding
import com.vuongvanduy.music_app.databinding.FragmentSettingsBinding
import java.lang.Exception

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
            findNavController().navigate(action)

        }

    }
}