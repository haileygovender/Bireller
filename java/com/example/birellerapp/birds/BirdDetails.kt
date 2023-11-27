package com.example.birellerapp.birds

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.birellerapp.R

class BirdDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bird_details)

        val bird = intent.getParcelableExtra<BirdDataClass>("bird")
        val birdImageView = findViewById<ImageView>(R.id.detailImage)
        val birdNameTextView = findViewById<TextView>(R.id.detailName)
        val birdLocationTextView = findViewById<TextView>(R.id.detailLocation)
        val birdDescriptionTextView = findViewById<TextView>(R.id.detailDescrip)

        if (bird != null) {
            // Load the image using Glide
            Glide.with(this)
                .load(bird.imageUrl)
                .into(birdImageView)

            birdNameTextView.text = bird.name
            birdLocationTextView.text = "Location: " + bird.location
            birdDescriptionTextView.text = "Description: " + bird.description
        }
    }
}