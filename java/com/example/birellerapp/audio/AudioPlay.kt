package com.example.birellerapp.audio

import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.birellerapp.R
import com.google.android.material.chip.Chip
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat

class AudioPlay : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var btnPlay: ImageButton
    private lateinit var forward:ImageButton
    private lateinit var backward:ImageButton
    private lateinit var seek: SeekBar
    private lateinit var chip: Chip //used for speed

    private lateinit var tvFileName: TextView
    private lateinit var recorder: MediaRecorder
    private lateinit var tvTrackProgress: TextView
    private lateinit var tvTrackDuration: TextView
    //handles the seek bars movement
    private lateinit var runnable:Runnable
    private lateinit var handler: Handler
    private var delay=100L
    private var jumpValue=1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_play)
        var filePath=intent.getStringExtra("filepath")
        var fileName=intent.getStringExtra("fileName")

        btnPlay=findViewById(R.id.btnPlay)
        forward=findViewById(R.id.btnForward)
        backward=findViewById(R.id.btnBackward)
        seek=findViewById(R.id.seekBar)
        chip=findViewById(R.id.idChip)
        //  waveFormView=findViewById(R.id.waveFormView)
        tvTrackDuration=findViewById(R.id.tvTrackDuration)
        tvTrackProgress=findViewById(R.id.tvTrackProgress)
        //  bar=findViewById(R.id.idToolBar)
        tvFileName=findViewById(R.id.tvFilename)


        tvFileName.text=fileName
        mediaPlayer= MediaPlayer()

        try {
            mediaPlayer.setDataSource(filePath)
            mediaPlayer.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
            //  Toast.makeText(this, "Failed to connect", Toast.LENGTH_SHORT).show()
        }

        tvTrackDuration.text=dateFormat(mediaPlayer.duration)


        handler=Handler(Looper.getMainLooper()) //delays an event handler
        runnable=Runnable{ //sets progress at the initial position
            seek.progress=mediaPlayer.currentPosition
            tvTrackProgress.text=dateFormat(mediaPlayer.currentPosition)
            handler.postDelayed(runnable,delay) //updates position
        }

        btnPlay.setOnClickListener{
            playPausePlayer()
        }
        playPausePlayer()
        seek.max=mediaPlayer.duration

        mediaPlayer.setOnCompletionListener {
            btnPlay.background=ResourcesCompat.getDrawable(resources,R.drawable.round_play_circle_24,theme)
            handler.removeCallbacks(runnable)
        }

        forward.setOnClickListener {
            mediaPlayer.seekTo(mediaPlayer.currentPosition+jumpValue)
            seek.progress+=jumpValue
        }

        backward.setOnClickListener {
            mediaPlayer.seekTo(mediaPlayer.currentPosition-jumpValue)
            seek.progress-=jumpValue
        }

        chip.setOnClickListener {
            // Adjusting playback speed
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val playbackParams = mediaPlayer.playbackParams
                if (playbackParams.speed < 2.0f) {
                    // Increase playback speed by 0.5x
                    playbackParams.speed += 0.5f
                } else {
                    // Reset playback speed to 1.0x
                    playbackParams.speed = 1.0f
                }
                mediaPlayer.playbackParams = playbackParams
                chip.text="${playbackParams.speed}"
            } else {

                //display a message .
            }
        }

        seek.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2)
                {
                    mediaPlayer.seekTo(p1)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

    }//onCreate

    private fun playPausePlayer(){
        if (  !mediaPlayer.isPlaying)
        {
            mediaPlayer.start()
            btnPlay.background=ResourcesCompat.getDrawable(resources,R.drawable.ic_pause,theme)
            handler.postDelayed(runnable,delay)
        }
        else{
            mediaPlayer.pause()
            btnPlay.background= ResourcesCompat.getDrawable(resources,R.drawable.round_play_circle_24,theme)
            handler.removeCallbacks(runnable)
        }
    }

    private fun dateFormat(duration:Int):String{
        var d = duration/1000
        var s =d%60
        var m =(d/60 % 60)
        var h=(d-m*60/360).toInt()

        var f: NumberFormat = DecimalFormat("00")
        var str="$m:${f.format(s)}"

        if (h>0)
        {
            str="$h:$str"
        }

        return  str
    }


}