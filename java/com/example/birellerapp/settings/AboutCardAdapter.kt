package com.example.birellerapp.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.birellerapp.R

class AboutCardAdapter (private val cardList: List<AboutCardItem>) :
    RecyclerView.Adapter<AboutCardAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentCard = cardList[position]

        holder.itemView.findViewById<TextView>(R.id.titleTextView).text = currentCard.title
        holder.itemView.findViewById<TextView>(R.id.descriptionTextView).text = currentCard.description
        holder.itemView.findViewById<ImageView>(R.id.iconImg).setImageResource(currentCard.iconResId)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }
}
