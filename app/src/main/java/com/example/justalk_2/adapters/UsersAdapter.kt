package com.example.justalk_2.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.justalk_2.R
import com.example.justalk_2.model.User
import de.hdodenhof.circleimageview.CircleImageView

class UsersAdapter : RecyclerView.Adapter<UserHolder>() {

    private var listOfUsers = listOf<User>()
    private var listener: OnUserClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val row =
            LayoutInflater.from(parent.context).inflate(R.layout.user_list_item, parent, false)
        return UserHolder(row)
    }

    override fun getItemCount(): Int {
        return listOfUsers.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val users = listOfUsers[position]
        val name = users.username!!.split("\\s".toRegex())[0]
        holder.profileName.text = name
        Log.d("image->", "onBindViewHolder: ${name}")

        if (users.status.equals("Online")) {
            holder.statusImageView.setImageResource(R.drawable.onlinestatus)

        } else {
            holder.statusImageView.setImageResource(R.drawable.offlinestatus)
        }

        Glide.with(holder.itemView.context)
            .load(users.imageUrl)
            .placeholder(R.drawable.person_icon)
            .into(holder.imageProfile)

        Log.d("image->", "onBindViewHolder: ${users.imageUrl}")
        holder.itemView.setOnClickListener {
            listener?.onUserSelected(position, users)
        }

    }

    fun setList(list: List<User>) {
        this.listOfUsers = list
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: OnUserClickListener) {
        this.listener = listener
    }
}

class UserHolder(row: View) : RecyclerView.ViewHolder(row) {
    val profileName: TextView = row.findViewById(R.id.userName)
    val imageProfile: CircleImageView = row.findViewById(R.id.imageViewUser)
    val statusImageView: ImageView = row.findViewById(R.id.statusOnline)

}

interface OnUserClickListener {
    fun onUserSelected(position: Int, users: User)
}