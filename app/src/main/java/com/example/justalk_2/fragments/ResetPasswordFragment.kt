package com.example.justalk_2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.justalk_2.R
import com.example.justalk_2.databinding.FragmentResetPasswordBinding
import com.example.justalk_2.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentResetPasswordBinding.inflate(inflater, container, false)
        var view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        binding.btnResetPassword.setOnClickListener {
            val email = binding.etResetEmail.editText?.text.toString()
            auth.sendPasswordResetEmail(email).addOnSuccessListener {

//                binding.tvResetPasswordResults.text = "An email has been sent\nplease check your email."
                Toast.makeText(requireActivity(), "An email has been sent\nplease check your email.", Toast.LENGTH_SHORT).show()

                val action = ResetPasswordFragmentDirections.actionResetPasswordFragmentToSignInFragment()
                view?.findNavController()?.navigate(action)
                activity?.supportFragmentManager?.popBackStack()

            }.addOnFailureListener {

                binding.tvResetPasswordResults.text = "Please try again later."
            }
        }
    }

}