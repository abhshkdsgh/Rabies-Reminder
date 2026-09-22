# Rabies Reminder 💉

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20Material%203-blue.svg)](https://developer.android.com/jetpack/compose)
[![Hilt](https://img.shields.io/badge/DI-Hilt-orange.svg)](https://dagger.dev/hilt/)
[![Room](https://img.shields.io/badge/Storage-Room-red.svg)](https://developer.android.com/training/data-storage/room)
[![License: GPL v3](https://img.shields.io/badge/License-GNU%20GPL%20v3.0-blue.svg)](LICENSE)

**Rabies Reminder** is an open-source, offline-first Android application designed to help patients and healthcare practitioners manage, schedule, and track Post-Exposure Prophylaxis (PEP) anti-rabies vaccination regimens reliably.

Rabies is a 100% preventable viral disease if timely post-exposure prophylaxis is administered. However, missing or delaying vaccination doses can be fatal. **Rabies Reminder** ensures critical vaccination deadlines are never missed by scheduling exact system alarms, advance pre-reminders, and localized notifications.

---

## 🌟 Key Features

* **💉 WHO-Standard PEP Schedules**:
  * **Intra-Dermal (ID) Regimen**: Updated Thai Red Cross schedule with doses on **Day 0, Day 3, Day 7, and Day 28**.
  * **Intra-Muscular (IM) Regimen**: Essen schedule with doses on **Day 0, Day 3, Day 7, Day 14, and Day 28**.
* **⏰ High-Priority Exact Alarms**:
  * Uses Android `AlarmManager` (`setExactAndAllowWhileIdle`) so dose alarms fire reliably even when the device is in **Doze Mode**.
  * Audio playback via `MediaPlayer` with automatic 90-second timeout safety.
* **🔔 Advance Pre-Reminders**:
  * Optional pre-reminders scheduled **2 days before each dose at 9:00 AM** to allow adequate time for travel or clinic appointment booking.
* **🔄 Reboot & Package Update Persistence**:
  * `ReminderReschedulerReceiver` automatically restores and reschedules all active alarms following a device reboot or application update (`ACTION_BOOT_COMPLETED` & `ACTION_MY_PACKAGE_REPLACED`).
* **🌐 Multi-Language Localization**:
  * Built-in dynamic locale switcher supporting 6 languages:
    * 🇬🇧 **English**
    * 🇮🇳 **Hindi (हिंदी)**
    * 🇮🇳 **Kannada (ಕನ್ನಡ)**
    * 🇮🇳 **Malayalam (മലയാളം)**
    * 🇮🇳 **Tamil (தமிழ்)**
    * 🇮🇳 **Telugu (తెలుగు)**
* **🔒 100% Offline & Privacy-First**:
  * Operates completely offline without requiring internet access or network permissions.
  * All vaccination schedule data remains strictly on the user's local device inside an encrypted Room database (`AppDatabase`).
* **🎨 Customization & Accessibility**:
  * Material 3 dynamic color schemes with System, Force Light, and Force Dark mode toggles.
  * In-app font scaling controls ($12\text{ sp}$ to $21\text{ sp}$) for enhanced legibility.
* **📖 Medical Awareness & First-Aid Leaflet**:
  * Integrated educational guide explaining animal bite exposure categories (Categories I, II, III), immediate 15-minute soap-and-water first-aid, rabid animal symptoms, and prevention strategies.

---

## 🏗️ Architecture & Tech Stack

This project follows **Clean Architecture** principles and the recommended Android **MVVM (Model-View-ViewModel)** architectural pattern:

```
com.adgh.rabiesreminder/
├── RabiesReminderApp.kt          # Application entry point with @HiltAndroidApp
├── MainActivity.kt               # Single-activity UI container with @AndroidEntryPoint
├── Screen.kt                     # Navigation routes & LocaleManager
├── DatabaseHandler.kt            # Room AppDatabase, HistoryDao & MgRecord entity
├── ReminderRepository.kt         # Single source of truth repository layer
├── ReminderViewModel.kt          # UiState StateFlow & business logic holder
├── AlarmManagerHelper.kt         # Exact alarm scheduling & cancellation helpers
├── AlarmSoundManager.kt          # Looping MediaPlayer audio manager
├── AlarmActionReceiver.kt        # BroadcastReceiver for notification stop actions
├── ReminderReceiver.kt           # BroadcastReceiver for exact alarm triggers
├── ReminderReschedulerReceiver.kt # BroadcastReceiver for boot/update rescheduling
├── Helpers.kt                    # Permission checks & localized ordinal utilities
├── CreateScheduleScreen.kt       # Date picker & PEP schedule configuration UI
├── ManageNewSchedule.kt          # Dose calculation engine & alarm setup UI
├── UpcomingReminders.kt          # Scheduled reminders list view & filtering
├── PrivacyPolicyScreen.kt        # Offline privacy policy page
├── RabiesInfoLeaflet.kt          # Rabies educational & first-aid leaflet
├── CreditsPage.kt                # Developer credits page
├── di/                           # Dependency Injection
│   └── DatabaseModule.kt         # Hilt module providing database singletons
└── ui/                           # UI Styling & Design System
    ├── components/
    │   └── MgComponents.kt       # Reusable Compose Material 3 components
    └── theme/
        └── Theme.kt              # AppColors & Material 3 typography definitions
```

### Tech Stack Highlights:
* **Language**: 100% Idiomatic [Kotlin](https://kotlinlang.org/)
* **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 Design
* **Architecture**: MVVM + Repository Pattern
* **Dependency Injection**: [Hilt](https://dagger.dev/hilt/)
* **Local Database**: [Room Persistence Library](https://developer.android.com/training/data-storage/room) with KSP
* **Asynchronous Flow**: Kotlin Coroutines & `StateFlow`
* **Navigation**: Jetpack Compose Navigation (`NavHost`)
* **System Services**: `AlarmManager`, `NotificationManager`, `BroadcastReceiver`

---

## 📱 Screenshots & User Experience

| Upcoming Reminders | Schedule Creation | Rabies Awareness Leaflet |
| :---: | :---: | :---: |
| Overview of active & upcoming doses grouped by date | Intra-Dermal vs Intra-Muscular regimen builder with date picker | Emergency first-aid, exposure categories & vaccination facts |

---

## 🚀 Getting Started

### Prerequisites
* **Android Studio**: Ladybug / Jellyfish or newer
* **JDK**: Java 11 or higher
* **Android SDK**: `compileSdk = 37`, `minSdk = 29` (Android 10+)

### Building from Source

1. **Clone the repository**:
   ```bash
   git clone https://github.com/abhshkdsgh/rabies-reminder.git
   cd rabies-reminder
   ```

2. **Open in Android Studio**:
   Open Android Studio, select **Open**, and select the cloned project folder.

3. **Build the project**:
   Run a Gradle sync and assemble debug build:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run on a Device / Emulator**:
   Connect an Android device running API 29 or higher and launch the `:app` configuration.

---

## 🛡️ Permissions

The app requests the following system permissions to operate reliable alarms:

* `android.permission.POST_NOTIFICATIONS` *(Android 13+)*: Required to show high-priority heads-up reminder notifications.
* `android.permission.SCHEDULE_EXACT_ALARM` *(Android 12+)*: Required to schedule exact-time vaccination alarms.
* `android.permission.RECEIVE_BOOT_COMPLETED`: Required to restore and reschedule active alarms after device restart.

---

## 📄 License & Terms

Created and maintained by **Dr. Abhishek Das G H**.

This project is licensed under the **GNU General Public License v3.0 (GNU GPL v3.0)** - see the [LICENSE](LICENSE) file for details.

### 🚫 Non-Commercial & Anti-Monetization Policy
This software is developed strictly as a **free, public healthcare tool and educational resource**. It is explicitly prohibited to use, modify, redistribute, or re-package this application or its source code for commercial gain, paid app sales, paid subscriptions, or in-app monetization/advertising.

> **Disclaimer**: This application is a reminder aid tool and educational guide. It is not a substitute for professional medical advice, diagnosis, or emergency clinical care. In case of an animal bite, immediately wash the wound under running water with soap for at least 15 minutes and seek emergency treatment at the nearest hospital or anti-rabies clinic.
