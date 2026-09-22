package com.adgh.rabiesreminder

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main [Application] class for the Rabies Reminder application.
 *
 * Annotated with [@HiltAndroidApp][HiltAndroidApp] to trigger Hilt dependency injection
 * code generation, setting up the application-level dependency container for Hilt modules.
 */
@HiltAndroidApp
class RabiesReminderApp : Application()

