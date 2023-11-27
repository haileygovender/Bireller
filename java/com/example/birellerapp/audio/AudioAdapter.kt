package com.example.birellerapp.audio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.birellerapp.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AudioAdapter(var records:ArrayList<RecordAudio>, var listener: ItemClickListener): RecyclerView.Adapter<AudioAdapter.ViewHolder>() {

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view), View.OnClickListener,View.OnLongClickListener {
        var tvFilename: TextView = itemView.findViewById(R.id.tvFileName)
        var tvMeta: TextView = itemView.findViewById(R.id.tvMeta)
        var cbCheck: CheckBox = itemView.findViewById(R.id.cbRecord)

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(p0: View?) {
            //get current position
            //check if the position is real

            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClickListener(position)
            }
        }

        override fun onLongClick(p0: View?): Boolean {
            //get current position
            //check if the position is real

            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemLongClickListener(position)

            }
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_audio_list,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return records.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position!= RecyclerView.NO_POSITION)
        {
            var record:RecordAudio =records[position]

            var dsf= SimpleDateFormat("dd/MM/yyyy")
            var date= Date(record.timeStamp)
            var strDate=dsf.format(date)

            holder.tvFilename.text=record.fileName
            holder.tvMeta.text="${record.duration} ${strDate}"
        }
    }

}