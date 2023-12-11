package com.vuongvanduy.music.ui.settings.account

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.vuongvanduy.music.R
import com.vuongvanduy.music.base.dialogs.ProgressDialog
import com.vuongvanduy.music.base.fragment.BaseFragment
import com.vuongvanduy.music.common.TITLE_ACCOUNT
import com.vuongvanduy.music.common.hideKeyboard
import com.vuongvanduy.music.databinding.FragmentChangePasswordBinding

class ChangePasswordFragment : BaseFragment() {

    private lateinit var binding: FragmentChangePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
    }

    private fun initListener() {
        binding.btChangePassword.setOnClickListener {
            onClickChangePassword()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onClickChangePassword() {
        hideKeyboard(mainActivity, binding.root)
        val dialog = ProgressDialog(requireActivity(), "Loading...")

        binding.apply {
            tvError.text = ""
            tvError.visibility = View.GONE
            tvNoti.visibility = View.GONE
        }

        val oldPass = binding.edtOldPassword.text?.trim().toString()
        val newPass = binding.edtNewPassword.text?.trim().toString()
        val confirmPass = binding.edtConfirmPassword.text?.trim().toString()

        ValidationUtils.checkValidChangePasswordInput(oldPass, newPass, confirmPass)?.let {
            binding.apply {
                tvError.text = it
                tvError.visibility = View.VISIBLE
                if (it.contains("confirm", true)) {
                    edtConfirmPassword.setText("")
                } else if (it.contains("new", true) ||
                    it.contains("passwords", true)
                ) {
                    edtNewPassword.setText("")
                    edtConfirmPassword.setText("")
                } else {
                    edtOldPassword.setText("")
                }
            }
            return
        }

        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(user?.email!!, oldPass)
        dialog.show()
        user.reauthenticate(credential)
            .addOnCompleteListener { authTask ->
                dialog.dismiss()
                if (authTask.isSuccessful) {
                    dialog.show()
                    user.updatePassword(newPass)
                        .addOnCompleteListener { task ->
                            dialog.dismiss()
                            if (task.isSuccessful) {
                                binding.apply {
                                    edtOldPassword.setText("")
                                    edtNewPassword.setText("")
                                    edtConfirmPassword.setText("")
                                }
                                Toast.makeText(
                                    mainActivity,
                                    "Change password success.",
                                    Toast.LENGTH_LONG
                                ).show()
                                findNavController().popBackStack(R.id.accountFragment, false)
                            }
                        }
                } else {
                    // If authentication fails, show an error message
                    binding.apply {
                        tvError.text = "Password is incorrect."
                        tvError.visibility = View.VISIBLE
                        edtOldPassword.setText("")
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.binding.toolBarTitle.text = TITLE_ACCOUNT
    }
}