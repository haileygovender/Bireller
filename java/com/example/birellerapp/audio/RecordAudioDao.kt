package com.example.birellerapp.audio

import androidx.room.*

@Dao
interface AudioRecordDao {
    @Query("SELECT * FROM audioRecords")
    fun getAll():List<RecordAudio>

    @Query("SELECT * FROM audioRecords WHERE fileName LIKE :query")
    fun searchDatabase(query:String):List<RecordAudio>

    @Insert
    fun insert(vararg audioRecord: RecordAudio)

    //Part 3
    @Delete
    fun delete(audioRecord: RecordAudio)

    @Delete
    fun delete(audioRecord: Array<RecordAudio>) //delete multiple records

    @Update
    fun update(audioRecord: RecordAudio)

}