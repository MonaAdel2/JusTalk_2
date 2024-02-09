package com.example.justalk_2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.justalk_2.MainActivity
import com.example.justalk_2.adapters.OnUserClickListener
import com.example.justalk_2.adapters.RecentChatsAdapter
import com.example.justalk_2.adapters.UsersAdapter
import com.example.justalk_2.adapters.onRecentChatClicked
import com.example.justalk_2.databinding.FragmentHomeBinding
import com.example.justalk_2.model.RecentChats
import com.example.justalk_2.model.User
import com.example.justalk_2.mvvm.ChatAppViewModel
import de.hdodenhof.circleimageview.CircleImageView


class HomeFragment : Fragment(), OnUserClickListener, onRecentChatClicked {
    private val TAG = "HomeFragment"

    lateinit var recyclerFriends: RecyclerView
    lateinit var userAdapter: UsersAdapter

    lateinit var userViewModel: ChatAppViewModel

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    lateinit var activity_: MainActivity

    lateinit var tvToolbarTitle: TextView
    lateinit var tvToolbarUsername: TextView
    lateinit var tvToolbarStatus: TextView
    lateinit var toolbar: Toolbar
    lateinit var imgUserProfile: CircleImageView
    lateinit var imgBackBtn: ImageView

    lateinit var recentChatsAdapter: RecentChatsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        var view = binding.root
        activity_ = activity as MainActivity
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBarHome.visibility = View.VISIBLE
//        activity_.viewShaded.visibility = View.VISIBLE
        userViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        userAdapter = UsersAdapter()
        recyclerFriends = binding.rvFriendsHomeFrg

        activity_.createDrawerLayout()

        // to add title for the toolbar
        toolbar =
            requireActivity().findViewById<View>(com.example.justalk_2.R.id.toolbar) as Toolbar
        tvToolbarTitle =
            toolbar.findViewById(com.example.justalk_2.R.id.tv_title_app_bar) as TextView
        tvToolbarTitle.text = "Chats"

        tvToolbarUsername =
            toolbar.findViewById(com.example.justalk_2.R.id.tv_username_chat_toolbar) as TextView
        tvToolbarStatus =
            toolbar.findViewById(com.example.justalk_2.R.id.tv_user_status_chat_toolbar) as TextView
        imgUserProfile =
            toolbar.findViewById(com.example.justalk_2.R.id.img_user_chat_toolbar) as CircleImageView
        imgBackBtn =
            toolbar.findViewById(com.example.justalk_2.R.id.btn_back_chat_toolbar) as ImageView

        changeToolbarItemsVisibility()

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerFriends.layoutManager = layoutManager

        userViewModel.getUsers().observe(viewLifecycleOwner, Observer {
            binding.progressBarHome.visibility = View.GONE
//            activity_.viewShaded.visibility = View.GONE
            userAdapter.setList(it)
            recyclerFriends.adapter = userAdapter
        })

        userAdapter.setOnClickListener(this)
        recentChatsAdapter = RecentChatsAdapter()

        userViewModel.getRecentChat().observe(viewLifecycleOwner, Observer {
            binding.progressBarHome.visibility = View.GONE
//            activity_.viewShaded.visibility = View.GONE
            binding.rvChatsHomeFrg.layoutManager = LinearLayoutManager(activity)
            recentChatsAdapter.setRecentChatList(it)
            binding.rvChatsHomeFrg.adapter = recentChatsAdapter
            checkRecentChatsNumber(it)
        })

        recentChatsAdapter.setOnRecentChatsListener(this)

    }

    private fun changeToolbarItemsVisibility() {
        imgUserProfile.visibility = View.GONE
        tvToolbarUsername.visibility = View.GONE
        tvToolbarStatus.visibility = View.GONE
        imgBackBtn.visibility = View.GONE
        tvToolbarTitle.visibility = View.VISIBLE
    }

    private fun checkRecentChatsNumber(chatsList: List<RecentChats>) {
        if (chatsList.isEmpty()) {
            binding.tvNoRecentChats.visibility = View.VISIBLE
        } else {
            binding.tvNoRecentChats.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onUserSelected(position: Int, users: User) {
        val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(users)
        view?.findNavController()?.navigate(action)
    }

    override fun getOnRecentChatClicked(position: Int, recentChatsList: RecentChats) {
        val action =
            HomeFragmentDirections.actionHomeFragmentToChatFromHomeFragment(recentChatsList)
        view?.findNavController()?.navigate(action)
    }

}