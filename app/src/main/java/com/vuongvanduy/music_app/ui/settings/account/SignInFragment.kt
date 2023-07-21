package com.vuongvanduy.music_app.ui.settings.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.vuongvanduy.music_app.R
import com.vuongvanduy.music_app.base.fragment.BaseFragment
import com.vuongvanduy.music_app.common.TITLE_ACCOUNT
import com.vuongvanduy.music_app.common.hideKeyboard
import com.vuongvanduy.music_app.databinding.FragmentSignInBinding

class SignInFragment : BaseFragment() {

    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutSignUp.setOnClickListener {
            goToSignUp()
        }

        binding.btSignIn.setOnClickListener {
            findNavController().popBackStack(R.id.accountFragment, false)
        }

        binding.layoutForgotPassword.setOnClickListener {
            goToForgotPassword()
        }
    }

    private fun goToSignUp() {
        if (isFragmentInBackStack(R.id.signUpFragment)) {
            findNavController().popBackStack(R.id.signUpFragment, false)
        } else {
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            findNavController().navigate(action)
        }
    }

    private fun goToForgotPassword() {
        if (isFragmentInBackStack(R.id.forgotPasswordFragment)) {
            findNavController().popBackStack(R.id.forgotPasswordFragment, false)
        } else {
            val action = SignInFragmentDirections.actionSignInFragmentToForgotPasswordFragment()
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.binding.toolBarTitle.text = TITLE_ACCOUNT
    }
}