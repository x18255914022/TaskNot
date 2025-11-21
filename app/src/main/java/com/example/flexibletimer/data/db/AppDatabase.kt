package com.example.flexibletimer.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flexibletimer.data.model.Alert
import com.example.flexibletimer.data.model.Timer

@Database(entities = [Timer::class, Alert::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun timerDao(): TimerDao
    abstract fun alertDao(): AlertDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flexible_timer_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
