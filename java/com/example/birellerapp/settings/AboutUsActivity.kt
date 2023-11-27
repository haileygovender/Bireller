package com.example.birellerapp.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.birellerapp.R

class AboutUsActivity : AppCompatActivity() {
    private val cardList = mutableListOf<AboutCardItem>() // Define a data model for your cards
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        // Initialize your RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.cardRecyclerView)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = AboutCardAdapter(cardList)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        // Add card items to the list
        cardList.add(AboutCardItem("Bird Hotspots", " Bireller empowers users to become citizen scientists. With the built-in recording feature, you can document your bird sightings effortlessly. The app allows you to record bird calls and capture images, creating a personal database of your birdwatching encounters.", R.drawable.ic_hotspots1))
     cardList.add(AboutCardItem("Personal Logs", "Maintain a detailed log of your birdwatching journeys. Track your favorite sightings, locations, and even the time of day when birds are most active. This feature helps you understand bird behavior and migration patterns.",R.drawable.ic_personal1))
        cardList.add(AboutCardItem("Education and Resources", "Bireller offers a wealth of educational content, including bird profiles and migration guides. Enhance your birdwatching skills and knowledge.", R.drawable.ic_education1))
       cardList.add(AboutCardItem("Extra Features", "Bireller is not only to test your min, but you can play tic tac toe and even add items to your To-do list to keep you on track.",R.drawable.ic_feature1))

        adapter.notifyDataSetChanged() // Notify the adapter that data has changed
    }
}