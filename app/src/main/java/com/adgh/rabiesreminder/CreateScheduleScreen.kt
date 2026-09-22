package com.adgh.rabiesreminder

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.adgh.rabiesreminder.ui.components.MgButton
import com.adgh.rabiesreminder.ui.components.MgText
import com.adgh.rabiesreminder.ui.components.today7amLong
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun CreateScheduleScreen(
    onConfirm: () -> Unit,
    uiHolder: ReminderUiStateHolder,
    basicFontSize: TextUnit,
    context: Context,
    selectedLanguage : String
) {
    val scheduleType0 = stringResource(R.string.schedule_type)
    var scheduleType by rememberSaveable(scheduleType0) { mutableStateOf(scheduleType0) }
    var selectedIndex by rememberSaveable { mutableIntStateOf(-1) }
    var startDate by rememberSaveable { mutableLongStateOf(today7amLong()) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(basicFontSize.value.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val options = listOf(
            stringResource(R.string.anti_rabies_post_exposure_prophylaxis_intra_dermal),
            stringResource(R.string.anti_rabies_post_exposure_prophylaxis_intra_muscular)
        )
        val pleaseSelect = stringResource(R.string.please_select)
        var mgDate by rememberSaveable(
            selectedLanguage
        ) { mutableStateOf(pleaseSelect) }
        var showDatePicker by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDate,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val minDate = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
                    val maxDate = System.currentTimeMillis() + (2L * 24 * 60 * 60 * 1000)

                    return utcTimeMillis in minDate..maxDate
                }
            }
        )
        // Custom formatting for the Headline (the top part)
        val fullDateHeadline by remember {
            derivedStateOf {
                val millis = datePickerState.selectedDateMillis
                if (millis != null) {
                    SimpleDateFormat("dd MMMM yyyy, EEEE", Locale.forLanguageTag(selectedLanguage)).format(Date(millis))
                } else {
                    pleaseSelect
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    MgButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let {
                                startDate = it
                                mgDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                            }
                            showDatePicker = false
                        },
                        text = stringResource(R.string.ok),
                        basicFontSize = basicFontSize
                    )
                },
                dismissButton = {
                    MgButton(
                        onClick = { showDatePicker = false },
                        text = stringResource(R.string.cancel),
                        basicFontSize = basicFontSize
                    )
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    headline = {
                        MgText(
                            text = fullDateHeadline,
                            basicFontSize = basicFontSize,
                            modifier = Modifier.padding(start = 24.dp, end = 12.dp, bottom = 12.dp)
                        )
                    }
                )
            }
        }

        MgText(text= stringResource(R.string.schedule_type), basicFontSize= basicFontSize)
        ExposedDropdown(
            value = scheduleType,
            basicFontSize = basicFontSize
        ) { index ->
            selectedIndex = index
            scheduleType = options[index]
        }
        MgButton(
            onClick = { showDatePicker = true },
            text = stringResource(R.string.start_date, mgDate),
            basicFontSize = basicFontSize
        )
        Spacer(Modifier.height(8.dp))
        if (
            selectedIndex != -1 &&
            mgDate != stringResource(R.string.please_select)
        ) {
            SchedulePreview(
                selectedIndex = selectedIndex,
                onConfirm = onConfirm,
                baseLong = startDate,
                uiHolder = uiHolder,
                basicFontSize = basicFontSize,
                context= context
            )
        }
    }
}