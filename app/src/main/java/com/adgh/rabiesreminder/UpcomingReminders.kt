package com.adgh.rabiesreminder

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.adgh.rabiesreminder.ui.components.EmptyState
import com.adgh.rabiesreminder.ui.components.MgAlertDialogue
import com.adgh.rabiesreminder.ui.components.MgButton
import com.adgh.rabiesreminder.ui.components.MgText
import com.adgh.rabiesreminder.ui.components.MgTextField
import com.adgh.rabiesreminder.ui.components.ReminderCard
import com.adgh.rabiesreminder.ui.theme.AppColors
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingRemindersScreen(uiHolder: ReminderUiStateHolder, onCreate: () -> Unit, basicFontSize: TextUnit, context: Context) {
    val uiState by uiHolder.uiState.collectAsState()

    when (val currentState = uiState) {
        is UiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is UiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                MgText(text= currentState.message, basicFontSize = basicFontSize)
            }
        }
        is UiState.Ready -> {
            var selectedOption by rememberSaveable {
                mutableStateOf(ReminderFilter.New)
            }
            val itemsToShow = if(selectedOption== ReminderFilter.New){
                currentState.reminders.filter {
                    it.long0 > currentTimeMillis()
                }
            } else currentState.reminders
            if (itemsToShow.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        basicFontSize = basicFontSize,
                        onCreate = {
                            onCreate()
                        },
                        showAllEntries = {
                            selectedOption = ReminderFilter.All
                        },
                        isAllEntriesSelected = selectedOption == ReminderFilter.All
                    )
                }
            }
            else {
                var isOldItemsVisible by rememberSaveable {
                    mutableStateOf(false)
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    val options = listOf(
                        stringResource(R.string.upcoming_entries),
                        stringResource(R.string.all_entries)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = isOldItemsVisible,
                            onExpandedChange = { isOldItemsVisible = !isOldItemsVisible },
                            modifier = Modifier.fillMaxWidth(0.5f)
                        ) {
                            MgTextField(
                                value = if (selectedOption == ReminderFilter.New) options[0] else options[1],
                                onValueChange = { /* onValueChange should be empty for readOnly */ },
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                readOnly = true,
                                label = stringResource(R.string.filter),
                                basicFontSize0 = basicFontSize,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isOldItemsVisible)
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = isOldItemsVisible,
                                onDismissRequest = { isOldItemsVisible = false }
                            ) {
                                options.forEachIndexed { index, opt ->
                                    DropdownMenuItem(
                                        modifier = Modifier
                                            .padding(vertical = (basicFontSize.value / 8).dp)
                                            .border(
                                                (basicFontSize.value / 16).dp,
                                                MaterialTheme.colorScheme.primary
                                            ),
                                        text = {
                                            MgText(
                                                text = opt,
                                                basicFontSize = basicFontSize,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(0.dp)
                                            )
                                        },
                                        onClick = {
                                            selectedOption =
                                                if (index == 0) ReminderFilter.New else ReminderFilter.All
                                            isOldItemsVisible = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.fillMaxWidth(0.5f))
                        var isDeleteAllClicked by rememberSaveable { mutableStateOf(false) }
                        MgAlertDialogue(
                            showDialog = isDeleteAllClicked,
                            onDismissRequest = { isDeleteAllClicked = false },
                            onConfirmation = {
                                itemsToShow.forEach {
                                    cancelAlarm(
                                        context,
                                        notificationCode = it.id0,
                                        dose = it.doseNumber.toString(),
                                        isPreviousDay = it.isPreviousDay
                                    )
                                }
                                uiHolder.deleteAll()
                            },
                            onReject = { isDeleteAllClicked = false },
                            title = stringResource(R.string.delete_all),
                            message = stringResource(R.string.are_you_sure_you_want_to_delete_all_reminders),
                            confirmString = stringResource(R.string.yes),
                            dismissString = stringResource(R.string.no),
                            basicFontSize = basicFontSize
                        )
                        MgButton(
                            onClick = {
                                isDeleteAllClicked = true
                            },
                            basicFontSize = basicFontSize,
                            text = {
                                Row {
                                    MgText(
                                        text = stringResource(R.string.delete_all),
                                        basicFontSize = basicFontSize,
                                        modifier = Modifier.padding(end = (basicFontSize.value / 4).dp)
                                    )
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.delete),
                                        tint = AppColors.Destructive
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(1f)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        itemsToShow.groupBy { reminder ->
                            SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                            ).format(Date(reminder.long0))
                        }.forEach { (dateString, items) ->
                            item {
                                val inputFormatter =
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy", LocalLocale.current.platformLocale)
                                val outputFormatter =
                                    DateTimeFormatter.ofPattern("dd MMMM, yyyy, EEEE", LocalLocale.current.platformLocale)
                                val localDate = LocalDate.parse(dateString, inputFormatter)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding((basicFontSize.value / 8).dp)
                                        .border(
                                            (basicFontSize.value / 8).dp,
                                            MaterialTheme.colorScheme.inversePrimary
                                        )
                                        .background(MaterialTheme.colorScheme.inversePrimary)
                                        .padding((basicFontSize.value / 8).dp),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    MgText(
                                        text = localDate.format(outputFormatter),
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        basicFontSize = basicFontSize,
                                        isBold = true
                                    )
                                }
                            }

                            items(items) {
                                r ->
                                var isDeleteClicked by rememberSaveable { mutableStateOf(false) }
                                MgAlertDialogue(
                                    showDialog = isDeleteClicked,
                                    onDismissRequest = { isDeleteClicked = false },
                                    onConfirmation = {
                                        uiHolder.delete(r.id0)
                                        cancelAlarm(
                                            context = context,
                                            notificationCode = r.id0,
                                            dose = r.doseNumber.toString(),
                                            isPreviousDay = r.isPreviousDay
                                        )
                                        isDeleteClicked = false
                                    },
                                    onReject = { isDeleteClicked = false },
                                    title = stringResource(R.string.delete),
                                    message = stringResource(
                                        R.string.are_you_sure_you_want_to_delete_the_reminder_on,
                                        Date(r.long0).let {
                                            SimpleDateFormat(
                                                "dd/MM/yyyy",
                                                Locale.ENGLISH
                                            ).format(it)
                                        }
                                    ),
                                    confirmString = stringResource(R.string.yes),
                                    dismissString = stringResource(R.string.no),
                                    basicFontSize = basicFontSize
                                )
                                ReminderCard(
                                    reminder = r,
                                    onToggle = {
                                        uiHolder.setActive(r.id0, it)
                                        if (!it) {
                                            cancelAlarm(
                                                context = context,
                                                notificationCode = r.id0,
                                                isPreviousDay = r.isPreviousDay,
                                                dose = r.doseNumber.toString()
                                            )
                                        } else {
                                            setMgAlarm(
                                                context = context,
                                                mgLong = r.long0,
                                                id0 = r.id0,
                                                dose = r.doseNumber,
                                                isPreviousDay = r.isPreviousDay
                                            )
                                        }
                                    },
                                    onDelete = {
                                        isDeleteClicked = true
                                    },
                                    basicFontSize = basicFontSize
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                        item {
                            Spacer(Modifier.height((basicFontSize.value * 6).dp))
                        }
                    }
                }
            }
        }
    }
}