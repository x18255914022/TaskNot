import android.app.AlarmManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.appwidget.AppWidgetManager
import com.example.flexibletimer.data.repository.TimerRepository
import com.example.flexibletimer.ui.widget.FlexibleTimerWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TimerService : Service() {

    private var countDownTimer: CountDownTimer? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val repository: TimerRepository by lazy {
        (application as FlexibleTimerApplication).repository
    }
    private val alarmManager: AlarmManager by lazy {
        getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }
    private val scheduledAlertPendingIntents = mutableListOf<PendingIntent>()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val timerId = intent.getLongExtra(EXTRA_TIMER_ID, -1L)
                if (timerId != -1L) {
                    serviceScope.launch {
                        val timer = repository.getTimerById(timerId)
                        timer?.let {
                            Log.d(TAG, "Starting timer: ${it.name}")
                            startForegroundService(it.name)
                            startTimer(it.durationMs)

                            FlexibleTimerWidget.sendUpdateBroadcast(this@TimerService, it.name, true, it.id)

                            val alerts = repository.getAlertsForTimerOnce(it.id)
                            alerts.forEach { alert ->
                                val triggerTime = System.currentTimeMillis() + alert.offsetMs
                                val alertIntent = Intent(this@TimerService, AlertReceiver::class.java).apply {
                                    action = ACTION_SHOW_ALERT
                                    putExtra(EXTRA_ALERT_SOUND_URI, alert.soundUri)
                                    putExtra(EXTRA_ALERT_OFFSET, alert.offsetMs)
                                }
                                val pendingIntent = PendingIntent.getBroadcast(
                                    this@TimerService,
                                    alert.id.toInt(), 
                                    alertIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                )
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                                scheduledAlertPendingIntents.add(pendingIntent)
                                Log.d(TAG, "Scheduled alert for ${it.name} at offset ${alert.offsetMs}")
                            }
                        }
                    }
                }
            }
            ACTION_STOP -> {
                Log.d(TAG, "Timer service stopping")
                stopTimer()
            }
        }
        return START_STICKY
    }

    private fun startTimer(durationMs: Long) {
        countDownTimer = object : CountDownTimer(durationMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateNotification(formatDuration(millisUntilFinished))
            }

            override fun onFinish() {
                stopTimer()
            }
        }.start()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        scheduledAlertPendingIntents.forEach { pendingIntent ->
            alarmManager.cancel(pendingIntent)
        }
        scheduledAlertPendingIntents.clear()

        FlexibleTimerWidget.sendUpdateBroadcast(this@TimerService, "No active timer", false, -1L)

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startForegroundService(timerName: String) {
        val notification = buildNotification("Timer \'$timerName\' is running...")
        startForeground(NOTIFICATION_ID, notification)
    }
    
    private fun updateNotification(contentText: String) {
        val notification = buildNotification(contentText)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(contentText: String): android.app.Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val stopIntent = Intent(this, TimerService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, FlexibleTimerApplication.TIMER_SERVICE_CHANNEL_ID)
            .setContentTitle("Flexible Timer")
            .setContentText(contentText)
            .setSmallIcon(R.mipmap.ic_launcher) 
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_media_pause, "Stop", stopPendingIntent)
            .setOnlyAlertOnce(true)
            .build()
    }
    
    private fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        scheduledAlertPendingIntents.forEach { pendingIntent ->
            alarmManager.cancel(pendingIntent)
        }
        scheduledAlertPendingIntents.clear()
        serviceScope.cancel()
        Log.d(TAG, "Timer service destroyed")
    }

    companion object {
        private const val TAG = "TimerService"
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_TIMER_ID = "extra_timer_id"
        const val ACTION_SHOW_ALERT = "ACTION_SHOW_ALERT"
        const val EXTRA_ALERT_SOUND_URI = "extra_alert_sound_uri"
        const val EXTRA_ALERT_OFFSET = "extra_alert_offset"
        private const val NOTIFICATION_ID = 1
    }
}

