package com.example.labexam4

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.AccessControlContext

class WinStreakDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "winstreak.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "winstreak"
        private const val COLUMN_ID = "id"
        private const val COLUMN_WINSTREAK = "winstreak"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_WINSTREAK INTEGER)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
         val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertWinStreak(winStreak: WinStreak){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WINSTREAK, winStreak.winStreak)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getHigheststreak(): Int{
        var winStreak = 0
        val db = readableDatabase
        val query = "SELECT MAX($COLUMN_WINSTREAK) FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            winStreak = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return winStreak
    }

}