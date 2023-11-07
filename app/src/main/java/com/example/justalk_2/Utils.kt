package com.example.justalk_2

import com.google.firebase.auth.FirebaseAuth
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class Utils {
    companion object{
        private val auth  = FirebaseAuth.getInstance()
        private var userId : String = ""

        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2


        fun getUiLoggedIn(): String{

            if(auth.currentUser != null){
                userId = auth.currentUser!!.uid
            }

            return userId
        }

//        fun getTime(): String {
//            val formatter = SimpleDateFormat("HH:mm:ss")
//            val date = Date(System.currentTimeMillis())
//            val strDate = formatter.format(date)
//
//            return strDate
//        }

        fun getTime(): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = Date(System.currentTimeMillis())
            val strDateTime = formatter.format(date)
            return strDateTime
        }


    }
}