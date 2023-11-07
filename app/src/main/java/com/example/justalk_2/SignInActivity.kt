package com.example.justalk_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import com.example.justalk_2.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class SignInActivity : AppCompatActivity() {

    private val TAG = "SignInActivity"
    private lateinit var binding: ActivitySignInBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        if(auth.currentUser != null){
            // save the current user
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.tvRegisterLogin.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.btnLoginLogin.setOnClickListener {
            val email = binding.etEmailLogin.editText?.text.toString()
            val password = binding.etPassLogin.editText?.text.toString()
            if(binding.etEmailLogin.editText?.text.toString().isEmpty()) {
                // set error message on edit text
                binding.etEmailLogin.error = "Email can't be empty"
                return@setOnClickListener
            }
            if(binding.etPassLogin.editText?.text.toString().isEmpty()) {
                // set error message on edit text
                binding.etPassLogin.error = "Password can't be empty"
                return@setOnClickListener
            }

            if(binding.etEmailLogin.editText?.text.toString().isNotEmpty() 
                && binding.etPassLogin.editText?.text.toString().isNotEmpty()){
                signIn(email, password)
                
            }


        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }.addOnFailureListener {
            when(it){
                is FirebaseAuthInvalidCredentialsException ->{
                    Toast.makeText(this, "Invalid Credentials.", Toast.LENGTH_SHORT).show()
                }
                else->{
                    Toast.makeText(this, "Can't signed you in.", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "signIn: ${it.message}")
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}