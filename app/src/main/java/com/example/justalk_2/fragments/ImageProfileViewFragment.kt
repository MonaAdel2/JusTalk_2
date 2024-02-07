package com.example.justalk_2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.justalk_2.MainActivity
import com.example.justalk_2.R
import com.example.justalk_2.databinding.FragmentImageProfileViewBinding
import com.example.justalk_2.databinding.FragmentSignInBinding
import com.example.justalk_2.mvvm.ChatAppViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class ImageProfileViewFragment : Fragment() {

    private var _binding: FragmentImageProfileViewBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageProfileViewModel: ChatAppViewModel
    private lateinit var storageRef: StorageReference
    lateinit var storage: FirebaseStorage

    lateinit var activity_: MainActivity
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentImageProfileViewBinding.inflate(inflater, container, false)
        var view = binding.root
        activity_ = activity as MainActivity
        activity_.setDrawerLocked()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageProfileViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        toolbar = requireActivity().findViewById<View>(com.example.justalk_2.R.id.toolbar) as Toolbar
        drawerLayout = requireActivity().findViewById<View>(R.id.drawer_layout) as DrawerLayout

        toolbar.visibility = View.GONE


        imageProfileViewModel.imageUrl.observe(viewLifecycleOwner, Observer{
            loadImage(it)
        })

    }

    private fun loadImage(it: String?) {
        Glide.with(requireContext()).load(it).placeholder(R.drawable.person_icon)
            .dontAnimate().into(binding.imgProfileDisplay)

    }

    override fun onPause() {
        super.onPause()
        toolbar.visibility = View.VISIBLE
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

    }



}