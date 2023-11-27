package com.example.birellerapp.authentication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.*
import com.bumptech.glide.Glide
import com.example.birellerapp.HomeActivity
import com.example.birellerapp.R
import com.example.birellerapp.observation.ObserveDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_IMAGE = 1
    }

    private lateinit var btnUpdateProfile: Button
    private lateinit var btnUpdateProfileImage: ImageButton
    private lateinit var edtUpdateFullName: EditText
    private lateinit var edtUpdateEmail: EditText
    private lateinit var edtUpdateNumber: EditText
    private lateinit var tvDob: EditText
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private lateinit var databaseReference: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        edtUpdateFullName = findViewById(R.id.edtUpdateFullName)
        edtUpdateEmail = findViewById(R.id.edtUpdateEmail)
        edtUpdateNumber = findViewById(R.id.edtUpdateNumber)
        btnUpdateProfileImage = findViewById(R.id.btnUpdateProfileImage)
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile)
        tvDob = findViewById(R.id.tvDob)
        mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser?.uid

        btnUpdateProfileImage.setOnClickListener {
            selectImageFromGallery()
        }

        databaseReference =
            FirebaseDatabase.getInstance().getReference("profiles").child(userId.toString())

        if (userId != null) {
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userProfile = snapshot.getValue(UserProfile::class.java)
                        if (userProfile != null) {
                            edtUpdateFullName.setText(userProfile.name)
                            edtUpdateEmail.setText(userProfile.email)
                            edtUpdateNumber.setText(userProfile.phoneNumber)
                            tvDob.setText(userProfile.birthDate)
                            // Load and display the image from the URL using Glide
                            Glide.with(this@ProfileActivity)
                                .load(userProfile.userImage)
                                .into(btnUpdateProfileImage)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                    Toast.makeText(
                        this@ProfileActivity,
                        "Failed to load profile data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        btnUpdateProfile.setOnClickListener {
            val name = edtUpdateFullName.text.toString()
            val email = edtUpdateEmail.text.toString()
            val phoneNumber = edtUpdateNumber.text.toString()
            val dateOfBirth = tvDob.text.toString()

            if (!isValidPhoneNumber(phoneNumber)) {
                // Display an error message for an invalid phone number
                Toast.makeText(this@ProfileActivity, "Invalid phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                // Display an error message for an invalid email
                Toast.makeText(this@ProfileActivity, "Invalid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidDateOfBirth(dateOfBirth)) {
                // Display an error message for an invalid date of birth
                Toast.makeText(this@ProfileActivity, "Invalid date of birth", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveUpdatedProfile()
        }

        val tvBirdsCount = findViewById<TextView>(R.id.tvBirdsCount)
        var count = 0
        if (userId != null) {
            val observationsReference = FirebaseDatabase.getInstance().getReference("Images")
            observationsReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            val data = item.getValue(ObserveDataClass::class.java)
                            if (data != null && data.id == userId) {
                                count++
                            }
                        }
                        tvBirdsCount.text = count.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                    Toast.makeText(this@ProfileActivity, "Failed to load profile data", Toast.LENGTH_SHORT).show()
                }
            })
        }


        val tvRank = findViewById<TextView>(R.id.tvRank)
        val tvPoints = findViewById<TextView>(R.id.tvPointsCount)
        var userPoints=0
        var   userLevel=""
        // Fetch and display the user's points
        val pointsReference = FirebaseDatabase.getInstance().getReference("Points")
        if (userId != null) {
            pointsReference.child(userId).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                     userPoints = dataSnapshot.getValue(Int::class.java) ?: 0
                    // Determine user's level based on points and set the rank
                     userLevel = determineUserLevel(userPoints)
                    tvRank.text = userLevel
                    tvPoints.text = userPoints.toString()
                }
                else
                {
                    tvRank.text = "-"
                    tvPoints.text = "0"
                }
            }
        }
    } //on create

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Glide.with(this@ProfileActivity)
                .load(imageUri)
                .into(btnUpdateProfileImage)
        }
    }

    private fun saveUpdatedProfile() {
        val userId = mAuth.currentUser?.uid
        val updatedName = edtUpdateFullName.text.toString()
        val updatedEmail = edtUpdateEmail.text.toString()
        val updatedPhoneNumber = edtUpdateNumber.text.toString()
        val updatedDob = tvDob.text.toString()

        if (userId != null) {
            if (imageUri != null) {
                val imageReference = storageReference.child("$userId/profileImage.jpg")
                imageReference.putFile(imageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        imageReference.downloadUrl
                            .addOnSuccessListener { downloadUri ->
                                val updatedUserProfile = UserProfile(
                                    userId,
                                    downloadUri.toString(),
                                    updatedName,
                                    updatedEmail,
                                    updatedPhoneNumber,
                                    updatedDob
                                )

                                databaseReference.setValue(updatedUserProfile)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this@ProfileActivity,
                                            "Profile updated successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                     /*   startActivity(
                                            Intent(
                                                this@ProfileActivity,
                                                HomeActivity::class.java
                                            )
                                        )*/
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            this@ProfileActivity,
                                            "Failed to update profile. Please try again.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                    }
            } else {
                // If no new image is selected, preserve the existing image URL
                databaseReference.child("userImage")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val currentImageUrl =
                                    snapshot.value.toString()//keeps the existing image
                                val updatedUserProfile = UserProfile(
                                    userId,
                                    currentImageUrl,  // Use the existing image URL
                                    updatedName,
                                    updatedEmail,
                                    updatedPhoneNumber,
                                    updatedDob
                                )

                                databaseReference.setValue(updatedUserProfile)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this@ProfileActivity,
                                            "Profile updated successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(
                                            Intent(
                                                this@ProfileActivity,
                                                HomeActivity::class.java
                                            )
                                        )
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            this@ProfileActivity,
                                            "Failed to update profile. Please try again.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle the error
                            Toast.makeText(
                                this@ProfileActivity,
                                "Failed to load the existing image URL",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }
    }

    private fun determineUserLevel(points: Int): String {
        // Define your logic to determine the user's level based on points
        if (points >= 100) {
            return "Gold"
        } else if (points >= 50) {
            return "Silver"
        } else {
            return "Bronze"
        }
    }



    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        // Assuming a valid phone number has 10 digits
        val phoneRegex = Regex("^\\d{10}$")
        return phoneRegex.matches(phoneNumber)
    }

    private fun isValidEmail(email: String): Boolean {
        // Using a simple email validation regex
        val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
        return emailRegex.matches(email)
    }

    private fun isValidDateOfBirth(dateOfBirth: String): Boolean {
        // Assuming date of birth is in the format MM-DD-YYYY
        val dobRegex = Regex("^\\d{2}-\\d{2}-\\d{4}$")
        return dobRegex.matches(dateOfBirth)
    }
}
