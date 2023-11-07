package com.example.justalk_2.mvvm

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justalk_2.MyApplication
import com.example.justalk_2.SharedPrefs
import com.example.justalk_2.Utils
import com.example.justalk_2.model.Message
import com.example.justalk_2.model.RecentChats
import com.example.justalk_2.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatAppViewModel: ViewModel() {
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    val message = MutableLiveData<String>()

    private val firesore = FirebaseFirestore.getInstance()

    val usersRepo = UsersRepo()
    val messageRepo = MessageRepo()
    val recentChatRepo = ChatListRepo()

    init {
        getCurrentUser()
        getRecentChat()
    }

    fun getUsers(): LiveData<List<User>>{
        return usersRepo.getUsers()
    }

    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {

        val context = MyApplication.instance.applicationContext

        firesore.collection("users").document(Utils.getUiLoggedIn()).addSnapshotListener{value, error->
            if(value!!.exists() && value != null){
                val user = value.toObject(User::class.java)
                name.value = user?.username!!
                imageUrl.value = user?.imageUrl!!

                val sharedPrefs = SharedPrefs(context)
                sharedPrefs.setValue("username", user.username!!)
            }

        }
    }

    fun sendMessage(sender: String, receiver:String, friendName: String, friendImage: String)
                    = viewModelScope.launch(Dispatchers.IO) {
        val context = MyApplication.instance.applicationContext

        var hashmap = hashMapOf<String, Any>(
            "sender" to sender,
            "receiver" to receiver,
            "message" to message.value!!,
            "time" to Utils.getTime()
        )

        val uniqueID = listOf(sender, receiver).sorted()
        uniqueID.joinToString(separator = "")

        val friendNameSplit = friendName.split("\\s".toRegex())[0]
        val mySharedPrefs = SharedPrefs(context)
        mySharedPrefs.setValue("friendId", receiver)
        mySharedPrefs.setValue("chatRoomId", uniqueID.toString())
        mySharedPrefs.setValue("friendName", friendNameSplit)
        mySharedPrefs.setValue("friendImage", friendImage)


        firesore.collection("messages").document(uniqueID.toString())
            .collection("chats").document(Utils.getTime())
            .set(hashmap).addOnCompleteListener { task->

                val hashmapForRecent = hashMapOf<String, Any>(
                    "friendId" to receiver,
                    "time" to Utils.getTime(),
                    "sender" to Utils.getUiLoggedIn(),
                    "message" to message.value!!,
                    "friendsImage" to friendImage,
                    "name" to friendName,
                    "person" to "you"
                )

                firesore.collection("Conversations${Utils.getUiLoggedIn()}").document(receiver).set(hashmapForRecent)
                firesore.collection("Conversations${receiver}").document(Utils.getUiLoggedIn())
                    .update("message", message.value!!, "time", Utils.getTime(), "person", name.value!!)

                // to clear the edit text after pressing send
                if(task.isSuccessful){
                    message.value = ""
                }
            }
    }

    fun getMessages(friendId: String): LiveData<List<Message>>{
        return messageRepo.getMessages(friendId)
    }

    fun getRecentChat(): LiveData<List<RecentChats>>{
        return recentChatRepo.getAllChatsList()
    }

    fun updateProfile() = viewModelScope.launch(Dispatchers.IO) {
        val context = MyApplication.instance.applicationContext
        val hashmapUser = hashMapOf<String, Any>("username" to name.value!!,
                                                "imageUrl" to imageUrl.value!!)

        firesore.collection("users").document(Utils.getUiLoggedIn()).update(hashmapUser).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(context, "Profile is updated", Toast.LENGTH_SHORT).show()

            }
        }

        val sharedPrefs = SharedPrefs(context)
        val friendID = sharedPrefs.getValue("friendId")

        val hashmapUpdated = hashMapOf<String, Any>("friendsImage" to imageUrl.value!!,
            "name" to name.value!!, "person" to name.value!!)

        firesore.collection("Conversations${friendID}").document(Utils.getUiLoggedIn()).update(hashmapUpdated)

        firesore.collection("Conversations${Utils.getUiLoggedIn()}").document(friendID!!).update("person", "you")

    }

}