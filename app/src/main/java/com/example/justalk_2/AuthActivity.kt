package com.example.justalk_2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

class AuthActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    lateinit var viewShaded: View

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewShaded = findViewById(R.id.shadeView_auth)

        var navHostFragment =
            supportFragmentManager.findFragmentById(R.id.auth_fragments_contanier) as NavHostFragment
        navController = navHostFragment.navController

    }
}