package com.example.justalk_2.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.justalk_2.MainActivity
import com.example.justalk_2.R
import com.example.justalk_2.Utils
import com.example.justalk_2.adapters.MessageAdapter
import com.example.justalk_2.databinding.FragmentChatBinding
import com.example.justalk_2.model.Message
import com.example.justalk_2.mvvm.ChatAppViewModel
import de.hdodenhof.circleimageview.CircleImageView

class ChatFragment : Fragment() {

    val args: ChatFragmentArgs by navArgs()
    lateinit var userViewModel: ChatAppViewModel

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    lateinit var activity_: MainActivity

    lateinit var toolbar: Toolbar
    lateinit var tvToolbarTitle: TextView
    lateinit var tvToolbarUsername: TextView
    lateinit var tvToolbarStatus: TextView
    lateinit var imgUserProfile: CircleImageView
    lateinit var imgBackBtn: ImageView

    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        var view = binding.root

        activity_ = activity as MainActivity
        activity_.setDrawerLocked()

        return view
    }

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBarChat1.visibility = View.VISIBLE

        userViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        binding.viewModel = userViewModel
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
            view.findNavController().navigate(R.id.action_chatFragment_to_homeFragment)
        }

        binding.btnSendChatLog.setOnClickListener {
            userViewModel.sendMessage(
                Utils.getUidLoggedIn(),
                args.User.userUid!!,
                args.User.username!!,
                args.User.imageUrl!!
            )
        }

        binding.progressBarChat1.visibility = View.GONE
        userViewModel.getMessages(args.User.userUid!!).observe(viewLifecycleOwner, Observer {
            binding.progressBarChat1.visibility = View.GONE
            initRecyclerView(it)
        })


    }

    private fun initRecyclerView(messages: List<Message>) {
        messageAdapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(context)
        binding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        messageAdapter.setMessageList(messages)
        messageAdapter.notifyDataSetChanged()
        binding.messagesRecyclerView.adapter = messageAdapter

    }

    private fun changeToolbarItemsVisibility() {
        imgUserProfile.visibility = View.VISIBLE
        tvToolbarUsername.visibility = View.VISIBLE
        tvToolbarStatus.visibility = View.VISIBLE
        imgBackBtn.visibility = View.VISIBLE
        tvToolbarTitle.visibility = View.GONE
    }


    private fun setToolbarData() {
        tvToolbarUsername.text = args.User.username
        tvToolbarStatus.text = args.User.status
        Log.d("ChatFragment", "setToolbarData: ${args.User.status} and ${args.User.username}")
        if (args.User.status == "Online") {
            imgUserProfile.borderColor =
                ContextCompat.getColor(requireContext(), R.color.online_status)
        } else {
            imgUserProfile.borderColor =
                ContextCompat.getColor(requireContext(), R.color.offline_status)
        }
        Glide.with(requireActivity()).load(args.User.imageUrl).into(imgUserProfile)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        activity_.setDrawerUnlocked()
        _binding = null
    }

}