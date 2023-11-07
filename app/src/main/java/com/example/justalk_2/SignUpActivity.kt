package com.example.justalk_2

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.justalk_2.databinding.ActivitySignInBinding
import com.example.justalk_2.databinding.ActivitySignUpBinding
import com.example.justalk_2.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    lateinit var auth: FirebaseAuth
    lateinit var fireStore: FirebaseFirestore

    lateinit var viewModel : ChatAppViewModel
    private lateinit var storageRef: StorageReference
    lateinit var storage: FirebaseStorage
    var uri: Uri? = null
    lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        binding.tvLoginRegister.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.btnRegisterRegister.setOnClickListener {
            val email = binding.etEmailRegister.editText?.text.toString()
            val password = binding.etPassRegister.editText?.text.toString()
            val username = binding.etUsernameRegister.editText?.text.toString()

            if(binding.etUsernameRegister.editText?.text.toString().isEmpty()){
                binding.etUsernameRegister.error = "Username can't be empty"
            }

            if(binding.etEmailRegister.editText?.text.toString().isEmpty()){
                binding.etEmailRegister.error = "Email can't be empty"
            }

            if(binding.etPassRegister.editText?.text.toString().isEmpty()){
                binding.etPassRegister.error = "Password can't be empty"
            }

            if(binding.etUsernameRegister.editText?.text.toString().isNotEmpty() &&
                binding.etEmailRegister.editText?.text.toString().isNotEmpty() &&
                binding.etPassRegister.editText?.text.toString().isNotEmpty() ){

                signUpWithEmailAndPassword(username, email, password)
            }
        }

        binding.btnUploadImgRegister.setOnClickListener{
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(this)
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

    private fun signUpWithEmailAndPassword(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            val user = auth.currentUser
            var data = hashMapOf("userUid" to user!!.uid!!,
                "username" to username,
                "userEmail" to email,
                "status" to "default",
                "imageUrl" to "https://www.pngarts.com/files/6/User-Avatar-in-Suit-PNG.png")

            fireStore.collection("users").document(user.uid).set(data)
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    private fun pickImageFromGallery(){
        val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickImageIntent.resolveActivity(this.packageManager) != null){
            startActivityForResult(pickImageIntent, Utils.REQUEST_IMAGE_PICK)
        }
    }
    private fun captureImageFromCamera(){
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
                        MediaStore.Images.Media.getBitmap(this?.contentResolver, imageUri)
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

        binding.btnUploadImgRegister.setImageBitmap(imageBitmap)

        val storagePath = storageRef.child("photos/${UUID.randomUUID()}.jpg")
        val uploadTask = storagePath.putBytes(data)
        uploadTask.addOnSuccessListener {
            val task = it.metadata?.reference?.downloadUrl

            task?.addOnSuccessListener {

                uri = it
                viewModel.imageUrl.value = uri.toString()

            }

            Toast.makeText(this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to upload image!", Toast.LENGTH_SHORT).show()
        }
    }
}