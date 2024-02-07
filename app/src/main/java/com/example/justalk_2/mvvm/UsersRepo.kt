package com.example.justalk_2.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.justalk_2.Utils
import com.example.justalk_2.model.User
import com.google.firebase.firestore.FirebaseFirestore

class UsersRepo {

    private  var firestore = FirebaseFirestore.getInstance()

    fun getUsers(): LiveData<List<User>>{
        val users = MutableLiveData<List<User>>()

        firestore.collection("Users").addSnapshotListener{snapshot, exception->
            if(exception!= null){
                return@addSnapshotListener
            }

            val usersList = mutableListOf<User>()
            
            snapshot?.documents?.forEach {document ->

                val user = document.toObject(User::class.java)

                // skip the current user from being displayed
                if(user!!.userUid != Utils.getUidLoggedIn()){
                    user.let{
                        usersList.add(it)
                    }
                }

            }

            users.value = usersList

        }

        return users

    }
}