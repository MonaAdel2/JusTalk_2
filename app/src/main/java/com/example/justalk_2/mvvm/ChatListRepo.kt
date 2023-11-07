package com.example.justalk_2.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.justalk_2.Utils
import com.example.justalk_2.model.RecentChats
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

class ChatListRepo {

    private val firestore = FirebaseFirestore.getInstance()

    fun getAllChatsList(): LiveData<List<RecentChats>>{

        val mainChatList = MutableLiveData<List<RecentChats>>()

        firestore.collection("Conversations${Utils.getUiLoggedIn()}").orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener{value, error ->
                if (error != null){
                    return@addSnapshotListener
                }
                val chatList = mutableListOf<RecentChats>()

                value?.forEach{document ->
                    val recentChatModel = document.toObject(RecentChats::class.java)

                    if (recentChatModel.sender.equals(Utils.getUiLoggedIn())){
                        recentChatModel.let {
                            chatList.add(it)
                        }
                    }

                }

                mainChatList.value = chatList


            }
        return mainChatList
    }
}