package com.example.justalk_2.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.example.justalk_2.MainActivity
import com.example.justalk_2.R
import com.example.justalk_2.Utils
import com.example.justalk_2.adapters.MessageAdapter
import com.example.justalk_2.databinding.FragmentChatBinding
import com.example.justalk_2.databinding.FragmentHomeBinding
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
    
    private lateinit var messageAdapter : MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentChatBinding.inflate(inflater, container, false)
        var view = binding.root

        activity_ = activity as MainActivity
        activity_.setDrawerLocked()

        return view
    }

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        binding.viewModel = userViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        toolbar = requireActivity().findViewById<View>(com.example.justalk_2.R.id.toolbar) as Toolbar
        tvToolbarTitle = toolbar.findViewById(com.example.justalk_2.R.id.tv_title_app_bar) as TextView

        tvToolbarUsername = toolbar.findViewById(com.example.justalk_2.R.id.tv_username_chat_toolbar) as TextView
        tvToolbarStatus = toolbar.findViewById(com.example.justalk_2.R.id.tv_user_status_chat_toolbar) as TextView
        imgUserProfile = toolbar.findViewById(com.example.justalk_2.R.id.img_user_chat_toolbar) as CircleImageView
        imgBackBtn = toolbar.findViewById(com.example.justalk_2.R.id.btn_back_chat_toolbar) as ImageView

        changeToolbarItemsVisibility()
        setToolbarData()


        imgBackBtn.setOnClickListener{
            view.findNavController().navigate(R.id.action_chatFragment_to_homeFragment)
        }

        binding.btnSendChatLog.setOnClickListener {
            userViewModel.sendMessage(Utils.getUiLoggedIn(),
                                        args.User.userUid!!,
                                        args.User.username!!,
                                        args.User.imageUrl!!)
        }

        userViewModel.getMessages(args.User.userUid!!).observe(viewLifecycleOwner, Observer {
            
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

    private fun changeToolbarItemsVisibility(){
        imgUserProfile.visibility = View.VISIBLE
        tvToolbarUsername.visibility = View.VISIBLE
        tvToolbarStatus.visibility = View.VISIBLE
        imgBackBtn.visibility = View.VISIBLE
        tvToolbarTitle.visibility = View.GONE
    }


    private fun setToolbarData(){
        tvToolbarUsername.text = args.User.username
        tvToolbarStatus.text = args.User.status
        Glide.with(requireActivity()).load(args.User.imageUrl).into(imgUserProfile)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        activity_.setDrawerUnlocked()
//        toolbar.setNavigationIcon(R.drawable.menu_icon);
        _binding = null
    }

}