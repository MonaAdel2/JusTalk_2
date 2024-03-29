package com.example.justalk_2.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.justalk_2.NavigationViewController
import com.example.justalk_2.R
import com.example.justalk_2.Utils
import com.example.justalk_2.databinding.ActivityMainBinding
import com.example.justalk_2.model.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    NavigationViewController {

    private val TAG = "MainActivity"
    private lateinit var navController: NavController
    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar: Toolbar
    private lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    lateinit var navigationView: NavigationView
    lateinit var header: View
    lateinit var tvUsername: TextView
    private var username: String? = null
    lateinit var imgProfile: CircleImageView
    lateinit var viewShaded: View
    private var token = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        viewShaded = findViewById(R.id.shadeView_main)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        // get references to add the username and profile image into navigation drawer
        navigationView = binding.navView
        header = navigationView.inflateHeaderView(R.layout.navigation_header)
        tvUsername = header.findViewById<TextView>(R.id.tv_username_header)
        imgProfile = header.findViewById<CircleImageView>(R.id.img_profile_header)

        var navHostFragment =
            supportFragmentManager.findFragmentById(R.id.frg_contanier_app) as NavHostFragment
        navController = navHostFragment.navController

        createDrawerLayout()
        addUserDataToNavigationDrawer()
        generateToken()

        imgProfile.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            openImageProfile()
        }

    }

    private fun addUserDataToNavigationDrawer() {
        firestore.collection("Users").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                return@addSnapshotListener
            }
            snapshot?.documents?.forEach { document ->
                val user = document.toObject(User::class.java)
                // get the current user document
                if (user!!.userUid == Utils.getUidLoggedIn()) {
                    username = user.username
                    tvUsername.text = username
                    Glide.with(applicationContext).load(user.imageUrl).into(imgProfile)
                }
            }
        }
    }

    fun createDrawerLayout() {
        drawerLayout = binding.drawerLayout
        var toogle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        var navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
            if (navController.currentDestination?.id == R.id.homeFragment) {
                moveTaskToBack(true)
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_setting -> {
                goToSettings()
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }

            R.id.action_logout -> {
                if (auth.currentUser != null) {
                    firestore.collection("Users").document(Utils.getUidLoggedIn())
                        .update("status", "Offline")
                }
                logout()
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
        }
        return false
    }

    private fun logout() {
        FirebaseMessaging.getInstance().deleteToken().addOnSuccessListener {
            Log.d(TAG, "logout: the token is deleted")
        }

        auth.signOut()
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    private fun goToSettings() {
        navController.navigate(R.id.settingFragment)
    }

    override fun setDrawerLocked() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        toolbar.navigationIcon = null
    }

    override fun setDrawerUnlocked() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    private fun generateToken() {
        val firebaseInstance = FirebaseInstallations.getInstance()
        firebaseInstance.id.addOnSuccessListener { installationID ->
            FirebaseMessaging.getInstance().token.addOnSuccessListener { retrivedToken ->
                token = retrivedToken
                val hashmap = hashMapOf<String, Any>("token" to token)
                firestore.collection("Tokens").document(Utils.getUidLoggedIn()).set(hashmap)
                    .addOnSuccessListener {
                        Log.d(TAG, "generateToken: the token is generated successfully.")
                    }.addOnFailureListener {
                        Log.d(TAG, "generateToken: unable to generate the token")
                    }

            }

        }
    }

    private fun openImageProfile() {
        navController.navigate(R.id.imageProfileViewFragment)
    }

    override fun onPause() {
        super.onPause()
        if (auth.currentUser != null) {
            firestore.collection("Users").document(Utils.getUidLoggedIn())
                .update("status", "Offline")
        }
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser != null) {
            firestore.collection("Users").document(Utils.getUidLoggedIn())
                .update("status", "Online")
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            firestore.collection("Users").document(Utils.getUidLoggedIn())
                .update("status", "Online")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (auth.currentUser != null) {
            firestore.collection("Users").document(Utils.getUidLoggedIn())
                .update("status", "Offline")
        }
    }
}