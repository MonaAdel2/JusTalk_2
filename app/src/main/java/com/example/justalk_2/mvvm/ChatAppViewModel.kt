package com.example.justalk_2.mvvm

import android.util.Log
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
import com.example.justalk_2.notifications.FirebaseServices
import com.example.justalk_2.notifications.entity.NotificationData
import com.example.justalk_2.notifications.entity.PushNotification
import com.example.justalk_2.notifications.entity.Token
import com.example.justalk_2.notifications.network.RetrofitInstance
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatAppViewModel : ViewModel() {
    private val TAG = "ChatAppViewModel"
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    val message = MutableLiveData<String>()

    private val firestore = FirebaseFirestore.getInstance()

//    var notificationListener: ListenerRegistration? = null

    val usersRepo = UsersRepo()
    val messageRepo = MessageRepo()
    val recentChatRepo = ChatListRepo()
    var token: String? = null

    init {
        getCurrentUser()
        getRecentChat()
    }
    companion object{
        var notificationListener: ListenerRegistration? = null
    }


    fun getUsers(): LiveData<List<User>> {
        return usersRepo.getUsers()
    }

    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {

        val context = MyApplication.instance.applicationContext

        Log.d(TAG, "getCurrentUser: current user: ${Utils.getUidLoggedIn()}")
        firestore.collection("Users").document(Utils.getUidLoggedIn())
            .addSnapshotListener { value, error ->
                if (value!!.exists() && value != null) {
                    val user = value.toObject(User::class.java)
                    name.value = user?.username!!
                    imageUrl.value = user.imageUrl!!

                    val sharedPrefs = SharedPrefs(context)
                    sharedPrefs.setValue("username", user.username)
                    Log.d(TAG, "getCurrentUser: the current user from the shared prefs ${sharedPrefs.getValue("username")}")
                }

            }
    }

    fun sendMessage(sender: String, receiver: String, friendName: String, friendImage: String) =
        viewModelScope.launch(Dispatchers.IO) {
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


            // Messages -> [sender, receiver] -> chats -> every single message
            val tempMessage = message.value!!
            firestore.collection("Messages").document(uniqueID.toString())
                .collection("Chats").document(Utils.getTime())
                .set(hashmap).addOnCompleteListener { task ->

                    val hashmapForRecent = hashMapOf<String, Any>(
                        "friendId" to receiver,
                        "time" to Utils.getTime(),
                        "sender" to Utils.getUidLoggedIn(),
                        "message" to message.value!!,
                        "friendsImage" to friendImage,
                        "name" to friendName,
                        "person" to "you"
                    )

                    firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(receiver)
                        .set(hashmapForRecent)
                    Log.d(
                        TAG,
                        "sendMessage: create the recent chat for user ${Utils.getUidLoggedIn()}"
                    )

                    Log.d(TAG, "sendMessage: ${message.value!!}")
                    val conversationRef = firestore.collection("Conversation${receiver}")
                        .document(Utils.getUidLoggedIn())
                        .get().addOnSuccessListener { documentSnapshot ->
                            Log.d(
                                TAG,
                                "sendMessage: get the reference to the collection of receiver"
                            )
                            if (documentSnapshot.exists()) {
                                // Document exists, perform the update
                                firestore.collection("Conversation${receiver}")
                                    .document(Utils.getUidLoggedIn())
                                    .update(
                                        "message", tempMessage,
                                        "time", Utils.getTime(),
                                        "person", name.value!!
                                    )

                            } else {
                                // Document does not exist, create it first
                                Log.d(TAG, "sendMessage: the message is: ${message.value!!}")
                                val data = hashMapOf(
                                    "friendId" to Utils.getUidLoggedIn(),
                                    "time" to Utils.getTime(),
                                    "sender" to Utils.getUidLoggedIn(),
                                    "message" to tempMessage,
                                    "friendsImage" to imageUrl.value!!,
                                    "name" to name.value!!,
                                    "person" to name.value!!
                                )
                                firestore.collection("Conversation${receiver}")
                                    .document(Utils.getUidLoggedIn()).set(data)

                            }

                        }

                    // for notification
//                    firestore.collection("Tokens").document(receiver)
//                        .addSnapshotListener { value, error ->
//                            if (value != null && value.exists()) {
//                                val tokenObject = value.toObject(Token::class.java)
//                                token = tokenObject?.token!!
//                                Log.d(TAG, "sendMessage: the token is ${token}")
//
//                                val loggedUserName =
//                                    mySharedPrefs.getValue("username")!!.split("\\s".toRegex())[0]
//                                if (message.value!!.isNotEmpty() && receiver.isNotEmpty()) {
//                                    PushNotification(
//                                        NotificationData(
//                                            loggedUserName,
//                                            message.value!!
//                                        ), token!!
//                                    ).also {
//                                        sendNotification(it)
//                                        Log.d(TAG, "sendMessage: Notification sent")
//                                    }
//                                } else {
//                                    Log.d(TAG, "sendMessage: No token, no notification")
//                                    Log.d(TAG, "sendMessage: $token")
//                                }
//
//                            }
//
//                            // to clear the edit text after pressing send
//                            if (task.isSuccessful) {
//                                message.value = ""
//                            }
//                        }



                    // new code for notification
                    firestore.collection("Tokens").document(receiver)
                        .addSnapshotListener { value, error ->
                            if (value != null && value.exists()) {
                                val tokenObject = value.toObject(Token::class.java)
                                val recipientToken = tokenObject?.token

                                // Get sender's token
                                val senderToken = FirebaseServices.token

                                if (recipientToken != null && recipientToken != senderToken) {
                                    val loggedUserName =
                                        mySharedPrefs.getValue("username")!!.split("\\s".toRegex())[0]
                                    if (message.value!!.isNotEmpty() && receiver.isNotEmpty()) {
                                        PushNotification(
                                            NotificationData(
                                                loggedUserName,
                                                message.value!!
                                            ), recipientToken
                                        ).also {
                                            sendNotification(it)
                                            Log.d(TAG, "sendMessage: Notification sent")
                                        }
                                    } else {
                                        Log.d(TAG, "sendMessage: No token, no notification")
                                        Log.d(TAG, "sendMessage: $token")
                                    }
                                }
                            }

                            // to clear the edit text after pressing send
                            if (task.isSuccessful) {
                                message.value = ""
                            }
                        }



                }

        }

    private fun sendNotification(notification: PushNotification) = viewModelScope.launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)

        } catch (e: Exception) {
            Log.d(TAG, "sendNotification: ${e.message}")
        }
    }

    fun getMessages(friendId: String): LiveData<List<Message>> {
        return messageRepo.getMessages(friendId)
    }

    fun getRecentChat(): LiveData<List<RecentChats>> {
        return recentChatRepo.getAllChatsList()
    }

    fun updateProfile() = viewModelScope.launch(Dispatchers.IO) {
        val context = MyApplication.instance.applicationContext
        val hashmapUser = hashMapOf<String, Any>(
            "username" to name.value!!,
            "imageUrl" to imageUrl.value!!
        )

        firestore.collection("Users").document(Utils.getUidLoggedIn()).update(hashmapUser)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Profile is updated", Toast.LENGTH_SHORT).show()

                }
            }

        val sharedPrefs = SharedPrefs(context)
        val friendID = sharedPrefs.getValue("friendId")

        val hashmapUpdated = hashMapOf<String, Any>(
            "friendsImage" to imageUrl.value!!,
            "name" to name.value!!, "person" to name.value!!
        )

        firestore.collection("Conversation$friendID").document(Utils.getUidLoggedIn())
            .update(hashmapUpdated)

        firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(friendID!!)
            .update("person", "you")

    }

}