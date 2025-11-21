package com.example.flexibletimer.data.repository

import com.example.flexibletimer.data.db.AlertDao
import com.example.flexibletimer.data.db.TimerDao
import com.example.flexibletimer.data.model.Alert
import com.example.flexibletimer.data.model.Timer
import kotlinx.coroutines.flow.Flow

class TimerRepository(
    private val timerDao: TimerDao,
    private val alertDao: AlertDao
) {

    fun getAllTimers(): Flow<List<Timer>> = timerDao.getAllTimers()

    suspend fun getTimerById(id: Long): Timer? {
        return timerDao.getTimerById(id)
    }

    suspend fun insertTimer(timer: Timer): Long {
        return timerDao.insert(timer)
    }

    suspend fun updateTimer(timer: Timer) {
        timerDao.update(timer)
    }

    suspend fun deleteTimer(timer: Timer) {
        timerDao.delete(timer)
    }

    fun getAlertsForTimer(timerId: Long): Flow<List<Alert>> {
        return alertDao.getAlertsForTimer(timerId)
    }
    
    suspend fun getAlertsForTimerOnce(timerId: Long): List<Alert> {
        return alertDao.getAlertsForTimerOnce(timerId)
    }

    suspend fun insertAlert(alert: Alert) {
        alertDao.insert(alert)
    }

    suspend fun updateAlert(alert: Alert) {
        alertDao.update(alert)
    }

    suspend fun deleteAlert(alert: Alert) {
        alertDao.delete(alert)
    }
}
