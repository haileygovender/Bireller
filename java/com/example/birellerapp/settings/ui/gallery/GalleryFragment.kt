package com.example.birellerapp.settings.ui.gallery

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.birellerapp.HomeActivity
import com.example.birellerapp.R
import com.example.birellerapp.databinding.FragmentGalleryBinding
import com.example.birellerapp.openstreetmaps.MappingActivity
import com.example.birellerapp.settings.SettingsClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val userId = mAuth.currentUser?.uid
    private val settingRef = database.getReference("settings").child(userId.toString())

    private lateinit var toggle: ToggleButton
    private lateinit var edtDistance: EditText
    private lateinit var addSettings: Button
    private var isMetricUnits = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

       /* val textView: TextView = binding.textGallery
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        toggle = binding.toggleUnits
        edtDistance = binding.edtDistance
        addSettings = binding.btnSettings


        toggle.setOnCheckedChangeListener { _, isChecked ->
            // Handle the toggle switch between metric and imperial units.
            isMetricUnits = isChecked



        }

        addSettings.setOnClickListener {
            saveToFirebase()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun saveToFirebase() {
        val maxDistanceStr = edtDistance.text.toString()
        if (maxDistanceStr.isNotEmpty()) {
            val maxDistanceKm = maxDistanceStr.toDouble()
            var settings = SettingsClass(userId.toString(), isMetricUnits,(maxDistanceKm/0.621371))
            if (!maxDistanceKm.isNaN()) {
                if (toggle.isChecked)
                {
                     settings = SettingsClass(userId.toString(), isMetricUnits,((maxDistanceKm/0.621371)))
                }
                else{
                     settings = SettingsClass(userId.toString(), isMetricUnits,((maxDistanceKm * 0.621371)))
                }

                if (userId != null) {
                    settingRef.setValue(settings)
                        .addOnSuccessListener {
                            // Profile saved successfully
                            Toast.makeText(requireContext(), "Settings Saved", Toast.LENGTH_SHORT).show()
                            edtDistance.setText("")
                          //  startActivity(Intent(requireContext(), HomeActivity::class.java))
                        }
                        .addOnFailureListener {
                            // Handle failure
                            Toast.makeText(requireContext(), "Failed to save data. Please try again.", Toast.LENGTH_SHORT).show()
                            edtDistance.setText("")
                        }
                }
            } else {
                // Handle invalid input
                Toast.makeText(requireContext(), "Invalid distance input", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Ask the user if they want to enter a max distance.
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Confirmation")
            builder.setMessage("Are you sure you don't want to enter a maximum distance?")
            builder.setPositiveButton("Yes") { dialog, which ->
                // Save the user profile to the Firebase Realtime Database

                var settings = SettingsClass(userId.toString(), isMetricUnits,0.0)
                if (userId != null) {
                    settingRef.setValue(settings)
                        .addOnSuccessListener {
                            // Profile saved successfully
                            Toast.makeText(requireContext(), "Settings Saved", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(requireContext(), HomeActivity::class.java))
                        }
                        .addOnFailureListener {
                            // Handle failure
                            Toast.makeText(requireContext(), "Failed to save data. Please try again.", Toast.LENGTH_SHORT).show()
                            edtDistance.setText("")
                        }
                }
            }

            builder.setNegativeButton("No") { dialog, which ->

            }
            val dialog = builder.create()
            dialog.show()
        }

    }
}