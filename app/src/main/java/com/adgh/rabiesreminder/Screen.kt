package com.adgh.rabiesreminder

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Navigation destination routes used within the application's Compose navigation graph.
 */
enum class Screen {
    /** Screen displaying active and upcoming post-exposure prophylaxis (PEP) reminders. */
    UpcomingReminders,

    /** Screen for configuring and scheduling a new PEP vaccination protocol. */
    NewReminder,

    /** Information leaflet detailing rabies facts, exposure categories, and first-aid steps. */
    AboutRabies,

    /** Page displaying app developer credits and open-source information. */
    CreditsPage,

    /** Page displaying the offline privacy policy. */
    PrivacyPolicyPage
}

/**
 * Represents the supported Post-Exposure Prophylaxis (PEP) anti-rabies vaccination protocols.
 */
enum class PEPSchedule {
    /** Intra-dermal (ID) PEP vaccination protocol (doses on Days 0, 3, 7, 28). */
    IntraDermal,

    /** Intra-muscular (IM) PEP vaccination protocol (doses on Days 0, 3, 7, 14, 28). */
    IntraMuscular
}

/**
 * Utility object responsible for applying language/locale changes to the application configuration.
 */
object LocaleManager {
    /**
     * Updates the application's locale configuration with the specified language tag.
     *
     * @param context The current context.
     * @param language BCP-47 language tag (e.g., "en", "hi", "kn", "ml", "ta", "te").
     * @return A new context wrapping the updated configuration with the target locale.
     */
    fun setLocale(context: Context, language: String): Context {
        val locale = Locale.forLanguageTag(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}
