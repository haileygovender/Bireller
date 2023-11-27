package com.example.birellerapp.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.room.Database
import com.example.birellerapp.MainActivity
import com.example.birellerapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DeleteAccount : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Images")
    private val settingsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("settings").child(userId.toString())
    private val profileRef:DatabaseReference=FirebaseDatabase.getInstance().getReference("profiles").child(userId.toString())

    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var deleteAccountButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

        usernameEditText = findViewById(R.id.tvUsername)
        passwordEditText = findViewById(R.id.tvPassword)
        deleteAccountButton = findViewById(R.id.btnDeleteAccount)

        deleteAccountButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Use Firebase Authentication to delete the user account
            val currentUser = auth.currentUser

            if (currentUser != null) {
                val credential = EmailAuthProvider.getCredential(username, password)

                currentUser.reauthenticate(credential)
                    .addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            currentUser.delete()
                                .addOnCompleteListener { deleteTask ->
                                    if (deleteTask.isSuccessful) {
                                        // Account deleted successfully
                                        removeUserEntries()
                                        deleteFromFirebase()
                                        deleteProfileFromFirebase(userId!!)
                                        deletePointsFromFirebase(userId!!)
                                        Toast.makeText(
                                            this@DeleteAccount,
                                            "Account deleted successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                   startActivity(Intent(this,MainActivity::class.java))
                                    } else {
                                        Toast.makeText(
                                            this@DeleteAccount,
                                            "Failed to delete account. Please try again.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } else {
                            Toast.makeText(
                                this@DeleteAccount,
                                "Reauthentication failed. Please check your credentials.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

    private fun removeUserEntries() {

        // Query to get observations of the current user
        val userQuery: Query = databaseReference.orderByChild("userId").equalTo(userId)

        userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val observationKey = snapshot.key

                    // Delete data from Firebase Realtime Database
                    snapshot.ref.removeValue()

                    // Delete corresponding image from Firebase Storage
                    val imageReference = storageReference.child("$userId/$observationKey.jpg")
                    imageReference.delete()
                        .addOnSuccessListener {
                            // Image deleted successfully
                        }
                        .addOnFailureListener {
                            // Handle failure
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                // You can add any error handling code here if needed
                Toast.makeText(
                    this@DeleteAccount,
                    "Database error: ${databaseError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun deleteFromFirebase() {
        if (userId != null) {
            settingsRef.removeValue()
                .addOnSuccessListener {
                    // Deletion successful
                }
                .addOnFailureListener {
                    // Handle failure
                }
        }
    }

    private fun deleteProfileFromFirebase(userId: String) {
        val imageReference = storageReference.child(userId)

        // Delete profile data from Realtime Database
        profileRef.removeValue()
            .addOnSuccessListener {
                // Profile data deletion successful
              //  Toast.makeText(this, "Profile data deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // Handle failure
             //   Toast.makeText(this, "Failed to delete profile data. Please try again.", Toast.LENGTH_SHORT).show()
            }

        // Delete profile image from Storage
        imageReference.delete()
            .addOnSuccessListener {
                // Profile image deletion successful
            }
            .addOnFailureListener {
                // Handle failure
            }
    }


    private fun deletePointsFromFirebase(userId: String) {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Points").child(userId)

        // Delete points data for the specific user
        databaseReference.removeValue()
            .addOnSuccessListener {
                // Points data deletion successful
              //  Toast.makeText(this, "Points data deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // Handle failure
               // Toast.makeText(this, "Failed to delete points data. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }
}