package com.example.birellerapp.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.birellerapp.R

class TicTacToeActivity : AppCompatActivity() {
    //variables
    private val combinationList: List<IntArray> = listOf(
        intArrayOf(0, 1, 2),
        intArrayOf(3, 4, 5),
        intArrayOf(6, 7, 8),
        intArrayOf(0, 3, 6),
        intArrayOf(1, 4, 7),
        intArrayOf(2, 5, 8),
        intArrayOf(2, 4, 6),
        intArrayOf(0, 4, 8)
    )
    private var boxPositions: IntArray = IntArray(9) { 0 }
    private var playerTurn = 1
    private var totalSelectedBoxes = 1

    private val imageViews: List<ImageView> by lazy {
        listOf(
            findViewById(R.id.image1),
            findViewById(R.id.image2),
            findViewById(R.id.image3),
            findViewById(R.id.image4),
            findViewById(R.id.image5),
            findViewById(R.id.image6),
            findViewById(R.id.image7),
            findViewById(R.id.image8),
            findViewById(R.id.image9)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tic_tac_toe)
        findViewById<LinearLayout>(R.id.playerOneLayout).setBackgroundResource(R.drawable.black_border)
        findViewById<LinearLayout>(R.id.playerTwoLayout).setBackgroundResource(R.drawable.gradient_box)
        val getPlayerOneName = intent.getStringExtra("playerOne")
        val getPlayerTwoName = intent.getStringExtra("playerTwo")

        findViewById<TextView>(R.id.playerOneName).setText(getPlayerOneName)
        findViewById<TextView>(R.id.playerTwoName).setText(getPlayerTwoName)
        setImageViewListeners()

        imageViews[0].setOnClickListener {
            if (isBoxSelectable(0)) {
                performAction( imageViews[0], 0)
            }
        }
        imageViews[1].setOnClickListener {
            if (isBoxSelectable(1)) {
                performAction( imageViews[1], 1)
            }
        }

        imageViews[2].setOnClickListener {
            if (isBoxSelectable(2)) {
                performAction( imageViews[2], 2)
            }
        }
        imageViews[3].setOnClickListener {
            if (isBoxSelectable(3)) {
                performAction( imageViews[3], 3)
            }
        }
        imageViews[4].setOnClickListener {
            if (isBoxSelectable(4)) {
                performAction( imageViews[4], 4)
            }
        }
        imageViews[5].setOnClickListener {
            if (isBoxSelectable(5)) {
                performAction( imageViews[5], 5)
            }
        }
        imageViews[6].setOnClickListener {
            if (isBoxSelectable(6)) {
                performAction( imageViews[6], 6)
            }
        }
        imageViews[7].setOnClickListener {
            if (isBoxSelectable(7)) {
                performAction( imageViews[7], 7)
            }
        }

    }

    private fun setImageViewListeners() {
        for (i in 0 until 9) {
            val imageView = imageViews[i]
            imageView.setOnClickListener {
                performAction(imageView, i)
            }
        }
    }

    private fun performAction(imageView: ImageView, selectedBoxPosition: Int) {
        boxPositions[selectedBoxPosition] = playerTurn

        val getPlayerOneName = intent.getStringExtra("playerOne")
        val getPlayerTwoName = intent.getStringExtra("playerTwo")
        if (playerTurn == 1) {
            imageView.setImageResource(R.drawable.map_screen)
            if (checkResults()) {
                showResultDialog("${getPlayerOneName} is the Winner!")
            } else if (totalSelectedBoxes == 9) {
                showResultDialog("Match Draw")
            } else {
                changePlayerTurn(2)
                totalSelectedBoxes++
            }
        } else {
            imageView.setImageResource(R.drawable.birds_screen)
            if (checkResults()) {
                showResultDialog("${getPlayerTwoName} is the Winner!")
            } else if (totalSelectedBoxes == 9) {
                showResultDialog("Match Draw")
            } else {
                changePlayerTurn(1)
                totalSelectedBoxes++
            }
        }
    }

    private fun changePlayerTurn(currentPlayerTurn: Int) {
        playerTurn = currentPlayerTurn
        if (playerTurn == 1) {
            findViewById<LinearLayout>(R.id.playerOneLayout).setBackgroundResource(R.drawable.black_border)
            findViewById<LinearLayout>(R.id.playerTwoLayout).setBackgroundResource(R.drawable.gradient_box)
        } else {
            findViewById<LinearLayout>(R.id.playerTwoLayout).setBackgroundResource(R.drawable.black_border)
            findViewById<LinearLayout>(R.id.playerOneLayout).setBackgroundResource(R.drawable.gradient_box)
        }
    }

    private fun checkResults(): Boolean {
        for (combination in combinationList) {
            if (boxPositions[combination[0]] == playerTurn &&
                boxPositions[combination[1]] == playerTurn &&
                boxPositions[combination[2]] == playerTurn
            ) {
                return true
            }
        }
        return false
    }

    private fun showResultDialog(message: String) {
        val resultDialog = ResultsActivity.ResultDialog(this, message, this)
        resultDialog.setCancelable(false)
        resultDialog.show()
    }

    private fun isBoxSelectable(boxPosition: Int): Boolean {
        return boxPositions[boxPosition] == 0
    }

    fun restartMatch() {
        boxPositions = IntArray(9) { 0 }
        playerTurn = 1
        totalSelectedBoxes = 1

        for (imageView in imageViews) {
            imageView.setImageResource(R.drawable.gradient_box)
        }

        findViewById<LinearLayout>(R.id.playerOneLayout).setBackgroundResource(R.drawable.black_border)
        findViewById<LinearLayout>(R.id.playerTwoLayout).setBackgroundResource(R.drawable.gradient_box)
    }
}