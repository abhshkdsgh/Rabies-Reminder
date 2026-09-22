package com.adgh.rabiesreminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adgh.rabiesreminder.ui.theme.RabiesReminderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this@MainActivity)
        enableEdgeToEdge()
        AlarmSoundManager.stopAlarm()

        val sharedPref = getSharedPreferences("com.ADGH.RabiesReminder-Prefs", MODE_PRIVATE)
        setContent {
            var currentTheme by rememberSaveable {
                mutableIntStateOf(
                    sharedPref.getInt(
                        "ColourModeStatus",
                        0
                    )
                )
            }
            val uiHolder: ReminderUiStateHolder = viewModel()
            RabiesReminderTheme(
                darkTheme = when (currentTheme) {
                    1 -> false
                    2 -> true
                    else -> isSystemInDarkTheme()
                }
            ) {
                currentTheme = homeScaffold(
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    uiHolder = uiHolder,
                    sharedPref = sharedPref,
                    onThemeChange = {
                        currentTheme = it
                    },
                    context = this@MainActivity,
                    activity = this@MainActivity
                )
            }
        }
    }
}
