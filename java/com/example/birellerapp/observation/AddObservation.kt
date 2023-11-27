package com.example.birellerapp.observation

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.example.birellerapp.R
import com.example.birellerapp.openstreetmaps.MappingActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*

class AddObservation : AppCompatActivity() {
    //variables
    private lateinit var uploadButton: FloatingActionButton
    private lateinit var uploadImage: ImageView
    private lateinit var uploadCaption: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var mAuth: FirebaseAuth
    private var imageUri: Uri? = null
    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Images")
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference


    // Variables for notification
    private val CHANNEL_ID = "channelId"
    private val CHANNEL_NAME = "channelName"
    private val NOTIFICATION_ID = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_observation)
        uploadButton = findViewById(R.id.uploadButton)
        uploadCaption = findViewById(R.id.uploadCaption)
        uploadImage = findViewById(R.id.uploadImage)
        mAuth = FirebaseAuth.getInstance()

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE

        val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
                uploadImage.setImageURI(imageUri)
            } else {
                Toast.makeText(this@AddObservation, "No Image Selected", Toast.LENGTH_SHORT).show()
            }
        }

        uploadImage.setOnClickListener {
            val photoPicker = Intent()
            photoPicker.action = Intent.ACTION_GET_CONTENT
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        uploadButton.setOnClickListener {
            if (imageUri != null) {
                uploadToFirebase(imageUri!!)
                Glide.with(this)
                    .load(imageUri)
                    .into(uploadImage)
            } else {
                Toast.makeText(this@AddObservation, "Please select an image", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        createNotificationChannel()
    }

    private fun uploadToFirebase(uri: Uri) {
        val location = intent.getStringExtra("addLocation")
        val latitude = intent.getStringExtra("addLatitude")
        val longitude = intent.getStringExtra("addLongitude")


        val bird = uploadCaption.text.toString()
        val currentDate = getCurrentDate()
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            val imageReference = storageReference.child(userId)

            imageReference.putFile(uri)
                .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                    imageReference.downloadUrl
                        .addOnSuccessListener { downloadUri: Uri ->
                            val dataClass = ObserveDataClass(
                                userId.toString(),
                                downloadUri.toString(),
                                latitude.toString(),
                                longitude.toString(),
                                location.toString(),
                                bird,
                                currentDate
                            )
                            val key: String? = databaseReference.push().key
                            databaseReference.child(key!!).setValue(dataClass)
                            progressBar.visibility = View.INVISIBLE
                            showNotification("Successfully Added", "Added at $currentDate")
                            updatePoints(userId)
                            val intent = Intent(this@AddObservation, ObservationActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                }
                .addOnProgressListener { snapshot: UploadTask.TaskSnapshot ->
                    progressBar.visibility = View.VISIBLE
                }
                .addOnFailureListener { e: Exception ->
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(this@AddObservation, "Failed", Toast.LENGTH_SHORT).show()
                }
        }else {
            Toast.makeText(this@AddObservation, "User ID is null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileExtension(fileUri: Uri): String? {
        val contentResolver: ContentResolver = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri))
    }

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy") // You can change the date format here
        return simpleDateFormat.format(calendar.time)

    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                lightColor = Color.GREEN
                enableLights(true)
            }

            val notificationManager =
                NotificationManagerCompat.from(this)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, content: String) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun updatePoints(userId: String) {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Points")
        databaseReference.child(userId).get().addOnSuccessListener { dataSnapshot ->
            var currentPoints = dataSnapshot.getValue(Int::class.java) ?: 0
            currentPoints += 10 // Add 10 points for the observation
            databaseReference.child(userId).setValue(currentPoints)
        }
    }

}