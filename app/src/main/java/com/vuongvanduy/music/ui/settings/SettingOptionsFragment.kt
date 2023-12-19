package com.vuongvanduy.music.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.vuongvanduy.music.base.fragment.BaseMainFragment
import com.vuongvanduy.music.common.*
import com.vuongvanduy.music.databinding.FragmentSettingOptionsBinding

class SettingOptionsFragment : BaseMainFragment() {

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

        initListener()

        registerObserver()
    }

    @SuppressLint("SetTextI18n")
    private fun registerObserver() {

        binding.tvName.text = if (FirebaseAuth.getInstance().currentUser != null) {
            "( ${FirebaseAuth.getInstance().currentUser?.displayName} )"
        } else {
            "( $GUEST )"
        }

        accountViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.tvName.text = if (user != null) {
                "( ${FirebaseAuth.getInstance().currentUser?.displayName} )"
            } else {
                "( $GUEST )"
            }
        }

        mainViewModel.themeMode.observe(viewLifecycleOwner) { mode ->
            binding.tvTheme.text = "( $mode )"
        }
    }

    private fun initListener() {
        binding.layoutAccount.setOnClickListener {
            val action =
                SettingOptionsFragmentDirections.actionSettingOptionsFragmentToAccountFragment()
            goToFragment(action, TITLE_ACCOUNT)
        }

        binding.layoutAppearance.setOnClickListener {
            val action =
                SettingOptionsFragmentDirections.actionSettingOptionsFragmentToAppearanceFragment()
            goToFragment(action, TITLE_APPEARANCE)
        }

        binding.layoutAppInfo.setOnClickListener {
            val action =
                SettingOptionsFragmentDirections.actionSettingOptionsFragmentToAppInfoFragment()
            goToFragment(action, TITLE_APP_INFO)
        }

        binding.layoutContact.setOnClickListener {
            val action =
                SettingOptionsFragmentDirections.actionSettingOptionsFragmentToContactFragment()
            goToFragment(action, TITLE_CONTACT)
        }
    }

    private fun goToFragment(action: NavDirections, title: String) {
        findNavController().navigate(action)
        mainActivity.binding.toolBarTitle.text = title
    }
}