package com.adgh.rabiesreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * [BroadcastReceiver] that reschedules all active vaccination alarms following a device reboot or app update.
 *
 * Listens for [Intent.ACTION_BOOT_COMPLETED] and [Intent.ACTION_MY_PACKAGE_REPLACED]. Uses
 * [goAsync] to maintain a background coroutine execution window while fetching records from Room
 * and invoking [setMgAlarm].
 */
class ReminderReschedulerReceiver : BroadcastReceiver() {

    /**
     * Receives boot or package update broadcasts and reschedules all future active alarms.
     *
     * @param context Receiver context.
     * @param intent Broadcast intent containing boot or package replaced action.
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (
            intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            val pendingResult = goAsync()
            // Re-schedule all alarms asynchronously
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
