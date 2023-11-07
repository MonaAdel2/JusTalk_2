package com.example.justalk_2.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.justalk_2.Utils
import com.example.justalk_2.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MessageRepo {

    private val firestore = FirebaseFirestore.getInstance()

    fun getMessages(friendId: String): LiveData<List<Message>>{

        val messages = MutableLiveData<List<Message>>()

        val uniqueId = listOf(Utils.getUiLoggedIn(), friendId).sorted()
        uniqueId.joinToString(separator = "")

        firestore.collection("messages").document(uniqueId.toString()).collection("chats").orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {
                    return@addSnapshotListener
                }

                val messagesList = mutableListOf<Message>()

                if (!snapshot!!.isEmpty) {
                    snapshot.documents.forEach { document ->
                        val messageModel = document.toObject(Message::class.java)

                        if (messageModel!!.sender.equals(Utils.getUiLoggedIn()) && messageModel.receiver.equals(friendId) ||
                            messageModel.sender.equals(friendId) && messageModel.receiver.equals(Utils.getUiLoggedIn())
                        ) {
                            messageModel.let {
                                messagesList.add(it!!)
                            }
                        }
                    }
                    messages.value = messagesList
                }
            }

        return messages

    }
}