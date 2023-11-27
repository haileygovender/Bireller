package com.example.birellerapp.audio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.birellerapp.R
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AudioList : AppCompatActivity() , ItemClickListener {

        private lateinit var records:ArrayList<RecordAudio>
        private lateinit var mAdapter: AudioAdapter
        private lateinit var db : AppDataClass
        private lateinit var recyclerView: RecyclerView
        private  lateinit var searchInput: TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_list)
        searchInput=findViewById(R.id.searchItem)
        records=ArrayList()
        //initalise the adapter and list of data
        mAdapter= AudioAdapter(records,this)
        db= Room.databaseBuilder(this,AppDataClass::class.java,"audioRecords").build()
        recyclerView=findViewById(R.id.recyclerView)

        recyclerView.apply {
            adapter=mAdapter
            layoutManager= LinearLayoutManager(context)
        }

        fetchAll()

        searchInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                //change happens
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                //how many characters change

                var query =p0.toString()
                searchDatabase(query)
            }

            override fun afterTextChanged(p0: Editable?) {

                //text has been changed
            }

        })
    }

    private fun   searchDatabase(query:String){
        GlobalScope.launch {
            records.clear() //clear records
            var queryRecords=db.audioRecordDao().searchDatabase("%$query%") // searches according to characters, not looking for the exact record
            records.addAll(queryRecords)
            runOnUiThread{
                mAdapter.notifyDataSetChanged()
            }

        }
    }
    private fun fetchAll(){
        GlobalScope.launch {
            records.clear()
            var queryRecords=db.audioRecordDao().getAll()
            records.addAll(queryRecords)

            mAdapter.notifyDataSetChanged()
        }
    }


    override fun onItemClickListener(position: Int) {
        var audioRecord= records[position]

        var intent= Intent(this,AudioPlay::class.java)

        intent.putExtra("filepath",audioRecord.filePath)
        intent.putExtra("fileName",audioRecord.fileName)
        startActivity(intent)
    }

    override fun onItemLongClickListener(position: Int) {
        Toast.makeText(this, "Long click", Toast.LENGTH_SHORT).show()
    }

}