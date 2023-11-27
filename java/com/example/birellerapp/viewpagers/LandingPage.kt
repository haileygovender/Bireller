package com.example.birellerapp.viewpagers

import android.content.Intent
import android.location.SettingInjectorService
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.birellerapp.HomeActivity
import com.example.birellerapp.R
import com.example.birellerapp.authentication.AuthenticationActivity
import com.example.birellerapp.settings.SettingsActivity

class LandingPage : AppCompatActivity() {
    //variable
    private lateinit var startButton:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        //typecasting
        startButton=findViewById(R.id.startButton)

        startButton.setOnClickListener {
            startActivity(Intent(this,HomeActivity::class.java))
        }
    }
}