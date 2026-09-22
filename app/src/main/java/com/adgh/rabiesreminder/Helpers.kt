package com.adgh.rabiesreminder

import android.Manifest.permission
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.adgh.rabiesreminder.ui.components.MgText
import com.adgh.rabiesreminder.ui.components.MgTextField

/**
 * List of supported application languages mapped from BCP-47 language codes to display names.
 */
val supportedLanguages = listOf(
    "en" to "English",
    "hi" to "Hindi",
    "kn" to "Kannada",
    "ml" to "Malayalam",
    "ta" to "Tamil",
    "te" to "Telugu"
)

/**
 * Composable dropdown selection field for choosing between Intra-dermal and Intra-muscular PEP schedules.
 *
 * @param value Currently selected display text.
 * @param options List of schedule type display strings.
 * @param basicFontSize Typography scale font size.
 * @param onSelected Callback emitting the selected option index.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdown(
    value: String,
    options: List<Any> = listOf(
        stringResource(R.string.anti_rabies_post_exposure_prophylaxis_intra_dermal),
        stringResource(R.string.anti_rabies_post_exposure_prophylaxis_intra_muscular)
    ),
    basicFontSize: TextUnit,
    onSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        MgTextField(
            value = value,
            onValueChange = { /* onValueChange should be empty for readOnly */ },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            label = stringResource(R.string.schedule_type),
            basicFontSize0 = basicFontSize,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEachIndexed { index, opt ->
                DropdownMenuItem(
                    text = { MgText(text= opt.toString(), basicFontSize = basicFontSize) },
                    onClick = {
                        onSelected(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Composable rendering a schedule preview layout for configuring alarm toggles and confirming creation.
 *
 * @param selectedIndex Selected schedule index (0 for Intra-Dermal, 1 for Intra-Muscular).
 * @param baseLong Base starting vaccination date timestamp in milliseconds.
 * @param onConfirm Callback invoked when schedule confirmation completes.
 * @param basicFontSize Typography font size scale.
 * @param uiHolder View model state holder for reminders.
 * @param context Android context.
 */
@Composable
fun SchedulePreview(
    selectedIndex: Int,
    baseLong: Long,
    onConfirm: () -> Unit,
    basicFontSize: TextUnit,
    uiHolder: ReminderUiStateHolder,
    context: Context
) {
    Column(modifier = Modifier.fillMaxWidth()){
        MgText(text= stringResource(R.string.confirm_schedule), basicFontSize = basicFontSize)
        Column(modifier = Modifier.fillMaxWidth()) {
            val isIntraMuscular = selectedIndex == 1
            ManageNewSchedule(
                currentSchedule = if(isIntraMuscular) PEPSchedule.IntraMuscular else PEPSchedule.IntraDermal,
                baseLong = baseLong,
                basicFontSize = basicFontSize,
                uiHolder = uiHolder,
                onConfirm = onConfirm,
                context= context
            )
        }
    }
}

/** Request code constant for runtime notification permission requests. */
const val permissionRequestCodeNotification = 300

/**
 * Requests POST_NOTIFICATIONS runtime permission on Android 13+ (API level 33).
 *
 * @param activity Target [MainActivity] launching the permission request dialog.
 */
fun requestNotificationPermission(activity: MainActivity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(permission.POST_NOTIFICATIONS),
            permissionRequestCodeNotification
        )
    }
}

/**
 * Creates the high-importance notification channel used by the app for dose alarms.
 *
 * @param context Android context.
 */
fun createNotificationChannel(context: Context) {
    val name: CharSequence = context.getString(R.string.channel_name)
    val description = context.getString(R.string.channel_description)
    val importance = NotificationManager.IMPORTANCE_HIGH
    val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
    channel.description = description
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannel(channel)
}

/**
 * Directs the user to system settings to enable exact alarm scheduling on Android 12+ (API level 31).
 *
 * @param context Android context.
 */
fun requestExactAlarmPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        if (!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = "package:${context.packageName}".toUri()
            }
            context.startActivity(intent)
        }
    }
}

/**
 * Checks if notification permissions and notification settings are enabled for this app.
 *
 * @param context Android context.
 * @return `true` if notifications can be posted, `false` otherwise.
 */
fun areNotificationsEnabled(context: Context): Boolean {
    if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
        return false
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return ContextCompat.checkSelfPermission(
            context,
            permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    return true
}

/**
 * Checks whether the application holds permission to schedule exact alarms.
 *
 * @param context Android context.
 * @return `true` if exact alarm capability is present or unnecessary (pre-Android 12).
 */
fun hasExactAlarmPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.canScheduleExactAlarms()
    } else {
        true
    }
}

/**
 * Formats an integer dose sequence number into a localized ordinal string representation.
 *
 * Supports special suffixes for Hindi, Telugu, Tamil, Malayalam, Kannada, and English fallback.
 *
 * @param context Context used to retrieve resource configuration language.
 * @return Formatted ordinal string (e.g. "1st", "2nd", "3rd", "1st-equivalent in target language").
 */
fun Int.toOrdinal(context: Context): String {
    val lang = context.resources.configuration.locales[0].language

    return when (lang) {
        "hi" -> "${this}वीं"
        "te" -> "${this}వా"
        "ta" -> "${this}-வது"
        "ml" -> "${this}ാമത്തെ"
        "kn" -> "${this}ನೇ"
        else -> {
            if (this in 11..13) return "${this}th"
            when (this % 10) {
                1 -> "${this}st"
                2 -> "${this}nd"
                3 -> "${this}rd"
                else -> "${this}th"
            }
        }
    }
}

