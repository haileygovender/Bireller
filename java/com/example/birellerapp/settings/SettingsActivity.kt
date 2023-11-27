package com.example.birellerapp.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.example.birellerapp.HomeActivity
import com.example.birellerapp.MainActivity
import com.example.birellerapp.R
import com.example.birellerapp.authentication.AuthenticationActivity
import com.example.birellerapp.authentication.ProfileActivity
import com.example.birellerapp.authentication.UserProfile
import com.example.birellerapp.databinding.ActivitySettingsBinding
import com.example.birellerapp.openstreetmaps.MappingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask


class SettingsActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarSettings.toolbar)

        val username = binding.navView.getHeaderView(0).findViewById<TextView>(R.id.tvDisplayUsername)
        val editButton = binding.navView.getHeaderView(0).findViewById<Button>(R.id.btnEditProfile)
        val imageProfile = binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.profileImageView)
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId!=null) {
               val user = auth.currentUser?.email
                if (user != null) {
                    // Use the username
                    username.text = user
                } else {
                    // Username is not set
                    Toast.makeText(this, "Can't receive username", Toast.LENGTH_SHORT).show()
                }
            } else {
                // No user is signed in
                Toast.makeText(this, "username not found", Toast.LENGTH_SHORT).show()
            }


        databaseReference = FirebaseDatabase.getInstance().getReference("profiles").child(userId.toString())

        if (userId != null) {
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userProfile = snapshot.getValue(UserProfile::class.java)
                        if (userProfile?.userImage != null) {
                            Glide.with(this@SettingsActivity)
                                .load(userProfile.userImage)
                                .into(imageProfile)
                        }
                        else
                        {
                            Toast.makeText(this@SettingsActivity, "No Image", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                    Toast.makeText(this@SettingsActivity, "Failed to load profile data", Toast.LENGTH_SHORT).show()
                }
            })
        }

        editButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }


        /*  binding.appBarSettings.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_settings)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setNavigationItemSelectedListener { item ->
            val id = item.itemId

            when (id)
            {

                R.id.nav_about ->
                {
                    var about = Intent(this, AboutUsActivity::class.java)
                    startActivity(about)
                }

                R.id.nav_rate ->
                {
                    var rate = Intent(this, RateUsActivity::class.java)
                    startActivity(rate)
                }
                R.id.nav_home ->
                {
                    var home = Intent(this, HomeActivity::class.java)
                    startActivity(home)
                }

                R.id.nav_delete_account ->
                {
                    var delete = Intent(this, DeleteAccount::class.java)
                    startActivity(delete)
                }
                R.id.nav_logout ->
                {
                    var logout = Intent(this, AuthenticationActivity::class.java)
                    startActivity(logout)
                }

                R.id.nav_switch_theme -> {
                    toggleTheme()

                }

            }
            true
        }



    }

    private fun toggleTheme() {
        val isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        val newMode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_NO // Switch to Light mode
        } else {
            AppCompatDelegate.MODE_NIGHT_YES // Switch to Dark mode
        }

        AppCompatDelegate.setDefaultNightMode(newMode)

        // Save the selected mode to SharedPreferences
        saveThemeMode(newMode)
    }

    private fun saveThemeMode(mode: Int) {
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("theme_mode", mode)
        editor.apply()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_settings)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}