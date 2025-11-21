package com.example.flexibletimer.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.flexibletimer.R
import com.example.flexibletimer.service.TimerService

/**
 * Implementation of App Widget functionality.
 */
class FlexibleTimerWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(
                context,
                appWidgetManager,
                appWidgetId,
                "No active timer",
                false,
                -1L
            )
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (context == null || intent == null) return

        if (intent.action == ACTION_TOGGLE_TIMER) {
            val timerId = intent.getLongExtra(EXTRA_TIMER_CURRENT_ID, -1L)
            val isRunning = intent.getBooleanExtra(EXTRA_TIMER_STATE_RUNNING, false)

            val serviceIntent = Intent(context, TimerService::class.java).apply {
                if (isRunning) {
                    action = TimerService.ACTION_STOP
                } else {
                    action = TimerService.ACTION_START
                    putExtra(TimerService.EXTRA_TIMER_ID, timerId)
                }
            }
            context.startService(serviceIntent)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        const val ACTION_TOGGLE_TIMER = "com.example.flexibletimer.TOGGLE_TIMER"
        const val EXTRA_TIMER_NAME = "com.example.flexibletimer.TIMER_NAME"
        const val EXTRA_TIMER_STATE_RUNNING = "com.example.flexibletimer.TIMER_STATE_RUNNING"
        const val EXTRA_TIMER_CURRENT_ID = "com.example.flexibletimer.TIMER_CURRENT_ID"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            timerName: String,
            isTimerRunning: Boolean,
            timerId: Long
        ) {
            val views = RemoteViews(context.packageName, R.layout.flexible_timer_widget)

            views.setTextViewText(R.id.widget_timer_name, timerName)

            val playPauseIcon = if (isTimerRunning) {
                android.R.drawable.ic_media_pause
            } else {
                android.R.drawable.ic_media_play
            }
            views.setImageViewResource(R.id.widget_button_play_pause, playPauseIcon)

            val toggleIntent = Intent(context, FlexibleTimerWidget::class.java).apply {
                action = ACTION_TOGGLE_TIMER
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                putExtra(EXTRA_TIMER_CURRENT_ID, timerId)
                putExtra(EXTRA_TIMER_STATE_RUNNING, isTimerRunning)
            }
            val togglePendingIntent = PendingIntent.getBroadcast(
                context, 
                appWidgetId, 
                toggleIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_button_play_pause, togglePendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun sendUpdateBroadcast(context: Context, timerName: String, isTimerRunning: Boolean, timerId: Long) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, FlexibleTimerWidget::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            appWidgetIds.forEach { appWidgetId ->
                updateAppWidget(context, appWidgetManager, appWidgetId, timerName, isTimerRunning, timerId)
            }
        }
    }
}
