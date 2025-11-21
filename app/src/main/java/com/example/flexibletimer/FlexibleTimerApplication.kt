package com.example.flexibletimer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.flexibletimer.data.db.AppDatabase
import com.example.flexibletimer.data.repository.TimerRepository

class FlexibleTimerApplication : Application() {
    
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { TimerRepository(database.timerDao(), database.alertDao()) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel for the foreground service notification
            val serviceChannel = NotificationChannel(
                TIMER_SERVICE_CHANNEL_ID,
                "Timer Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for the running timer notification"
            }

            // Channel for the timer alerts
            val alertChannel = NotificationChannel(
                TIMER_ALERT_CHANNEL_ID,
                "Timer Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for timer alerts and notifications"
            }

            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(serviceChannel)
            notificationManager.createNotificationChannel(alertChannel)
        }
    }

    companion object {
        const val TIMER_SERVICE_CHANNEL_ID = "timer_service_channel"
        const val TIMER_ALERT_CHANNEL_ID = "timer_alert_channel"
    }
}