package com.example.birellerapp.authentication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.example.birellerapp.HomeActivity
import com.example.birellerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class RegisterFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.register_fragment, container, false)


        val signupButton = view.findViewById<Button>(R.id.signup_button)
        val emailEditText = view.findViewById<EditText>(R.id.signup_email)
        val passwordEditText = view.findViewById<EditText>(R.id.signup_password)
        val confirmPasswordEditText = view.findViewById<EditText>(R.id.signup_password)
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

        signupButton.setOnClickListener {
            // Retrieve user input and validate it

            val email = emailEditText.text.toString().trim()
            val passwordReg = passwordEditText.text.toString().trim()
            val passwordConfirm = confirmPasswordEditText.text.toString().trim()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(passwordReg) || TextUtils.isEmpty(passwordConfirm)) {
                Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordReg != passwordConfirm) {
                Toast.makeText(requireContext(), "Passwords don't match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth.createUserWithEmailAndPassword(email, passwordReg)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Registration Successful!",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intentTwo = Intent(requireContext(), AddProfile::class.java)
                        startActivity(intentTwo)
                    } else {
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            Toast.makeText(
                                requireContext(),
                                "Email address is already in use.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Registration unsuccessful",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        emailEditText.setText("")
                        passwordEditText.setText("")
                        confirmPasswordEditText.setText("")
                        emailEditText.requestFocus()
                    }
                }
        }

        return view
    }
}
