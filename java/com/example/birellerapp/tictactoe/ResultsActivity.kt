package com.example.birellerapp.tictactoe

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.birellerapp.R

class ResultsActivity : AppCompatActivity() {
    class ResultDialog(
        context: Context,
        private val message: String,
        private val gameActivity: TicTacToeActivity
    ) : Dialog(context) {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_results)
            val messageText = findViewById<TextView>(R.id.messageText)
            val startAgainButton = findViewById<Button>(R.id.startAgainButton)

            messageText.text = message

            startAgainButton.setOnClickListener {
                gameActivity.restartMatch()
                dismiss()
            }
        }
    }
}