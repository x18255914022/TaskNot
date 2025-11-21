package com.example.flexibletimer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.flexibletimer.data.model.Timer
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDao {
    @Insert
    suspend fun insert(timer: Timer): Long

    @Update
    suspend fun update(timer: Timer)

    @Delete
    suspend fun delete(timer: Timer)

    @Query("SELECT * FROM timers WHERE id = :id")
    suspend fun getTimerById(id: Long): Timer?

    @Query("SELECT * FROM timers ORDER BY created_at DESC")
    fun getAllTimers(): Flow<List<Timer>>
}
