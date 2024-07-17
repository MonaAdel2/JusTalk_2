package com.example.justalk_2.activities

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.justalk_2.R

class AuthActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    lateinit var viewShaded: View
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewShaded = findViewById(R.id.shadeView_auth)

        var navHostFragment =
            supportFragmentManager.findFragmentById(R.id.auth_fragments_contanier) as NavHostFragment
        navController = navHostFragment.navController

        sharedPreferences = getSharedPreferences("modes", MODE_PRIVATE)
        var isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        if (isDarkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}