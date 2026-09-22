package com.adgh.rabiesreminder

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * [BroadcastReceiver] handling explicit notification actions such as stopping a ringing alarm.
 *
 * Listens for the "STOP_ALARM" action triggered by the user tapping the "Stop" button on an active notification.
 */
class AlarmActionReceiver : BroadcastReceiver() {

    /**
     * Receives broadcast intents and cancels alarm playback and active notification display.
     *
     * @param context Context in which the receiver is running.
     * @param intent Broadcast intent containing action and "notificationCode" extra.
     */
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            "STOP_ALARM" -> {
                val notificationCode = intent.getIntExtra("notificationCode", 1001)
                AlarmSoundManager.stopAlarm()
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                try {
                    notificationManager.cancel(notificationCode)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

