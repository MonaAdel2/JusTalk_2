package com.example.justalk_2.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.justalk_2.AuthActivity
import com.example.justalk_2.MainActivity
import com.example.justalk_2.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID


class SignUpFragment : Fragment() {

    private val TAG = "SignUpFragment"
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore

    private lateinit var storageRef: StorageReference
    private lateinit var storage: FirebaseStorage

    private var uri: Uri? = null
    private var bitmap: Bitmap? = null

    private lateinit var activity_: AuthActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        var view = binding.root
        activity_ = activity as AuthActivity
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: beginning of sign up frg")


        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        binding.tvLoginRegister.setOnClickListener {
            val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
            view.findNavController()?.navigate(action)
        }

        binding.btnRegisterRegister.setOnClickListener {
            val email = binding.etEmailRegister.editText?.text.toString()
            val password = binding.etPassRegister.editText?.text.toString()
            val username = binding.etUsernameRegister.editText?.text.toString()

            if (username.isEmpty()) {
                binding.etUsernameRegister.error = "Username can't be empty"
            }

            if (email.isEmpty()) {
                binding.etEmailRegister.error = "Email can't be empty"
            }

            if (password.isEmpty()) {
                binding.etPassRegister.error = "Password can't be empty"
            }

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                activity_.viewShaded.visibility = View.VISIBLE
                binding.progressBarSignUp.visibility = View.VISIBLE
                signUpWithEmailAndPassword(username, email, password, bitmap)
            }
        }

        binding.btnUploadImgRegister.setOnClickListener {
            Log.d(TAG, "onViewCreated: image upload button pressed")
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
    }

    private fun signUpWithEmailAndPassword(
        username: String,
        email: String,
        password: String,
        imageBitmap: Bitmap?
    ) {
        // Check if the imageBitmap is not null
        if (imageBitmap != null) {
            // Upload the image to Firebase Storage
            uploadImageToFirebaseStorage(imageBitmap) { imageUrl ->
                // Image upload successful, proceed with user registration
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResult ->
                        val user = authResult.user
                        // Construct user data with the profile image URL
                        val data = hashMapOf(
                            "userUid" to user!!.uid,
                            "username" to username,
                            "userEmail" to email,
                            "status" to "Offline",
                            "imageUrl" to imageUrl
                        )
                        fireStore.collection("Users").document(user.uid).set(data)

                        activity_.viewShaded.visibility = View.GONE
                        binding.progressBarSignUp.visibility = View.GONE

                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        activity?.supportFragmentManager?.popBackStack()
                    }.addOnFailureListener { exception ->
                    // Handle user creation failure
                    Log.d(TAG, "signUpWithEmailAndPassword: ${exception.message}")
                    Toast.makeText(requireContext(), "Failed to create user", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            // If imageBitmap is null, proceed with user registration without uploading an image
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val user = authResult.user
                    val data = hashMapOf(
                        "userUid" to user!!.uid,
                        "username" to username,
                        "userEmail" to email,
                        "status" to "Offline",
                        // Default image URL when user doesn't upload an image
                        "imageUrl" to "https://www.pngarts.com/files/6/User-Avatar-in-Suit-PNG.png"
                    )

                    activity_.viewShaded.visibility = View.GONE
                    binding.progressBarSignUp.visibility = View.GONE

                    fireStore.collection("Users").document(user.uid).set(data)
                        .addOnSuccessListener {
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        }

                }.addOnFailureListener { exception ->
                // Handle user creation failure
                Toast.makeText(requireContext(), "Failed to create user", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "signUpWithEmailAndPassword: ${exception.message}")
                activity_.viewShaded.visibility = View.GONE
                binding.progressBarSignUp.visibility = View.GONE
            }
        }
    }


    private val requestImageCapture =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                uploadImageToFirebaseStorage(imageBitmap) { imageUrl ->

                }
            }
        }

    private val requestImagePick =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                val imageBitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                uploadImageToFirebaseStorage(imageBitmap) { imageUrl ->

                }
            }
        }

    private fun pickImageFromGallery() {
        val pickImageIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickImageIntent.resolveActivity(requireActivity().packageManager) != null) {
            requestImagePick.launch(pickImageIntent)
        }
    }

    private fun captureImageFromCamera() {
        val pickImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        requestImageCapture.launch(pickImageIntent)
    }


    private fun uploadImageToFirebaseStorage(
        imageBitmap: Bitmap?,
        onSuccess: (imageUrl: String) -> Unit
    ) {
        if (imageBitmap != null) {
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            bitmap = imageBitmap
            binding.btnUploadImgRegister.setImageBitmap(imageBitmap)
            val storagePath = storageRef.child("photos/${UUID.randomUUID()}.jpg")
            val uploadTask = storagePath.putBytes(data)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                storagePath.downloadUrl.addOnSuccessListener { _uri ->
                    val imageUrl = _uri.toString()
                    uri = _uri
                    onSuccess(imageUrl) // Pass the imageUrl to the callback function
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to upload image: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // If imageBitmap is null, don't attempt to upload the image
            Toast.makeText(requireContext(), "Image is null!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}