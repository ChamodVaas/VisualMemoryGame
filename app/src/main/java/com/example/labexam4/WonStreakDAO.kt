package com.example.labexam4
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WonStreakDao {
    @Query("SELECT * FROM won_streak")
    fun getWonStreak(): LiveData<List<Int>>
}