package com.example.flexibletimer.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.flexibletimer.FlexibleTimerApplication
import com.example.flexibletimer.R
import com.example.flexibletimer.ui.activities.MainActivity
import java.util.concurrent.TimeUnit

class AlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TimerService.ACTION_SHOW_ALERT) {
            Log.d(TAG, "Alert received!")

            val soundUriString = intent.getStringExtra(TimerService.EXTRA_ALERT_SOUND_URI)
            val offsetMs = intent.getLongExtra(TimerService.EXTRA_ALERT_OFFSET, 0L)
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notificationIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

            val formattedOffset = formatDuration(offsetMs)
            val notificationBuilder = NotificationCompat.Builder(context, FlexibleTimerApplication.TIMER_ALERT_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Timer Alert!")
                .setContentText("Alert at: $formattedOffset")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            if (!soundUriString.isNullOrEmpty()) {
                val customSoundUri = Uri.parse(soundUriString)
                notificationBuilder.setSound(customSoundUri)
            } else {
                notificationBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // For Android O and above, sound is set on the NotificationChannel
                // However, we can still set a custom sound if the channel allows overriding
                // For simplicity here, we rely on the channel default if no custom sound
                // or if it's set as part of the channel creation.
                // If custom sound is set, it will override the channel sound.
                if (!soundUriString.isNullOrEmpty()) {
                    val customSoundUri = Uri.parse(soundUriString)
                    notificationBuilder.setSound(customSoundUri)
                } else {
                    // Use default sound for the channel
                }
            } else {
                 if (!soundUriString.isNullOrEmpty()) {
                    val customSoundUri = Uri.parse(soundUriString)
                    notificationBuilder.setSound(customSoundUri)
                } else {
                    notificationBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND)
                }
            }

            // Use a unique ID for each alert notification
            notificationManager.notify(offsetMs.toInt(), notificationBuilder.build())
        }
    }

    private fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    companion object {
        private const val TAG = "AlertReceiver"
    }
}
