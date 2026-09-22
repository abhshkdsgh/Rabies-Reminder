package com.adgh.rabiesreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

const val NOTIFICATION_CHANNEL_ID = "reminder_channel"

fun setMgAlarm(context: Context, id0: Int, dose: Int, mgLong: Long, isPreviousDay: Boolean = false) {
    if (mgLong <= System.currentTimeMillis()) return

    val alarmManager = context.getSystemService(AlarmManager::class.java)
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("notificationCode", id0)
        putExtra("dose", dose.toString())
        putExtra("isPreviousDay", isPreviousDay)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context, id0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    try {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            mgLong,
            pendingIntent
        )
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}

fun calculateAlarmLong(baseLong: Long, daysToAdd: Int, hourOfDay: Int? = null): Long {
    return Calendar.getInstance().apply {
        timeInMillis = baseLong
        add(Calendar.DAY_OF_YEAR, daysToAdd)
        if (hourOfDay != null) {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, 0)
        }
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

fun cancelAlarm(context: Context, notificationCode: Int, dose: String, isPreviousDay: Boolean) {
    val alarmManager = context.getSystemService(AlarmManager::class.java)
    val intent = Intent(
        context, ReminderReceiver::class.java
    ).apply {
        putExtra("notificationCode", notificationCode)
        putExtra("dose", dose)
        putExtra("isPreviousDay", isPreviousDay)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context, notificationCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager.cancel(pendingIntent)
}
