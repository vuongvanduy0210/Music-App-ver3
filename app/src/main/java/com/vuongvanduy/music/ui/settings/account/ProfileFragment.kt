package com.vuongvanduy.music.ui.settings.account

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.vuongvanduy.music.R
import com.vuongvanduy.music.base.dialogs.ProgressDialog
import com.vuongvanduy.music.base.fragment.BaseFragment
import com.vuongvanduy.music.common.TITLE_ACCOUNT
import com.vuongvanduy.music.common.hideKeyboard
import com.vuongvanduy.music.databinding.FragmentProfileBinding


class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding

    private var uriImage: Uri? = null

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Log.e("FRAGMENT_NAME", "Permission denied")
            }
        }

    private val activityResultGetImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data ?: return@registerForActivityResult
                val uri = intent.data
                if (uri != null) {
                    uriImage = uri
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerObserver()

        initListener()
    }

    private fun registerObserver() {
        accountViewModel.user.observe(viewLifecycleOwner) { user ->
            accountViewModel.isShowSignOut.postValue(user != null)

            if (user != null) {
                binding.apply {
                    Glide.with(mainActivity).load(user.photoUrl).error(R.drawable.img_avatar_error)
                        .into(imgUser)
                    edtName.setText(user.displayName)
                    tvEmail.text = user.email
                }
            }
        }
    }

    private fun initListener() {
        binding.imgUser.setOnClickListener {
            requestPermissionReadStorage()
        }
        binding.btUpdateProfile.setOnClickListener {
            onClickUpdateProfile()
        }
    }

    private fun requestPermissionReadStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (mainActivity.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                // get list song from device and send to music device fragment
                openGallery()
            } else {
                activityResultLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (mainActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // get list song from device and send to music device fragment
                openGallery()
            } else {
                activityResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        activityResultGetImage
            .launch(Intent.createChooser(intent, "Select picture"))
    }

    @SuppressLint("SetTextI18n")
    private fun onClickUpdateProfile() {
        val dialog = ProgressDialog(mainActivity, "Updating...")
        hideKeyboard(mainActivity, binding.root)
        val name = binding.edtName.text.trim().toString()
        if (name.isEmpty() || name.isBlank()) {
            binding.tvError.text = "Name can't blank"
            binding.tvError.visibility = View.VISIBLE
            return
        }
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val profileUpdates = userProfileChangeRequest {
            displayName = name
            if (uriImage != null) {
                photoUri = uriImage
            }
        }
        dialog.show()
        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                dialog.dismiss()
                if (task.isSuccessful) {
                    Toast.makeText(
                        mainActivity,
                        "Update profile success.",
                        Toast.LENGTH_LONG
                    ).show()
                    accountViewModel.user.postValue(FirebaseAuth.getInstance().currentUser)
                    findNavController().popBackStack(R.id.accountFragment, false)
                }
            }
        uriImage = null
    }

    override fun onResume() {
        super.onResume()
        mainActivity.binding.toolBarTitle.text = TITLE_ACCOUNT

        uriImage?.let { setImageViewForUser(it) }
    }

    private fun setImageViewForUser(uri: Uri) {
        Log.e("AccountFragment", uri.toString())
        Glide.with(mainActivity).load(uri).into(binding.imgUser)
    }
}