package com.example.birellerapp.audio

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RecordAudio::class], version = 1)
abstract class AppDataClass : RoomDatabase(){
    abstract fun audioRecordDao():AudioRecordDao
}