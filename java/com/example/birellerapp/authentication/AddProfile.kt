package com.example.birellerapp.authentication

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.birellerapp.HomeActivity
import com.example.birellerapp.R
import com.example.birellerapp.openstreetmaps.MappingActivity
import com.example.birellerapp.settings.SettingsClass
import com.example.birellerapp.viewpagers.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class AddProfile : AppCompatActivity() {
    //variables
    private lateinit var profileImage: ImageButton
    private lateinit var fullName: EditText
    private lateinit var emailAddress: EditText
    private lateinit var cellNumber: EditText
    private lateinit var dob: EditText
    private lateinit var addProfile:Button


    // Initialize Firebase Database
    private lateinit var mAuth: FirebaseAuth
    private var imageUri: Uri? = null
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    val database = FirebaseDatabase.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val profileRef = database.getReference("profiles")
    private val settingRef = database.getReference("settings").child(userId.toString())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_profile)

        profileImage = findViewById(R.id.imgBtnProfileImage)
        fullName = findViewById(R.id.etFullName)
        emailAddress = findViewById(R.id.etUserEmail)
        cellNumber = findViewById(R.id.etUserNumber)
        dob = findViewById(R.id.etDateOfBirth)
        addProfile=findViewById(R.id.btnRegisterProfile)

        mAuth = FirebaseAuth.getInstance()

        val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
                profileImage.setImageURI(imageUri)
            } else {
                Toast.makeText(this@AddProfile, "No Image Selected", Toast.LENGTH_SHORT).show()
            }
        }

        profileImage.setOnClickListener {
            val photoPicker = Intent()
            photoPicker.action = Intent.ACTION_GET_CONTENT
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        addProfile.setOnClickListener {
            val name = fullName.text.toString()
            val email = emailAddress.text.toString()
            val phoneNumber = cellNumber.text.toString()
            val dateOfBirth = dob.text.toString()

            if (!isValidPhoneNumber(phoneNumber)) {
                // Display an error message for an invalid phone number
                Toast.makeText(this@AddProfile, "Invalid phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                // Display an error message for an invalid email
                Toast.makeText(this@AddProfile, "Invalid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidDateOfBirth(dateOfBirth)) {
                // Display an error message for an invalid date of birth
                Toast.makeText(this@AddProfile, "Invalid date of birth", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveUserProfile()
            saveToFirebase()
        }


    }


    // Inside your AddProfile activity or fragment
    private fun saveUserProfile() {
        // Get the current user's UID (you need to have user authentication set up)

        val name = fullName.text.toString()
        val email = emailAddress.text.toString()
        val phoneNumber = cellNumber.text.toString()
        val dateOfBirth = dob.text
        val imageReference = storageReference.child(userId.toString())


        // Save the user profile to the Firebase Realtime Database
        if (userId != null && imageUri != null) {
            imageReference.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                    imageReference.downloadUrl
                        .addOnSuccessListener { downloadUri: Uri ->
                            val userProfile = UserProfile(userId,downloadUri.toString(),name,email,phoneNumber,dateOfBirth.toString())
                            profileRef.child(userId).setValue(userProfile)
                                .addOnSuccessListener {
                                    // Profile saved successfully
                                    Toast.makeText(
                                        this,
                                        "Profile saved successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    startActivity(Intent(this, ViewPager::class.java))
                                }
                                .addOnFailureListener {
                                    // Handle failure
                                    Toast.makeText(
                                        this,
                                        "Failed to save profile. Please try again.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                }
        }
    }

    private fun getFileExtension(fileUri: Uri): String? {
        val contentResolver: ContentResolver = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri))
    }
    private fun saveToFirebase() {


                if (userId != null) {
                    val settings = SettingsClass(userId.toString(), true,0.0)
                    settingRef.setValue(settings)
                        .addOnSuccessListener {
                            // Profile saved successfully

                        }
                        .addOnFailureListener {
                            // Handle failure

                        }
                }
            }


    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        // valid phone number has 10 digits
        val phoneRegex = Regex("^\\d{10}$")
        return phoneRegex.matches(phoneNumber)
    }

    private fun isValidEmail(email: String): Boolean {
        // Using a simple email validation regex
        val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
        return emailRegex.matches(email)
    }

    private fun isValidDateOfBirth(dateOfBirth: String): Boolean {
        // format MM-DD-YYYY
        val dobRegex = Regex("^\\d{2}-\\d{2}-\\d{4}$")
        return dobRegex.matches(dateOfBirth)
    }

}