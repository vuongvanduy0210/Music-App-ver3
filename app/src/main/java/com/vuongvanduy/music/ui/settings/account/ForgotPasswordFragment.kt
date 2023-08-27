package com.vuongvanduy.music.ui.settings.account

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.vuongvanduy.music.R
import com.vuongvanduy.music.base.dialogs.ProgressDialog
import com.vuongvanduy.music.base.fragment.BaseFragment
import com.vuongvanduy.music.common.TITLE_ACCOUNT
import com.vuongvanduy.music.common.hideKeyboard
import com.vuongvanduy.music.common.showDialog
import com.vuongvanduy.music.databinding.FragmentForgotPasswordBinding

class ForgotPasswordFragment : BaseFragment() {

    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
    }

    private fun initListener() {
        binding.btSend.setOnClickListener {
            onClickSendResetPassword()
        }

        binding.layoutSignIn.setOnClickListener {
            goToSignIn()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onClickSendResetPassword() {
        hideKeyboard(mainActivity, binding.root)
        val dialog = ProgressDialog(mainActivity, "Loading...")

        val email = binding.edtEmail.text.trim().toString()
        binding.tvNoti.apply {
            text = ""
            visibility = View.GONE
        }
        if (email.isEmpty() || email.isBlank()) {
            binding.apply {
                tvNoti.text = "Email can't blank"
                tvNoti.visibility = View.VISIBLE
            }
            return
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.apply {
                tvNoti.text = "Email is wrong format"
                edtEmail.setText("")
                tvNoti.visibility = View.VISIBLE
            }
            return
        }

        dialog.show()
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                dialog.dismiss()
                if (task.isSuccessful) {
                    val message = "Email sent. Check your email to complete reset password."
                    showDialog(mainActivity, layoutInflater, message)
                } else {
                    val message = "Email sent fail. Please check your email or network connection."
                    showDialog(mainActivity, layoutInflater, message)
                }
            }
    }

    private fun goToSignIn() {
        if (isFragmentInBackStack(R.id.signInFragment)) {
            findNavController().popBackStack(R.id.signInFragment, false)
        } else {
            val action =
                ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToSignInFragment()
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.binding.toolBarTitle.text = TITLE_ACCOUNT
    }
}