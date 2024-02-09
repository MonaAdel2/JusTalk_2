package com.example.justalk_2.mvvm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.justalk_2.Utils
import com.example.justalk_2.model.RecentChats
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatListRepo {
    private val TAG = "ChatListRepo"

    private val firestore = FirebaseFirestore.getInstance()

    fun getAllChatsList(): LiveData<List<RecentChats>> {

        val mainChatList = MutableLiveData<List<RecentChats>>()

        firestore.collection("Conversation${Utils.getUidLoggedIn()}")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val chatList = mutableListOf<RecentChats>()

                value?.forEach { document ->
                    val recentChatModel = document.toObject(RecentChats::class.java)
                    recentChatModel.let { recentChat ->
                        firestore.collection("Users").document(recentChat.friendId!!).get()
                            .addOnSuccessListener {
                                if (it.exists()) {
                                    val data = it.data
                                    Log.d(TAG, "getFriendStatus: ${data?.get("status").toString()}")
                                    recentChat.status = data?.get("status").toString()

                                }
                            }
                        chatList.add(recentChat)
                    }
                }
                mainChatList.value = chatList
            }
        return mainChatList
    }

}