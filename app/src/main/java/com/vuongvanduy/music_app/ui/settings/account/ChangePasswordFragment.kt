package com.vuongvanduy.music_app.ui.settings.account

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.vuongvanduy.music_app.R
import com.vuongvanduy.music_app.base.dialogs.ProgressDialog
import com.vuongvanduy.music_app.base.fragment.BaseFragment
import com.vuongvanduy.music_app.common.TITLE_ACCOUNT
import com.vuongvanduy.music_app.common.hideKeyboard
import com.vuongvanduy.music_app.databinding.FragmentChangePasswordBinding

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

        val oldPass = binding.edtOldPassword.text.trim().toString()
        val newPass = binding.edtNewPassword.text.trim().toString()
        val confirmPass = binding.edtConfirmPassword.text.trim().toString()

        if (oldPass.isEmpty() || oldPass.isBlank()) {
            binding.apply {
                tvError.text = "Password can't blank"
                edtNewPassword.setText("")
                tvError.visibility = View.VISIBLE
            }
            return
        } else if (newPass.isEmpty() || newPass.isBlank()) {
            binding.apply {
                tvError.text = "New password can't blank"
                edtNewPassword.setText("")
                tvError.visibility = View.VISIBLE
            }
            return
        } else if (confirmPass.isEmpty() || confirmPass.isBlank()) {
            binding.apply {
                tvError.text = "Confirm password can't blank"
                edtConfirmPassword.setText("")
                tvError.visibility = View.VISIBLE
            }
            return
        } else if (newPass.length < 6) {
            binding.apply {
                tvError.text = "Password must contain at least 6 characters"
                edtNewPassword.setText("")
                edtConfirmPassword.setText("")
                tvError.visibility = View.VISIBLE
            }
            return
        } else if (newPass != confirmPass) {
            binding.apply {
                tvError.text = "Those passwords did’t match. Try again."
                edtNewPassword.setText("")
                edtConfirmPassword.setText("")
                tvError.visibility = View.VISIBLE
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
                                    activity,
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