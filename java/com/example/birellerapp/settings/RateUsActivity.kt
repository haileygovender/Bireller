package com.example.birellerapp.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import com.example.birellerapp.R

class RateUsActivity : AppCompatActivity() {
    private lateinit var ratingBar: RatingBar
    private lateinit var commentEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate_us)

        ratingBar = findViewById(R.id.ratingBar)
        commentEditText = findViewById(R.id.commentEditText)
        submitButton = findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            Toast.makeText(this
                , "Thank you for your response :)", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,SettingsActivity::class.java))
        }
    }
}