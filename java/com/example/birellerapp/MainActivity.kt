package com.example.birellerapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate
import com.example.birellerapp.authentication.AuthenticationActivity

class MainActivity : AppCompatActivity() {
    //variables
    private val SPLASH_TIME:Long=3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the saved theme mode from SharedPreferences
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val savedThemeMode = sharedPref.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

// Set the app's theme mode
        AppCompatDelegate.setDefaultNightMode(savedThemeMode)

        Handler().postDelayed({
            startActivity(Intent(this@MainActivity,AuthenticationActivity::class.java))
            finish()
        }
            ,SPLASH_TIME)
    }
    }
