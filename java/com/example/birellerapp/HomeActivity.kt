package com.example.birellerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.birellerapp.audio.AudioActivity
import com.example.birellerapp.authentication.AddProfile
import com.example.birellerapp.authentication.AuthenticationActivity
import com.example.birellerapp.authentication.ProfileActivity
import com.example.birellerapp.authentication.UserProfile
import com.example.birellerapp.birds.BirdActivity
import com.example.birellerapp.databinding.ActivityHomeBinding
import com.example.birellerapp.observation.ObservationActivity
import com.example.birellerapp.openstreetmaps.MappingActivity
import com.example.birellerapp.settings.AboutUsActivity
import com.example.birellerapp.settings.DeleteAccount
import com.example.birellerapp.settings.RateUsActivity
import com.example.birellerapp.settings.SettingsActivity
import com.example.birellerapp.tictactoe.AddPlayerActivity
import com.example.birellerapp.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val exploreButton:ImageButton=findViewById(R.id.exploreButton)
        val sightingButton:ImageButton=findViewById(R.id.observationButton)
        val recordingButton:ImageButton=findViewById(R.id.recordsButton)
        val tictacButton:ImageButton=findViewById(R.id.tictacButton)
        val birdButton:ImageButton=findViewById(R.id.birdsButton)


        exploreButton.setOnClickListener {
            startActivity(Intent(this, ExploreActivity::class.java))

        }

        sightingButton.setOnClickListener {
            startActivity(Intent(this, ObservationActivity::class.java))

        }

        recordingButton.setOnClickListener {
            startActivity(Intent(this, AudioActivity::class.java))

        }

        tictacButton.setOnClickListener {
            startActivity(Intent(this, AddPlayerActivity::class.java))

        }

        birdButton.setOnClickListener {
            startActivity(Intent(this, BirdActivity::class.java))

        }




        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setOnItemSelectedListener { item ->
            val id = item.itemId

            when (id)
            {
                R.id.navigation_home ->
                {
                    var home = Intent(this, HomeActivity::class.java)
                    startActivity(home)
                }
                R.id.navigation_dashboard ->
                {
                    var map = Intent(this, MappingActivity::class.java)
                    startActivity(map)
                }

                R.id.navigation_notifications ->
                {
                    var settings =Intent(this, SettingsActivity::class.java)
                    startActivity(settings)
                }

                R.id.navigation_profile ->
                {
                    var profile =Intent(this, ProfileActivity::class.java)
                    startActivity(profile)
                }


            }
            true
        }

    }
}