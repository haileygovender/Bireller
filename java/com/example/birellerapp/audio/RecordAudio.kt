package com.example.birellerapp.audio

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName= "audioRecords")
data class RecordAudio(

    val fileName : String,
    val filePath : String,
    val timeStamp : Long,
    val duration : String,
    val ampsPath : String
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @Ignore
    var isChecked = false

}