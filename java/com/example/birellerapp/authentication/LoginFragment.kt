import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.birellerapp.HomeActivity
import com.example.birellerapp.R
import com.example.birellerapp.settings.SettingsActivity
import com.example.birellerapp.viewpagers.ViewPager
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var forgotPasswordButton: Button
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.login_fragment, container, false)

        editTextEmail = view.findViewById(R.id.login_email)
        editTextPassword = view.findViewById(R.id.login_password)
        buttonLogin = view.findViewById(R.id.login_button)
        forgotPasswordButton = view.findViewById(R.id.forgot_password_button)
        auth = FirebaseAuth.getInstance()

        // Set a click listener for the "Forgot Password" link
        forgotPasswordButton.setOnClickListener {
            val email = editTextEmail.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(
                    requireContext(),
                    "Please enter your email to reset the password.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Send a password reset email to the user's email address
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        notifyUser("Password reset email sent. Check your email.")
                    } else {
                        notifyUser("Failed to send password reset email. Please check your email address.")
                    }
                }
        }


        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                notifyUser("Please enter both email and password.")
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login successful
                    notifyUser("Authentication successful!")
                    startActivity(Intent(requireContext(), HomeActivity::class.java))
                } else {
                    // Login unsuccessful
                    val exception = task.exception
                    if (exception != null) {
                        val errorMessage = exception.localizedMessage
                        notifyUser("Login failed: $errorMessage")
                    } else {
                        notifyUser("Login failed: An unknown error occurred.")
                    }

                    // Clear the email and password fields
                    editTextEmail.text.clear()
                    editTextPassword.text.clear()

                    // Set focus on the email field
                    editTextEmail.requestFocus()
                }
            }
        }

        return view
    }

    private fun notifyUser(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


}
