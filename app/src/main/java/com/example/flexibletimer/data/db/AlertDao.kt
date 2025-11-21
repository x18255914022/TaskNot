package com.example.flexibletimer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.flexibletimer.data.model.Alert
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Insert
    suspend fun insert(alert: Alert): Long

    @Update
    suspend fun update(alert: Alert)

    @Delete
    suspend fun delete(alert: Alert)

    @Query("SELECT * FROM alerts WHERE timer_id = :timerId ORDER BY offset_ms ASC")
    fun getAlertsForTimer(timerId: Long): Flow<List<Alert>>

    @Query("SELECT * FROM alerts WHERE timer_id = :timerId")
    suspend fun getAlertsForTimerOnce(timerId: Long): List<Alert>
}
