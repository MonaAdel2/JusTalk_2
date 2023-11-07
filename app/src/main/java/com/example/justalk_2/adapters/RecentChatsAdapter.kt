package com.example.justalk_2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.justalk_2.R
import com.example.justalk_2.model.RecentChats
import de.hdodenhof.circleimageview.CircleImageView

class RecentChatsAdapter: RecyclerView.Adapter<RecentChatsViewHolder>() {

    private var recentChatsList = listOf<RecentChats>()
    private var listener: onRecentChatClicked? = null
    private var recentChats = RecentChats()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recent_chats_item, parent, false)
        return RecentChatsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return recentChatsList.size
    }

    override fun onBindViewHolder(holder: RecentChatsViewHolder, position: Int) {
        val recentChatsList = recentChatsList[position]
        recentChats = recentChatsList

        holder.tvFriendName.text = recentChatsList.name

        // to display only a few words of the message.
        val messagePart = recentChatsList.message!!.split(" ").take(4).joinToString(" ")
        val makeLastMessage = "${recentChatsList.person}: ${messagePart} "
        holder.tvMessage.text = makeLastMessage

        Glide.with(holder.itemView.context).load(recentChatsList.friendsImage).placeholder(R.drawable.person_icon).into(holder.imgFriend)
        holder.tvTime.text = recentChatsList.time!!.substring(0,5)

        holder.itemView.setOnClickListener {
            listener?.getOnRecentChatClicked(position, recentChatsList)
        }
    }

    fun setOnRecentChatsListener(listener: onRecentChatClicked){
        this.listener = listener
    }

    fun setRecentChatList(list: List<RecentChats>){
        this.recentChatsList = list
    }

}

class RecentChatsViewHolder(row: View): RecyclerView.ViewHolder(row){
    val imgFriend: CircleImageView = row.findViewById(R.id.img_friend_recent_chat_item)
    val tvFriendName: TextView = row.findViewById(R.id.tv_friend_name_recent_chat_item)
    val tvMessage: TextView = row.findViewById(R.id.tv_message__recent_chat_item)
    val tvTime: TextView = row.findViewById(R.id.tv_time_recent_chat_item)

}

interface onRecentChatClicked{
    fun getOnRecentChatClicked(position: Int, recentChatsList: RecentChats)
}