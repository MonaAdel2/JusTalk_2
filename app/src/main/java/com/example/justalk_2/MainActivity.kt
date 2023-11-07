package com.example.justalk_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.justalk_2.databinding.ActivityMainBinding
import com.example.justalk_2.model.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, NavigationViewController {
    private lateinit var navController: NavController
    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar: Toolbar
    private lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    lateinit var navigationView: NavigationView
    lateinit var header: View
    lateinit var tvUsername:TextView
    private var username: String? = null
    lateinit var imgProfile: CircleImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

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

    }

    private fun addUserDataToNavigationDrawer() {
        firestore.collection("users").addSnapshotListener{snapshot, exception->
            if(exception!= null){
                return@addSnapshotListener
            }
            snapshot?.documents?.forEach {document ->
                val user = document.toObject(User::class.java)
                // get the current user document
                if(user!!.userUid == Utils.getUiLoggedIn()){
                    username = user.username
                    tvUsername.text = username
                    Glide.with(this).load(user.imageUrl).into(imgProfile)
                }
            }
        }
    }

    fun createDrawerLayout(){
        drawerLayout = binding.drawerLayout
        var toogle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        var navigationView : NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }



    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount > 0){
            super.onBackPressed()
        }else{
            if (navController.currentDestination?.id == R.id.homeFragment){
                moveTaskToBack(true)
            }else{
                super.onBackPressed()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_setting->{
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                goToSettings()
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.action_logout->{
                logout()
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
        }
        return false
    }

    private fun logout() {
        auth.signOut()
        startActivity(Intent(this, SignInActivity::class.java))
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
}