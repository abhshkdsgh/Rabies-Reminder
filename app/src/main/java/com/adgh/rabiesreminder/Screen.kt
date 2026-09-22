package com.adgh.rabiesreminder

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

enum class Screen {
    UpcomingReminders,
    NewReminder,
    AboutRabies,
    CreditsPage,
    PrivacyPolicyPage
}

enum class PEPSchedule{
    IntraDermal,
    IntraMuscular
}

object LocaleManager {
    fun setLocale(context: Context, language: String): Context {
        val locale = Locale.forLanguageTag(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}