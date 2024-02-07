package com.example.justalk_2.notifications

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.justalk_2.R
import com.example.justalk_2.SharedPrefs
import com.example.justalk_2.Utils
import com.google.firebase.firestore.FirebaseFirestore

private const val CHANNEL_Id = "my_channel"
class NotificationReplay: BroadcastReceiver() {

    val firestore = FirebaseFirestore.getInstance()
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager : NotificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val removeInput = RemoteInput.getResultsFromIntent(intent)

        if (removeInput != null){
            val repliedText = removeInput.getString("Key_REPLY_TEXT")
            val mySharedPrefs = SharedPrefs(context)

            val friendId = mySharedPrefs.getValue("friendId")
            val chatRoomId = mySharedPrefs.getValue("chatRoomId")
            val friendName = mySharedPrefs.getValue("friendName")
            val friendImage = mySharedPrefs.getValue("friendImage")

            val hashMap = hashMapOf<String, Any>("sender" to Utils.getUidLoggedIn(),
                "time" to Utils.getTime(), "receiver" to friendId!!,
                "message" to repliedText!!)

            // for chat room
            firestore.collection("Messages").document(chatRoomId!!)
                .collection("Chats").document(Utils.getTime())
                .set(hashMap)

            // for recent chats
            val hashmapForRecent = hashMapOf<String, Any>(
                "friendId" to friendId,
                "time" to Utils.getTime(),
                "sender" to Utils.getUidLoggedIn(),
                "message" to repliedText,
                "friendsImage" to friendImage!!,
                "name" to friendName!!,
                "person" to "you"
            )
            firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(friendId)
                .set(hashmapForRecent)

            val updatedHashMap = hashMapOf<String, Any>(
                "message" to repliedText,
                "time" to Utils.getTime(),
                "person" to friendName
            )

            firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(friendId)
                .set(updatedHashMap)


            val sharedCustomPref = SharedPrefs(context)
            val replayID = sharedCustomPref.getIntValue("values", 0)
            val repliedNotfication = NotificationCompat.Builder(context, CHANNEL_Id)
                .setSmallIcon(R.drawable.chat_icon)
                .setContentText("Replay Sent")
                .build()

            notificationManager.notify(replayID!!, repliedNotfication)



        }
    }

}