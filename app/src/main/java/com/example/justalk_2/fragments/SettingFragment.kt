package com.example.justalk_2.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.justalk_2.MainActivity
import com.example.justalk_2.R
import com.example.justalk_2.Utils
import com.example.justalk_2.databinding.FragmentSettingBinding
import com.example.justalk_2.mvvm.ChatAppViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.util.UUID

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    lateinit var activity_: MainActivity

    lateinit var settingViewModel: ChatAppViewModel

    private lateinit var storageRef: StorageReference
    lateinit var storage: FirebaseStorage
    var uri: Uri? = null
    lateinit var bitmap: Bitmap

    lateinit var toolbar: Toolbar
    lateinit var tvToolbarTitle: TextView
    lateinit var tvToolbarUsername: TextView
    lateinit var tvToolbarStatus: TextView
    lateinit var imgUserProfile: CircleImageView
    lateinit var imgBackBtn: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        var view = binding.root
        activity_ = activity as MainActivity
        activity_.setDrawerLocked()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        binding.viewModel = settingViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        toolbar =
            requireActivity().findViewById<View>(R.id.toolbar) as Toolbar
        tvToolbarTitle =
            toolbar.findViewById(R.id.tv_title_app_bar) as TextView

        tvToolbarUsername =
            toolbar.findViewById(R.id.tv_username_chat_toolbar) as TextView
        tvToolbarStatus =
            toolbar.findViewById(R.id.tv_user_status_chat_toolbar) as TextView
        imgUserProfile =
            toolbar.findViewById(R.id.img_user_chat_toolbar) as CircleImageView
        imgBackBtn =
            toolbar.findViewById(R.id.btn_back_chat_toolbar) as ImageView

        changeToolbarItemsVisibility()
        setToolbarData()

        imgBackBtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_settingFragment_to_homeFragment)
        }

        settingViewModel.imageUrl.observe(viewLifecycleOwner, Observer {
            loadImage(it)
        })

        binding.imgUserSettingsFrg.setOnClickListener {

            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose your profile picture")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Take Photo" -> {
                        captureImageFromCamera()
                    }

                    options[item] == "Choose from Gallery" -> {
                        pickImageFromGallery()
                    }

                    options[item] == "Cancel" -> dialog.dismiss()
                }
            }
            builder.show()

        }

        binding.btnUpdateSettingsFrg.setOnClickListener {
            settingViewModel.updateProfile()
        }

    }

    private fun loadImage(it: String?) {
        Glide.with(requireContext()).load(it).placeholder(R.drawable.person_icon)
            .dontAnimate().into(binding.imgUserSettingsFrg)

    }

    private fun pickImageFromGallery() {
        val pickImageIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickImageIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(pickImageIntent, Utils.REQUEST_IMAGE_PICK)
        }
    }

    private fun captureImageFromCamera() {
        val pickImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(pickImageIntent, Utils.REQUEST_IMAGE_CAPTURE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                Utils.REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap

                    uploadImageToFirebaseStorage(imageBitmap)
                }

                Utils.REQUEST_IMAGE_PICK -> {
                    val imageUri = data?.data
                    val imageBitmap =
                        MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
                    uploadImageToFirebaseStorage(imageBitmap)
                }
            }
        }


    }

    private fun uploadImageToFirebaseStorage(imageBitmap: Bitmap?) {

        val baos = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()


        bitmap = imageBitmap!!

        binding.imgUserSettingsFrg.setImageBitmap(imageBitmap)

        val storagePath = storageRef.child("photos/${UUID.randomUUID()}.jpg")
        val uploadTask = storagePath.putBytes(data)
        uploadTask.addOnSuccessListener {
            val task = it.metadata?.reference?.downloadUrl

            task?.addOnSuccessListener {

                uri = it
                settingViewModel.imageUrl.value = uri.toString()

            }

            Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to upload image!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun changeToolbarItemsVisibility() {
        imgUserProfile.visibility = View.GONE
        tvToolbarUsername.visibility = View.VISIBLE
        tvToolbarStatus.visibility = View.GONE
        imgBackBtn.visibility = View.VISIBLE
        tvToolbarTitle.visibility = View.GONE
    }

    private fun setToolbarData() {
        tvToolbarUsername.text = "Settings"
    }

    override fun onResume() {
        super.onResume()
        settingViewModel.imageUrl.observe(viewLifecycleOwner, Observer {
            loadImage(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity_.setDrawerUnlocked()
        _binding = null
    }

}