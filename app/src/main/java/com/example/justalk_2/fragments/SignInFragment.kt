package com.example.justalk_2.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.justalk_2.MainActivity
import com.example.justalk_2.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class SignInFragment : Fragment() {
    private val TAG = "SignInFragment"

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentSignInBinding.inflate(inflater, container, false)
        var view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if(auth.currentUser != null){
            // move to the home fragment
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            activity?.supportFragmentManager?.popBackStack()
        }
        progressBar = binding.progressBarSignIn

        binding.tvRegisterLogin.setOnClickListener {
           // go to sign up fragment
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            Log.d(TAG, "onViewCreated: passed 1 $action")
            view?.findNavController()?.navigate(action)
            Log.d(TAG, "onViewCreated: passed 2 navigate to sign up")
//            activity?.supportFragmentManager?.popBackStack()
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
                progressBar.visibility = View.VISIBLE
//                binding.shadeView.visibility = View.VISIBLE

            }

        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            // need to move to the MainActivity
            progressBar.visibility = View.GONE
//            binding.shadeView.visibility = View.GONE
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            activity?.supportFragmentManager?.popBackStack()  // finish

        }.addOnFailureListener {
            progressBar.visibility = View.GONE
//            binding.shadeView.visibility = View.GONE
            when(it){
                is FirebaseAuthInvalidCredentialsException ->{
                    // need to change it to sync bar
                    Toast.makeText(requireContext(), "Invalid Credentials.", Toast.LENGTH_SHORT).show()
                }
                else->{
                    Toast.makeText(requireContext(), "Can't signed you in.", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "signIn: ${it.message}")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}