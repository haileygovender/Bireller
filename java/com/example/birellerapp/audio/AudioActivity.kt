package com.example.birellerapp.audio

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.example.birellerapp.R
import com.example.birellerapp.WaveFormView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import java.util.Date as Date

val REQUEST_CODE=200

class AudioActivity : AppCompatActivity(),AudioDataClass.OnTimerTickListener {

    //variables
    private lateinit var amplitudes: ArrayList<Float>
    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var isGranted = false
    private lateinit var recorder: MediaRecorder
    private var dirPath = ""
    private var fileName = ""
    private lateinit var btnRecord: ImageButton
    private var isRecording = false
    private var isPaused = false
    private lateinit var timer: AudioDataClass
    private lateinit var tvTimer: TextView
    private lateinit var vibrator: Vibrator // when a user clicks record or play
    private lateinit var btnList: ImageButton
    private lateinit var btnDelete: ImageButton
    private lateinit var btnDone: ImageButton
    private lateinit var waveFormView: WaveFormView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var fileNameInput: TextView
    private lateinit var bottomSheet: LinearLayout
    private lateinit var db: AppDataClass
    private var duration = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)

        isGranted = ActivityCompat.checkSelfPermission(
            this,
            permissions[0]
        ) == PackageManager.PERMISSION_GRANTED

        if (!isGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
        }
        db = Room.databaseBuilder(this, AppDataClass::class.java, "audioRecords")
            .build()
        bottomSheet = findViewById(R.id.bottomSheet)
        timer = AudioDataClass(this) //calls listener
        btnRecord = findViewById(R.id.btnRecord)
        btnDelete = findViewById(R.id.btnDelete)
        btnList = findViewById(R.id.btnList)
        btnDone = findViewById(R.id.btnDone)
        tvTimer = findViewById(R.id.tvTimer)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = 0 //max size when collapsed
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        waveFormView = findViewById(R.id.waveFormView)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        btnRecord.setOnClickListener {
            when {
                isPaused -> resumeRecording()
                isRecording -> pauseRecording()
                else -> startRecording()
            }
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }

        btnDone.setOnClickListener {
            stopRecording()
            Toast.makeText(this, "Record saved", Toast.LENGTH_SHORT).show()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheet.visibility = View.VISIBLE
            fileNameInput = findViewById(R.id.fileNameInput)
            fileNameInput.setText(fileName)
        }

        btnDelete.setOnClickListener {
            stopRecording()
            File("$dirPath$fileName.mp3")
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
        }

        btnList.setOnClickListener {

            startActivity(Intent(this, AudioList::class.java))
        }
        var bottomSheetBG = findViewById<View>(R.id.bottomsheetBg)

        bottomSheetBG.setOnClickListener {
            File("$dirPath$fileName.mp3").delete()
            dismiss()
        }
        var initialTouchY = 0.0 // Declare initialTouchY

        bottomSheetBG.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                // Record the initial touch position
                initialTouchY = motionEvent.y.toDouble()
            } else if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                val currentY = motionEvent.y

                if (currentY - initialTouchY > 0) {
                    // User is sliding down, so hide the BottomSheet
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    return@setOnTouchListener true
                }
            }
            false
        }


        var btnCancel = findViewById<Button>(R.id.btnCancel)

        btnCancel.setOnClickListener {
            File("$dirPath$fileName.mp3").delete()
            dismiss()
        }
        var btnOk = findViewById<Button>(R.id.btnOk)
        btnOk.setOnClickListener {
            dismiss()
            save()
        }



        btnDelete.isClickable = false
    }//onCreate



    private fun save() {
        val newFileName: String = fileNameInput.text.toString()
        if (newFileName != fileName) {
            val newFile = File("$dirPath$newFileName.mp3")
            File("$dirPath$fileName.mp3").renameTo(newFile) //creates new file
        }

        var filePath = "$dirPath$newFileName.mp3"
        var timestamp: Long = Date().time
        var ampsPath = "$dirPath$newFileName"

        try {
            var fos = FileOutputStream(ampsPath)
            var out = ObjectOutputStream(fos)
            out.writeObject(amplitudes)
            fos.close()
            out.close()
        } catch (e: IOException) {
        }

        var record = RecordAudio(newFileName, filePath, timestamp, duration, ampsPath)

        GlobalScope.launch {
            db.audioRecordDao().insert(record)
        }

    }

    private fun dismiss() {
        bottomSheet.visibility = View.GONE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        hideKeyBoard(fileNameInput)

        Handler(Looper.getMainLooper()).postDelayed(
            {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }, 100
        ) //delays keyboard
    }

    private fun hideKeyBoard(view: View) {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun pauseRecording() {
        recorder.pause()
        isPaused = true
        btnRecord.setImageResource(R.drawable.round_play_circle_24)
        timer.pause()
    }

    private fun resumeRecording() {
        recorder.resume()
        isPaused = false
        btnRecord.setImageResource(R.drawable.ic_pause )
        timer.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE)
            isGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    private fun startRecording() {
        if (!isGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
            return
        }

        recorder = MediaRecorder()
        dirPath = "${externalCacheDir?.absolutePath}/"
        var simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")
        var date = simpleDateFormat.format(Date())
        fileName = "audio_record $date"
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$fileName.mp3")

            try {
                prepare()
            } catch (e: IOException) {
            }
            start()
        }
        btnRecord.setImageResource(R.drawable.ic_pause)
        isRecording = true
        isPaused = false
        timer.start()

        btnDelete.isClickable = true
        btnDelete.setImageResource(R.drawable.ic_delete)
        btnList.visibility = View.GONE
        btnDone.visibility = View.VISIBLE
    }

    private fun stopRecording() {
        timer.stop()

        try {
            recorder.stop()
        } catch (e: Exception) {
            // Handle the exception, log it, or show an error message
        }

        try {
            recorder.release()
        } catch (e: Exception) {
            // Handle the exception, log it, or show an error message
        }


        isPaused = false
        isRecording = false

        btnList.visibility = View.VISIBLE
        btnDone.visibility = View.GONE

        btnDelete.isClickable = false
        btnDelete.setImageResource(R.drawable.ic_delete) //disabaled

        btnRecord.setImageResource(R.drawable.baseline_fiber_manual_record_24)

        tvTimer.text = "00:00.00"
        amplitudes = waveFormView.clear()
    }


    override fun onTimerTick(duration: String) {
        tvTimer.text = duration
        this.duration = duration.dropLast(3)
        waveFormView.addAmplitude(recorder.maxAmplitude.toFloat())
    }
}



