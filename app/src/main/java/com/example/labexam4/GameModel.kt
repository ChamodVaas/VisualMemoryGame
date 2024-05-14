package com.example.labexam4

import kotlin.random.Random

data class GameModel (
    var gameId: String = "-1",
    var filledPos: MutableList<String> = MutableList(20) { "" },
    var winner: String = "",
    var gameStatus: GameStatus = GameStatus.CREATED
)

enum class GameStatus {
    CREATED,
    JOINED,
    INPROGRESS,
    FINISHED
}
