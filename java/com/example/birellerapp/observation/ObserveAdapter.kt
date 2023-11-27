package com.example.birellerapp.observation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.birellerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ObserveAdapter(val context: Context, private val dataList: ArrayList<ObserveDataClass>, private val onItemClick: (ObserveDataClass) -> Unit) :
    RecyclerView.Adapter<ObserveAdapter.MyViewHolder>() {

    private var filterList: ArrayList<ObserveDataClass> = dataList.toMutableList() as ArrayList<ObserveDataClass>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_observe_rv, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(filterList[position].imageURL).into(holder.recyclerImage)
        holder.recyclerLocation.text = filterList[position].location
        holder.recyclerBird.text = filterList[position].birds

        // Set a click listener for the item view
        holder.itemView.setOnClickListener {
            onItemClick(filterList[position])
        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerImage: ImageView = itemView.findViewById(R.id.birdImage)
        val recyclerBird: TextView = itemView.findViewById(R.id.tvBird)
        val recyclerLocation: TextView = itemView.findViewById(R.id.tvLocation)
    }

    fun setData(data: ArrayList<ObserveDataClass>) {
        filterList = data
        notifyDataSetChanged()
    }

    fun filterData(query: String) {
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
        setData(filteredList)
    }
}
