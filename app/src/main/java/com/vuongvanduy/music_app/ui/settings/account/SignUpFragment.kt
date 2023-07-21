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
import com.vuongvanduy.music_app.databinding.FragmentSignUpBinding


class SignUpFragment : BaseFragment() {

    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        binding.layoutSignIn.setOnClickListener {
            goToSignIn()
        }

        binding.btSignUp.setOnClickListener {
            findNavController().popBackStack(R.id.accountFragment, false)
        }
    }

    private fun goToSignIn() {
        if (isFragmentInBackStack(R.id.signInFragment)) {
            findNavController().popBackStack(R.id.signInFragment, false)
        } else {
            val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.binding.toolBarTitle.text = TITLE_ACCOUNT
    }
}