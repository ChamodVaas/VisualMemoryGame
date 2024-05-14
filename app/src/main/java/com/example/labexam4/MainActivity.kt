package com.example.labexam4

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.labexam4.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: WinStreakDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = WinStreakDatabase(this)

        val highestStreak = db.getHigheststreak()
        //set winstreakview to higheststreak that read from database
        binding.winstreakview.text = highestStreak.toString()

        binding.PlayBtn.setOnClickListener {
            createGame()
        }
    }

    //update higheststreak when it change
    override fun onResume() {
        super.onResume()
        val highestStreak = db.getHigheststreak()
        binding.winstreakview.text = highestStreak.toString()
    }

    private fun createGame() {
        GameData.saveGameModel(
            GameModel(
                gameStatus = GameStatus.JOINED
            )
        )
        startGame()
    }

    private fun startGame() {
        startActivity(Intent(this, GameActivity::class.java))
    }
}
