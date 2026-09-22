package com.adgh.rabiesreminder

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            "STOP_ALARM" -> {
                val notificationCode = intent.getIntExtra("notificationCode", 1001)
                AlarmSoundManager.stopAlarm()
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                try{
                    notificationManager.cancel(notificationCode)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
