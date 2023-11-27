package com.example.birellerapp.observation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.birellerapp.R
import com.example.birellerapp.birds.BirdDataClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ObservationDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_observation_details)

        val observation = intent.getParcelableExtra<ObserveDataClass>("observation")


        if (observation != null) {
            val birdImageView = findViewById<ImageView>(R.id.obsvImage)
            val birdNameTextView = findViewById<TextView>(R.id.obsvBirdName)
            val birdLocationTextView = findViewById<TextView>(R.id.obsvLocation)
            val birdLatitudeTextView = findViewById<TextView>(R.id.obsvLatitude)
            val birdLongitudeTextView = findViewById<TextView>(R.id.obsvLongitude)
            val birdDateTextView = findViewById<TextView>(R.id.obsvDate)

            // Use the retrieved ObserveDataClass to populate your views
            // You can use Glide to load the image from the imageURL
            Glide.with(this).load(observation.imageURL).into(birdImageView)
            birdNameTextView.text = observation.birds
            birdLocationTextView.text = "Location: ${observation.location}"
            birdLatitudeTextView.text = "Latitude: ${observation.latitude}"
            birdLongitudeTextView.text = "Longitude: ${observation.longitude}"
            birdDateTextView.text = "Date: ${observation.date}"
        }
    }
}
