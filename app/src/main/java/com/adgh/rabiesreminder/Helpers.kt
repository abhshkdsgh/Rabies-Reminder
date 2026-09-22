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

val supportedLanguages = listOf(
    "en" to "English",
    "hi" to "Hindi",
    "kn" to "Kannada",
    "ml" to "Malayalam",
    "ta" to "Tamil",
    "te" to "Telugu"
)

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

const val permissionRequestCodeNotification = 300

fun requestNotificationPermission(activity: MainActivity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(permission.POST_NOTIFICATIONS),
            permissionRequestCodeNotification
        )
    }
}

fun createNotificationChannel(context: Context) {
    val name: CharSequence = context.getString(R.string.channel_name)
    val description = context.getString(R.string.channel_description)
    val importance = NotificationManager.IMPORTANCE_HIGH
    val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
    channel.description = description
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannel(channel)
}

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

fun hasExactAlarmPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.canScheduleExactAlarms()
    } else {
        true
    }
}

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
