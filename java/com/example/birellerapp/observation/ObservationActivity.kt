package com.example.birellerapp.observation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.birellerapp.R
import com.example.birellerapp.birds.BirdDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ObservationActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAuth: FirebaseAuth
    private var dataList = ArrayList<ObserveDataClass>()
    private lateinit var adapter: ObserveAdapter
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Images")
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_observation)
        mAuth = FirebaseAuth.getInstance()
        recyclerView = findViewById(R.id.rvObservation)
        searchView = findViewById(R.id.idSearchView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager= LinearLayoutManager(this)

        // Initialize the adapter before using it
        adapter = ObserveAdapter(this, dataList) { selectedItem ->
            // Handle the click on the selected item
            val intent = Intent(this, ObservationDetails::class.java)
            intent.putExtra("observation", selectedItem)
            startActivity(intent)

        }
        recyclerView.adapter = adapter // Set the adapter for the RecyclerView

        val userId = mAuth.currentUser?.uid

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter dataList based on the search query
                filterData(newText)
                return true
            }
        })

        if (userId != null) {
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dataList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val dataClass = dataSnapshot.getValue(ObserveDataClass::class.java)
                        if (userId == dataClass?.id) {
                            dataClass?.let { dataList.add(it) }
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                }
            })
        }
    }

    private fun filterData(query: String?) {
        val filteredList = ArrayList<ObserveDataClass>()
        if (query.isNullOrBlank()) {
            // If the query is null or empty, show all data
            filteredList.addAll(dataList)
        } else {
            // Filter data based on the query (search in birds and location fields)
            for (data in dataList) {
                if ((data.birds?.contains(query, true) == true) ||
                    (data.location?.contains(query, true) == true)) {
                    filteredList.add(data)
                }
            }
        }
        adapter.setData(filteredList)
        adapter.notifyDataSetChanged()
    }
}
