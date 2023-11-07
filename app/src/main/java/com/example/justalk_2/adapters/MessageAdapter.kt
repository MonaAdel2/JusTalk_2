package com.example.justalk_2.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.justalk_2.R
import com.example.justalk_2.Utils
import com.example.justalk_2.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(): RecyclerView.Adapter<myViewHolder>() {

    var messagesList = listOf<Message>()
    val SENDER = 0
    val RECIEVER = 1

    var firestore = FirebaseFirestore.getInstance()
    private val TAG = "MessageAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        return if (viewType == SENDER){
            val view = inflater.inflate(R.layout.sender_row, parent, false)
            myViewHolder(view)
        }else{
            val view = inflater.inflate(R.layout.reciever_row, parent, false)
            myViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val message = messagesList[position]
        holder.message.visibility = View.VISIBLE
        holder.time.visibility = View.VISIBLE

        holder.message.text = message.message
        holder.time.text = message.time?.substring(0,5)?: ""
//        holder.time.text = message.time?.substring(11,16)?: ""

        if (getItemViewType(position) == SENDER){
            firestore.collection("users").document(Utils.getUiLoggedIn()).get().addOnSuccessListener {documentSnapshot->
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "onBindViewHolder: ${Utils.getUiLoggedIn()}")
                    val userData = documentSnapshot.data
                    Glide.with(holder.itemView.context).load(documentSnapshot.getString("imageUrl"))
                        .placeholder(R.drawable.person_icon).into(holder.image)
                }
            }
        }else{
            firestore.collection("users").document(message.sender!!).get().addOnSuccessListener {documentSnapshot->
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "onBindViewHolder: ${message.receiver!!}")
                    Log.d(TAG, "onBindViewHolder: ${message.sender!!}")
                    val userData = documentSnapshot.data
                    Glide.with(holder.itemView.context).load(documentSnapshot.getString("imageUrl"))
                        .placeholder(R.drawable.person_icon).into(holder.image)
                }
        }


        }
    }
    fun setMessageList(list: List<Message>){
        this.messagesList = list
    }

    override fun getItemViewType(position: Int): Int =
        if (messagesList[position].sender == Utils.getUiLoggedIn()) SENDER else RECIEVER

}

class myViewHolder(row: View): RecyclerView.ViewHolder(row){
    val message: TextView = row.findViewById(R.id.tv_message_item)
    val time: TextView = row.findViewById(R.id.tv_time_item)
    val image: CircleImageView = row.findViewById(R.id.img_user_item)

}