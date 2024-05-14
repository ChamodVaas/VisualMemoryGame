package com.example.labexam4

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.labexam4.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityGameBinding
    private var gameModel: GameModel? = null
    private lateinit var buttons: Array<View>
    private var wonStreak: Int = 0
    private lateinit var db: WinStreakDatabase
    private var randomPattern = arrayOf<String>()
    private var addElementToPattern: Int = 6
    private var wonStreakGaptoaddElement: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = WinStreakDatabase(this)

        initButtons()

        binding.button16.setOnClickListener {
            startGame()
        }

        GameData.gameModel.observe(this) {
            gameModel = it
            setUI()
        }
    }

    private fun initButtons() {
        buttons = arrayOf(
            binding.button, binding.button1, binding.button2, binding.button3,
            binding.button4, binding.button5, binding.button6, binding.button7,
            binding.button8, binding.button9, binding.button10, binding.button11,
            binding.button12, binding.button13, binding.button14, binding.button15,
            binding.button17, binding.button18, binding.button19, binding.button20
        )

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener(this)
            button.tag = index.toString()
        }
    }

    private fun setUI() {
        gameModel?.apply {
            val filledPos = filledPos
            for (i in 0 until filledPos.size) {
                if (filledPos[i] == "O") {//filter random generated postions of buttons
                    (buttons[i] as androidx.appcompat.widget.AppCompatButton).text = ""
                    buttons[i].setBackgroundColor(Color.GREEN) // Set button background color to green
                } else {
                    buttons[i].setBackgroundColor(Color.BLUE) // Set button background color to blue
                }
            }

            binding.button16.visibility = View.VISIBLE

            binding.textView2.text =
                when (gameStatus) {
                    GameStatus.CREATED -> {
                        binding.button16.visibility = View.INVISIBLE
                        "Game ID : $gameId"
                    }
                    GameStatus.JOINED -> {
                        "Click on start game"
                    }
                    GameStatus.INPROGRESS -> {
                        binding.button16.visibility = View.INVISIBLE
                        "Click on the Green buttons"
                    }
                    GameStatus.FINISHED -> {
                        if (winner.isNotEmpty()) {
                            wonStreak++
                            wonStreakGaptoaddElement++
                            if(wonStreakGaptoaddElement > 2){
                                addElementToPattern++//when winstreak hit 3 increase number of positions that generate randomly
                                wonStreakGaptoaddElement = 0
                            }
                            "You Won!"
                        } else {
                            "You Failed!"
                        }
                    }
                }
            binding.textView3.text = "Your Win Streak is ${wonStreak}"
        }
    }


    private fun startGame() {
        randomPattern = generateRandomPattern()
        gameModel?.apply {
            updateGameData(
                GameModel(
                    gameId = gameId,
                    filledPos = randomPattern.toMutableList(),
                    gameStatus = GameStatus.INPROGRESS
                )
            )
            buttons.forEachIndexed { index, button ->
                button.isEnabled = false // Disable button
                if (randomPattern[index] == "O") {//filter random generated postions of buttons
                    (button as androidx.appcompat.widget.AppCompatButton).text = ""
                    button.setBackgroundColor(Color.GREEN) // Set button background color to green
                } else {
                    button.setBackgroundColor(Color.BLUE) // Set button background color to blue
                }
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({//limit time of green buttons appear in display to 1 second
            gameModel?.apply {
                buttons.forEachIndexed { index, button ->
                    if (randomPattern[index] == "O") {//filter random generated postions of buttons
                        (button as androidx.appcompat.widget.AppCompatButton).text = ""
                        button.setBackgroundColor(Color.BLUE) // Set all button background color to blue
                    }
                    button.isEnabled = true // Enable button after 1 seconds
                    button.setOnClickListener(this@GameActivity)
                }
            }
        }, 1000) // 1 seconds delay
    }


    private fun updateGameData(model: GameModel) {
        GameData.saveGameModel(model)
    }

    private fun onClickOButton(clickedPos: Int) {
        gameModel?.apply {
            if (filledPos[clickedPos] == "O") {//check clicked buttons positions and generated positions are equal
                filledPos[clickedPos] = "X"
                buttons[clickedPos].isEnabled = false//make clicked buttons to disable
                var count = 0
                for (pos in filledPos) {
                    if (pos == "X") {
                        count++//count clicked buttons positions that are include in random generated position
                    }
                }
                if (count == addElementToPattern) {
                    finishGame("won")//finish round when random generated positions and clicked buttons positions equal
                }
                (buttons[clickedPos] as androidx.appcompat.widget.AppCompatButton).setBackgroundColor(Color.GREEN) // Set clicked button background color to green
            } else {
                finishGame("failed")//finish round when player choose wrong button position that is not include in random generated positions
                (buttons[clickedPos] as androidx.appcompat.widget.AppCompatButton).setBackgroundColor(Color.RED) // Set clicked button background color to red
            }
        }
    }


    override fun onClick(v: View?) {//Display error massage when player clicked buttons before start game
        gameModel?.apply {
            if (gameStatus != GameStatus.INPROGRESS) {
                Toast.makeText(applicationContext, "Game not started", Toast.LENGTH_SHORT).show()
                return
            }
            val clickedPos = (v?.tag as String).toInt()
            onClickOButton(clickedPos)
        }
    }

    private fun finishGame(result: String) {
        gameModel?.apply {
            buttons.forEach { button ->
                (button as androidx.appcompat.widget.AppCompatButton).text = ""
                button.isEnabled = false // Disable button
            }
            updateGameData(
                GameModel(
                    gameId = gameId,
                    filledPos = filledPos,
                    gameStatus = GameStatus.FINISHED,
                    winner = if (result == "won") "You" else ""
                )
            )
            if (result == "failed") {
                // Save won streak to the database
                saveWonStreak(wonStreak)
                wonStreak = 0
            }
            Toast.makeText(applicationContext, "Game $result", Toast.LENGTH_SHORT).show()//display game result
        }
    }

    private fun saveWonStreak(streak: Int) {// Save won streak to the database
        val winStreak = WinStreak(0, streak)
        db.insertWinStreak(winStreak)
        finish()
        Toast.makeText(this, "Winstreak Saved", Toast.LENGTH_SHORT).show()//display successful message
    }

    private fun generateRandomPattern(): Array<String> {//generate random pattern
        val pattern = Array(buttons.size) { "" }
        val random = java.util.Random()
        val indexes = mutableSetOf<Int>()
        while (indexes.size < addElementToPattern) {
            indexes.add(random.nextInt(buttons.size))
        }
        for (index in indexes) {
            //assign O to random generated positions in array to use filter positions in above code
            pattern[index] = "O"
        }
        return pattern
    }
}
