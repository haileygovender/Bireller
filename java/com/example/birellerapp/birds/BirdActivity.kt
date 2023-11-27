package com.example.birellerapp.birds

import BirdAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.example.birellerapp.R
import com.google.firebase.database.*

class BirdActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var birdAdapter: BirdAdapter
    private lateinit var birdList: ArrayList<BirdDataClass>
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bird)

        listView = findViewById(R.id.listview)
        birdList = ArrayList()

        // Initialize Firebase database reference
        databaseRef = FirebaseDatabase.getInstance().reference.child("Birds")

        birdAdapter = BirdAdapter(this, birdList)
        listView.adapter = birdAdapter

        // Attach a ValueEventListener to fetch bird data from Firebase
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                birdList.clear() // Clear the list to avoid duplicates

                for (birdSnapshot in dataSnapshot.children) {
                    val birdData = birdSnapshot.getValue(BirdDataClass::class.java)
                    birdData?.let {
                        birdList.add(it)
                    }
                }

                birdAdapter.notifyDataSetChanged() // Notify the adapter that the data has changed
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error here
            }
        })

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedBird = birdList[position]
            val intent = Intent(this, BirdDetails::class.java)
            intent.putExtra("bird", selectedBird)
            startActivity(intent)
        }
    }
}
