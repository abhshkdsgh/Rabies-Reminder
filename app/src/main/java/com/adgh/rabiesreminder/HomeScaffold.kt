package com.adgh.rabiesreminder

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.adgh.rabiesreminder.ui.components.InfoCard
import com.adgh.rabiesreminder.ui.components.MgAlertDialogue
import com.adgh.rabiesreminder.ui.components.MgButton
import com.adgh.rabiesreminder.ui.components.MgText
import com.adgh.rabiesreminder.ui.theme.AppColors

/**
 * Root composable scaffold managing the primary application frame, top app bar, bottom navigation bar,
 * floating action buttons, theme switching, dynamic font scaling, multi-language locale selection,
 * permissions status banners, and navigation routing between app screens.
 *
 * @param modifier Custom modifier applied to the root scaffold.
 * @param uiHolder View model managing reminder database state.
 * @param sharedPref Preferences used to persist theme, font size, and language settings.
 * @param onThemeChange Callback invoked when the user updates the color mode.
 * @param context Android context for permission and locale updates.
 * @param activity Target [MainActivity] for exit dialog confirmation.
 * @return Int representing the updated theme preference integer (0 = System, 1 = Light, 2 = Dark).
 */
@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun homeScaffold(
    modifier: Modifier,
    uiHolder: ReminderUiStateHolder,
    sharedPref: SharedPreferences,
    onThemeChange: (Int) -> Unit,
    context: Context,
    activity: MainActivity
) : Int {
    var currentTheme by rememberSaveable {
        mutableIntStateOf(sharedPref.getInt("ColourModeStatus", 0))
    }
    var selectedLanguage by rememberSaveable {
        mutableStateOf(sharedPref.getString("language", "en") ?: "en")
    }
    val selectedLanguageName = supportedLanguages.find { it.first == selectedLanguage }?.second ?: "English"
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = navBackStackEntry?.destination?.route?.let { route ->
        Screen.entries.find { it.name == route }
    } ?: Screen.UpcomingReminders


    val onStartCreate: () -> Unit = {
        if (currentScreen != Screen.NewReminder) {
            navController.navigate(Screen.NewReminder.name) {
                launchSingleTop = true
            }
        }
    }
    val onOpenUpcoming: () -> Unit = {
        if (currentScreen != Screen.UpcomingReminders) {
            navController.navigate(Screen.UpcomingReminders.name) {
                popUpTo(Screen.UpcomingReminders.name) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    val onOpenAboutRabies: () -> Unit = {
        if (currentScreen != Screen.AboutRabies) {
            navController.navigate(Screen.AboutRabies.name) {
                popUpTo(Screen.AboutRabies.name) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    var basicFontSize by rememberSaveable { mutableIntStateOf(sharedPref.getInt("basicFontSize", 16)) }
    var isLanguageDropDownMenuExpanded by rememberSaveable{mutableStateOf(false)}
    var isExitDialogueOpen by rememberSaveable {
        mutableStateOf(false)
    }
    CompositionLocalProvider(LocalContext provides LocaleManager.setLocale(context, selectedLanguage)){
        Scaffold(
            modifier = modifier.fillMaxSize(),
            floatingActionButton = {
                if(currentScreen == Screen.UpcomingReminders){
                    ExtendedFloatingActionButton(
                        text = {
                            MgText(
                                text = stringResource(R.string.new_reminder),
                                basicFontSize = basicFontSize.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        icon = {
                            Icon(
                                Icons.Default.AddCircle,
                                contentDescription = stringResource(R.string.new_reminder)
                            )
                        },
                        onClick = onStartCreate,
                        containerColor = AppColors.Primary,
                        modifier = Modifier.padding(bottom = 96.dp)
                    )
                }
            },
            containerColor = AppColors.Background
        ) { innerPadding ->
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ){
                    var areOptionsExpanded by rememberSaveable { mutableStateOf(false) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SmallTopAppBar(
                            title = {
                                Spacer(Modifier.weight(0.5f))
                                MgText(
                                    text = stringResource(R.string.app_name),
                                    basicFontSize = basicFontSize.sp,
                                    isBold = true,
                                    times = 1.21f
                                )
                                Spacer(Modifier.weight(0.5f))
                            },
                            actions = {
                                IconButton(onClick = {
                                    if (currentScreen != Screen.AboutRabies) {
                                        navController.navigate(Screen.AboutRabies.name) {
                                            launchSingleTop = true
                                        }
                                    }
                                }) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = stringResource(R.string.about)
                                    )
                                }
                                Box {
                                    IconButton(
                                        onClick = { areOptionsExpanded = !areOptionsExpanded }
                                    ) {
                                        Icon(
                                            Icons.Default.MoreVert,
                                            contentDescription = stringResource(R.string.menu)
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = areOptionsExpanded,
                                        onDismissRequest = {
                                            areOptionsExpanded = false
                                        }
                                    ) {
                                        DropdownMenuItem(
                                            text = {
                                                CompositionLocalProvider(
                                                    LocalContext provides LocaleManager.setLocale(
                                                        context,
                                                        selectedLanguage
                                                    )
                                                ) {
                                                    Column(
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        when (
                                                            sharedPref.getInt(
                                                                "ColourModeStatus",
                                                                0
                                                            )
                                                        ) {
                                                            2 -> {
                                                                MgButton(
                                                                    onClick = {
                                                                        onThemeChange(1)
                                                                        sharedPref.edit {
                                                                            putInt(
                                                                                "ColourModeStatus",
                                                                                1
                                                                            )
                                                                        }
                                                                        currentTheme = 1
                                                                    },
                                                                    text = stringResource(R.string.force_light_mode),
                                                                    basicFontSize = basicFontSize.sp,
                                                                    isPaddingForced = false
                                                                )
                                                                MgButton(
                                                                    onClick = {
                                                                        onThemeChange(0)
                                                                        sharedPref.edit {
                                                                            putInt(
                                                                                "ColourModeStatus",
                                                                                0
                                                                            )
                                                                        }
                                                                        currentTheme = 0
                                                                    },
                                                                    isPaddingForced = false,
                                                                    text = stringResource(R.string.follow_system_colour_mode),
                                                                    basicFontSize = basicFontSize.sp
                                                                )
                                                            }

                                                            1 -> {
                                                                MgButton(
                                                                    onClick = {
                                                                        onThemeChange(2)
                                                                        sharedPref.edit {
                                                                            putInt(
                                                                                "ColourModeStatus",
                                                                                2
                                                                            )
                                                                        }
                                                                        currentTheme = 2
                                                                    },
                                                                    isPaddingForced = false,
                                                                    text = stringResource(R.string.force_dark_mode),
                                                                    basicFontSize = basicFontSize.sp
                                                                )
                                                                MgButton(
                                                                    onClick = {
                                                                        onThemeChange(0)
                                                                        sharedPref.edit {
                                                                            putInt(
                                                                                "ColourModeStatus",
                                                                                0
                                                                            )
                                                                        }
                                                                        currentTheme = 0
                                                                    },
                                                                    isPaddingForced = false,
                                                                    text = stringResource(R.string.follow_system_colour_mode),
                                                                    basicFontSize = basicFontSize.sp
                                                                )
                                                            }

                                                            0 -> {
                                                                MgButton(
                                                                    onClick = {
                                                                        onThemeChange(1)
                                                                        sharedPref.edit {
                                                                            putInt(
                                                                                "ColourModeStatus",
                                                                                1
                                                                            )
                                                                        }
                                                                        currentTheme = 1
                                                                    },
                                                                    isPaddingForced = false,
                                                                    text = stringResource(R.string.force_light_mode),
                                                                    basicFontSize = basicFontSize.sp
                                                                )
                                                                MgButton(
                                                                    onClick = {
                                                                        onThemeChange(2)
                                                                        sharedPref.edit {
                                                                            putInt(
                                                                                "ColourModeStatus",
                                                                                2
                                                                            )
                                                                        }
                                                                        currentTheme = 2
                                                                    },
                                                                    isPaddingForced = false,
                                                                    text = stringResource(R.string.force_dark_mode),
                                                                    basicFontSize = basicFontSize.sp
                                                                )
                                                            }
                                                        }
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.Center
                                                        ) {
                                                            Column(
                                                                verticalArrangement = Arrangement.Center,
                                                                horizontalAlignment = Alignment.CenterHorizontally
                                                            ) {
                                                                MgText(
                                                                    text = stringResource(R.string.change_font_size),
                                                                    basicFontSize = basicFontSize.sp
                                                                )
                                                                MgText(
                                                                    text = stringResource(
                                                                        R.string.current_font_size,
                                                                        basicFontSize
                                                                    ),
                                                                    basicFontSize = basicFontSize.sp
                                                                )
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.Center
                                                                ) {
                                                                    if (basicFontSize > 12) {
                                                                        Column(
                                                                            Modifier
                                                                                .clickable {
                                                                                    basicFontSize--
                                                                                    sharedPref
                                                                                        .edit {
                                                                                            putInt(
                                                                                                "basicFontSize",
                                                                                                basicFontSize
                                                                                            )
                                                                                        }
                                                                                }
                                                                                .background(
                                                                                    MaterialTheme.colorScheme.primary
                                                                                )
                                                                                .border(
                                                                                    BorderStroke(
                                                                                        5.dp,
                                                                                        MaterialTheme.colorScheme.onPrimary
                                                                                    )
                                                                                )
                                                                                .height(48.dp)
                                                                                .width(48.dp),
                                                                            verticalArrangement = Arrangement.Center,
                                                                            horizontalAlignment = Alignment.CenterHorizontally
                                                                        ) {
                                                                            MgText(
                                                                                text = stringResource(
                                                                                    R.string.a_minus
                                                                                ),
                                                                                basicFontSize = basicFontSize.sp,
                                                                                color = MaterialTheme.colorScheme.onPrimary
                                                                            )
                                                                        }
                                                                    }
                                                                    if (basicFontSize != 16) {
                                                                        Column(
                                                                            Modifier
                                                                                .clickable {
                                                                                    basicFontSize =
                                                                                        16
                                                                                    sharedPref
                                                                                        .edit {
                                                                                            putInt(
                                                                                                "basicFontSize",
                                                                                                basicFontSize
                                                                                            )
                                                                                        }
                                                                                }
                                                                                .background(
                                                                                    MaterialTheme.colorScheme.primary
                                                                                )
                                                                                .border(
                                                                                    BorderStroke(
                                                                                        5.dp,
                                                                                        MaterialTheme.colorScheme.onPrimary
                                                                                    )
                                                                                )
                                                                                .height(48.dp)
                                                                                .width(48.dp),
                                                                            verticalArrangement = Arrangement.Center,
                                                                            horizontalAlignment = Alignment.CenterHorizontally
                                                                        ) {
                                                                            MgText(
                                                                                text = stringResource(
                                                                                    R.string.a
                                                                                ),
                                                                                basicFontSize = basicFontSize.sp,
                                                                                color = MaterialTheme.colorScheme.onPrimary
                                                                            )
                                                                        }
                                                                    }
                                                                    if (basicFontSize < 21) {
                                                                        Column(
                                                                            Modifier
                                                                                .clickable {
                                                                                    basicFontSize++
                                                                                    sharedPref
                                                                                        .edit {
                                                                                            putInt(
                                                                                                "basicFontSize",
                                                                                                basicFontSize
                                                                                            )
                                                                                        }
                                                                                }
                                                                                .background(
                                                                                    MaterialTheme.colorScheme.primary
                                                                                )
                                                                                .border(
                                                                                    BorderStroke(
                                                                                        5.dp,
                                                                                        MaterialTheme.colorScheme.onPrimary
                                                                                    )
                                                                                )
                                                                                .height(48.dp)
                                                                                .width(48.dp),
                                                                            verticalArrangement = Arrangement.Center,
                                                                            horizontalAlignment = Alignment.CenterHorizontally
                                                                        ) {
                                                                            MgText(
                                                                                text = stringResource(
                                                                                    R.string.a_plus
                                                                                ),
                                                                                basicFontSize = basicFontSize.sp,
                                                                                color = MaterialTheme.colorScheme.onPrimary
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }, onClick = { }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                CompositionLocalProvider(
                                                    LocalContext provides LocaleManager.setLocale(
                                                        context,
                                                        selectedLanguage
                                                    )
                                                ) {
                                                    Row(
                                                        horizontalArrangement = Arrangement.End
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.Center
                                                        ) {
                                                            MgText(
                                                                text = stringResource(R.string.change_language),
                                                                basicFontSize = basicFontSize.sp
                                                            )
                                                            Spacer(modifier = Modifier.width((basicFontSize / 2).dp))
                                                            MgText(
                                                                text = selectedLanguageName,
                                                                basicFontSize = basicFontSize.sp
                                                            )
                                                        }
                                                        if (isLanguageDropDownMenuExpanded) {
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.End
                                                            ) {
                                                                DropdownMenu(
                                                                    expanded = true,
                                                                    onDismissRequest = {
                                                                        isLanguageDropDownMenuExpanded =
                                                                            false
                                                                    }
                                                                ) {
                                                                    supportedLanguages.forEach { languagePair ->
                                                                        DropdownMenuItem(
                                                                            text = {
                                                                                MgText(
                                                                                    text = languagePair.second,
                                                                                    basicFontSize = basicFontSize.sp
                                                                                )
                                                                            },
                                                                            onClick = {
                                                                                LocaleManager.setLocale(
                                                                                    context,
                                                                                    languagePair.first
                                                                                )
                                                                                sharedPref.edit {
                                                                                    putString(
                                                                                        "language",
                                                                                        languagePair.first
                                                                                    )
                                                                                }
                                                                                selectedLanguage =
                                                                                    languagePair.first
                                                                            }
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            },
                                            onClick = {
                                                isLanguageDropDownMenuExpanded =
                                                    true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                CompositionLocalProvider(
                                                    LocalContext provides LocaleManager.setLocale(
                                                        context,
                                                        selectedLanguage
                                                    )
                                                ) {
                                                    MgText(
                                                        text = "Privacy Policy",
                                                        basicFontSize = basicFontSize.sp
                                                    )
                                                }
                                            },
                                            onClick = {
                                                if (currentScreen != Screen.PrivacyPolicyPage) {
                                                    navController.navigate(Screen.PrivacyPolicyPage.name) {
                                                        launchSingleTop = true
                                                    }
                                                }
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                CompositionLocalProvider(
                                                    LocalContext provides LocaleManager.setLocale(
                                                        context,
                                                        selectedLanguage
                                                    )
                                                ) {
                                                    MgText(
                                                        text = "Credits",
                                                        basicFontSize = basicFontSize.sp
                                                    )
                                                }
                                            },
                                            onClick = {
                                                if (currentScreen != Screen.CreditsPage) {
                                                    navController.navigate(Screen.CreditsPage.name) {
                                                        launchSingleTop = true
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        var forRecomposition by rememberSaveable { mutableIntStateOf(0) }
                        val isNotificationPermissionGiven by produceState(
                            initialValue = false,
                            key1 = forRecomposition
                        ) {
                            value = areNotificationsEnabled(
                                context = context
                            )
                        }
                        val isExactAlarmPermissionIsGiven by produceState(
                            initialValue = false,
                            key1 = forRecomposition
                        ) {
                            value = hasExactAlarmPermission(context)
                        }
                        InfoCard()
                        Spacer(Modifier.height(12.dp))

                        val permissionsGranted = hasExactAlarmPermission(context) && areNotificationsEnabled(context)

                        NavHost(
                            navController = navController,
                            startDestination = Screen.UpcomingReminders.name
                        ) {
                            composable(Screen.UpcomingReminders.name) {
                                if (!permissionsGranted) {
                                    Column(
                                        modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(4.dp)
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.primary,
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(4.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        MgText(
                                            text = stringResource(R.string.please_enable_notification_permission_and_exact_alarm_permission_to_set_reminders),
                                            basicFontSize = basicFontSize.sp,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Row(
                                            modifier = Modifier.height(IntrinsicSize.Min),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            if (!isExactAlarmPermissionIsGiven) {
                                                MgButton(
                                                    onClick = { requestExactAlarmPermission(context) },
                                                    text = stringResource(R.string.request_exact_alarm_permission),
                                                    basicFontSize = basicFontSize.sp,
                                                    modifier = Modifier
                                                        .fillMaxWidth(0.5f)
                                                        .padding(4.dp)
                                                )
                                            } else {
                                                MgText(
                                                    text = stringResource(R.string.alarm_permission_is_enabled),
                                                    basicFontSize = basicFontSize.sp,
                                                    modifier = Modifier
                                                        .fillMaxWidth(0.5f)
                                                        .padding(4.dp)
                                                )
                                            }
                                            VerticalDivider(
                                                color = MaterialTheme.colorScheme.primary,
                                                thickness = 4.dp
                                            )
                                            if (!isNotificationPermissionGiven) {
                                                MgButton(
                                                    onClick = { requestNotificationPermission(activity) },
                                                    text = stringResource(R.string.request_notification_permission),
                                                    basicFontSize = basicFontSize.sp,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(4.dp)
                                                )
                                            } else {
                                                MgText(
                                                    text = stringResource(R.string.notifications_are_enabled),
                                                    basicFontSize = basicFontSize.sp,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(4.dp)
                                                )
                                            }
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        MgButton(
                                            onClick = {
                                                if (forRecomposition == 100)
                                                    forRecomposition = 0
                                                else forRecomposition++
                                            },
                                            text = stringResource(R.string.refresh),
                                            basicFontSize = basicFontSize.sp
                                        )
                                    }
                                } else {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        UpcomingRemindersScreen(
                                            uiHolder = uiHolder,
                                            onCreate = onStartCreate,
                                            basicFontSize = basicFontSize.sp,
                                            context = context
                                        )
                                        Spacer(Modifier.height((basicFontSize * 5.76f).dp))
                                        MgButton(
                                            text = stringResource(R.string.rabies_information_sheet),
                                            basicFontSize = basicFontSize.sp,
                                            onClick = {
                                                if (currentScreen != Screen.AboutRabies) {
                                                    navController.navigate(Screen.AboutRabies.name) {
                                                        launchSingleTop = true
                                                    }
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }

                            composable(Screen.NewReminder.name) {
                                CreateScheduleScreen(
                                    onConfirm = onOpenUpcoming,
                                    uiHolder = uiHolder,
                                    basicFontSize = basicFontSize.sp,
                                    context = context,
                                    selectedLanguage = selectedLanguage
                                )
                            }

                            composable(Screen.AboutRabies.name) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    MgButton(
                                        text = stringResource(R.string.go_back_to_home_screen),
                                        basicFontSize = basicFontSize.sp,
                                        onClick = onOpenUpcoming,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    RabiesInfoLeaflet(
                                        basicFontSize = basicFontSize.sp
                                    )
                                    MgButton(
                                        text = stringResource(R.string.go_back_to_home_screen),
                                        basicFontSize = basicFontSize.sp,
                                        onClick = onOpenUpcoming,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            composable(Screen.CreditsPage.name) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    MgButton(
                                        text = stringResource(R.string.go_back_to_home_screen),
                                        basicFontSize = basicFontSize.sp,
                                        onClick = onOpenUpcoming,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    CreditsPage(basicFontSize = basicFontSize.sp)
                                    MgButton(
                                        text = stringResource(R.string.go_back_to_home_screen),
                                        basicFontSize = basicFontSize.sp,
                                        onClick = onOpenUpcoming,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            composable(Screen.PrivacyPolicyPage.name) {
                                val base = basicFontSize.sp
                                val screenHeight =
                                    (LocalConfiguration.current.screenHeightDp - 48).dp

                                Column(
                                    Modifier
                                        .height(screenHeight)
                                        .fillMaxWidth()
                                ) {
                                    PrivacyPolicyScreen(base, context, navController)
                                }
                            }
                        }
                        Spacer(Modifier.height(96.dp))
                    }
                    NavigationBar {
                        listOf(
                            Screen.UpcomingReminders,
                            Screen.NewReminder,
                            Screen.AboutRabies
                        ).forEachIndexed { index, screen ->
                            NavigationBarItem (
                                selected = screen == currentScreen,
                                onClick = {
                                    when(index){
                                        0 -> {
                                            onOpenUpcoming()
                                        }
                                        1 -> {
                                            onStartCreate()
                                        }
                                        2 -> {
                                            onOpenAboutRabies()
                                        }
                                    }
                                },
                                icon = {
                                    when(index) {
                                        0 -> {
                                            Icon(
                                                painter = painterResource(R.drawable.bell_icon_line),
                                                contentDescription = stringResource(R.string.reminders),
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                        1 -> {
                                            Icon(
                                                painter = painterResource(R.drawable.new_alarm),
                                                contentDescription = stringResource(R.string.new_reminder),
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                        2 -> {
                                            Icon(
                                                painter = painterResource(R.drawable.info),
                                                contentDescription = stringResource(R.string.about_rabies),
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                },
                                label = {
                                    when(index){
                                        0 -> {
                                            MgText(
                                                text = stringResource(R.string.reminders),
                                                basicFontSize = basicFontSize.sp
                                            )
                                        }
                                        1 -> {
                                            MgText(
                                                text = stringResource(R.string.new_reminder),
                                                basicFontSize = basicFontSize.sp
                                            )
                                        }
                                        2 -> {
                                            MgText(
                                                text = stringResource(R.string.about_rabies),
                                                basicFontSize = basicFontSize.sp
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                BackHandler {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    } else {
                        isExitDialogueOpen = true
                    }
                }

                MgAlertDialogue(
                    showDialog = isExitDialogueOpen,
                    onDismissRequest = { isExitDialogueOpen = false },
                    onConfirmation = { activity.finish() },
                    onReject = { isExitDialogueOpen = false },
                    title = stringResource(R.string.exit_alert),
                    message = stringResource(R.string.are_you_sure_that_you_want_to_exit),
                    confirmString = stringResource(R.string.yes),
                    dismissString = stringResource(R.string.no),
                    basicFontSize = basicFontSize.sp
                )
            }
        }
    }
    return currentTheme
}

/**
 * Custom small top app bar component displaying title, navigation icon, and actions menu.
 *
 * @param title Composable content rendered as the app bar title.
 * @param modifier Layout modifier.
 * @param navigationIcon Optional navigation icon composable slot.
 * @param actions Optional action items composable slot.
 * @param backgroundColor Background color of the top bar surface.
 * @param contentColor Tint color for text and icons inside the top bar.
 * @param elevation Surface tonal and shadow elevation.
 */
@Composable
fun SmallTopAppBar(
    title: @Composable ()->Unit,
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    elevation: Dp = 0.dp
) {
    Surface(
        color = backgroundColor,
        tonalElevation = elevation,
        shadowElevation = elevation,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (navigationIcon != null) {
                Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                    CompositionLocalProvider(LocalContentColor provides contentColor) { navigationIcon() }
                }
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }

            Spacer(modifier = Modifier.width(4.dp))
            title()
            Spacer(modifier = Modifier.width(4.dp))

            if (actions != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CompositionLocalProvider(LocalContentColor provides contentColor) { actions() }
                }
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}