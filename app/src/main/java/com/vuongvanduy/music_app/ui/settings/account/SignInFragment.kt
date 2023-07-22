package com.vuongvanduy.music_app.ui.settings.account

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.vuongvanduy.music_app.R
import com.vuongvanduy.music_app.base.dialogs.ProgressDialog
import com.vuongvanduy.music_app.base.fragment.BaseFragment
import com.vuongvanduy.music_app.common.TITLE_ACCOUNT
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

        initListener()
    }

    private fun initListener() {

        binding.btSignIn.setOnClickListener {
            onClickSignIn()
        }

        binding.layoutSignUp.setOnClickListener {
            goToSignUp()
        }

        binding.layoutForgotPassword.setOnClickListener {
            goToForgotPassword()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onClickSignIn() {

        val progressDialog = ProgressDialog(mainActivity, "Signing in...")

        binding.tvError.apply {
            text = ""
            visibility = View.GONE
        }

        val auth = FirebaseAuth.getInstance()

        val email = binding.edtEmail.text.trim().toString()
        val password = binding.edtPassword.text.trim().toString()

        if (email.isEmpty() || email.isBlank()) {
            binding.apply {
                tvError.text = "Email can't blank"
                edtEmail.setText("")
                tvError.visibility = View.VISIBLE
            }
            return
        } else if (password.isEmpty() || password.isBlank()) {
            binding.apply {
                tvError.text = "Password can't blank"
                edtPassword.setText("")
                tvError.visibility = View.VISIBLE
            }
            return
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.apply {
                tvError.text = "Email is wrong format"
                edtEmail.setText("")
                tvError.visibility = View.VISIBLE
            }
            return
        } else if (password.length < 6) {
            binding.apply {
                tvError.text = "Password must contain at least 6 characters"
                edtPassword.setText("")
                tvError.visibility = View.VISIBLE
            }
            return
        }

        progressDialog.show()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                progressDialog.dismiss()
                if (task.isSuccessful) {
                    // Sign in success
                    findNavController().popBackStack(R.id.accountFragment, false)
                }
            }
            .addOnFailureListener { exception ->
                if (exception is FirebaseAuthInvalidUserException) {
                    binding.tvError.apply {
                        text = "Email does not exist"
                        visibility = View.VISIBLE
                    }
                } else if (exception is FirebaseAuthInvalidCredentialsException) {
                    binding.tvError.apply {
                        text = "Password is incorrect"
                        visibility = View.VISIBLE
                    }
                    binding.edtPassword.setText("")
                }
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