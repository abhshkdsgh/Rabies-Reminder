package com.adgh.rabiesreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

/** Notification channel ID string used for posting vaccination reminder notifications. */
const val NOTIFICATION_CHANNEL_ID = "reminder_channel"

/**
 * Schedules an exact alarm with [AlarmManager] to fire at the specified time.
 *
 * Registers a broadcast [PendingIntent] directed at [ReminderReceiver]. Uses
 * [AlarmManager.setExactAndAllowWhileIdle] so the alarm fires even when the device is in Doze mode.
 *
 * @param context Application context.
 * @param id0 Unique identifier matching the reminder database ID and PendingIntent request code.
 * @param dose Sequence number of the vaccination dose (1-based).
 * @param mgLong Epoch timestamp in milliseconds when the alarm should trigger.
 * @param isPreviousDay `true` if this alarm is a pre-reminder scheduled prior to the injection date.
 */
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

/**
 * Calculates a future timestamp in milliseconds by adding days and setting a specific target hour.
 *
 * @param baseLong Starting timestamp in milliseconds since epoch.
 * @param daysToAdd Number of days to offset from [baseLong].
 * @param hourOfDay Optional target hour of day (0-23) at which to set the alarm time.
 * @return Computed timestamp in milliseconds since epoch.
 */
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

/**
 * Cancels a previously scheduled exact alarm from [AlarmManager].
 *
 * Recreates the matching [PendingIntent] using `notificationCode` and revokes it from system scheduler.
 *
 * @param context Application context.
 * @param notificationCode Unique identifier corresponding to the scheduled PendingIntent request code.
 * @param dose Sequence identifier for the dose.
 * @param isPreviousDay Flag indicating whether it was a pre-reminder alarm.
 */
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

