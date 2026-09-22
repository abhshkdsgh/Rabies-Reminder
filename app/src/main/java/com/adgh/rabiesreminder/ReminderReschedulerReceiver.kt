package com.adgh.rabiesreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReschedulerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (
            intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            val pendingResult = goAsync()
            // Re-schedule all alarms
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val roomDatabaseInstance = DatabaseHandler(
                        AppDatabase.getDatabase(context.applicationContext)
                    )
                    val reminders =
                        roomDatabaseInstance.getAllEntries()
                    for (reminder in reminders) {
                        if (reminder.long0 > System.currentTimeMillis() && reminder.isActive) {
                            setMgAlarm(
                                context,
                                reminder.id0,
                                reminder.doseNumber,
                                reminder.long0,
                                reminder.isPreviousDay
                            )
                        }
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}