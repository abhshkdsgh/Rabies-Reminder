package com.adgh.rabiesreminder

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReminderReceiver : BroadcastReceiver() {

    @SuppressLint("FullScreenIntentPolicy")
    override fun onReceive(context: Context, intent: Intent) {
        val notificationCode = intent.getIntExtra("notificationCode", 1001)
        val dose = intent.getStringExtra("dose") ?: if (notificationCode == 999) "test dose" else "unknown dose"
        val isPreviousDay = intent.getBooleanExtra("isPreviousDay", false)

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        
        // Ensure channel exists
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, context.getString(R.string.channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.channel_description)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)

        if (!isPreviousDay) AlarmSoundManager.startAlarm(context)

        val stopIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            action = "STOP_ALARM"
            putExtra("notificationCode", notificationCode)
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context, notificationCode, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(context, NotificationDismissReceiver::class.java)
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, notificationCode, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val fullScreenIntent = Intent(context, MainActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Fix: If it's the "previous day" notification (sent at 9am 2 days before),
        // we show the date of the actual injection (2 days from now).
        val displayDate = Calendar.getInstance().apply {
            if (isPreviousDay) add(Calendar.DATE, 2)
        }.time
        
        val currentTime = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(displayDate)

        val title = if (isPreviousDay) {
            context.getString(R.string.injection_reminder)
        } else {
            context.getString(R.string.get_injected_today)
        }
        val content = if (isPreviousDay) {
            context.getString(R.string.dont_forget_your_dose, dose, currentTime)
        } else {
            context.getString(R.string.time_for_dose_today, dose, currentTime)
        }

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setDeleteIntent(dismissPendingIntent)
            .addAction(R.mipmap.ic_launcher, "Stop", stopPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationCode, notification)
    }
}

class NotificationDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        AlarmSoundManager.stopAlarm()
    }
}
