package com.vuongvanduy.music_app.ui.settings.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.vuongvanduy.music_app.R
import com.vuongvanduy.music_app.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btSignIn.setOnClickListener {
            val action = AccountFragmentDirections.actionAccountFragmentToSignInFragment()
            findNavController().navigate(action)
        }

        binding.btSignUp.setOnClickListener {
            val action = AccountFragmentDirections.actionAccountFragmentToSignUpFragment()
            findNavController().navigate(action)
        }

        binding.btChangeProfile.setOnClickListener {
            val action = AccountFragmentDirections.actionAccountFragmentToProfileFragment()
            findNavController().navigate(action)
        }

        binding.btChangePassword.setOnClickListener {
            val action = AccountFragmentDirections.actionAccountFragmentToChangePasswordFragment()
            findNavController().navigate(action)
        }
    }
}