package com.adgh.rabiesreminder

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.adgh.rabiesreminder.ui.components.AlarmEntry
import com.adgh.rabiesreminder.ui.components.MgAlertDialogue
import com.adgh.rabiesreminder.ui.components.MgButton
import com.adgh.rabiesreminder.ui.components.MgText
import com.adgh.rabiesreminder.ui.components.today7amLong
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun ManageNewSchedule(
    currentSchedule: PEPSchedule, baseLong: Long, basicFontSize: TextUnit, uiHolder: ReminderUiStateHolder, onConfirm: () -> Unit,
    context: Context
) {
    val coroutineScope = rememberCoroutineScope()
    
    when (currentSchedule) {
        PEPSchedule.IntraDermal -> {
            var isConfirmAlertDialogueShown by rememberSaveable {
                mutableStateOf(false)
            }
            var is3On by rememberSaveable { mutableStateOf(true) }
            var isP3On by rememberSaveable { mutableStateOf(true) }
            var is7On by rememberSaveable { mutableStateOf(true) }
            var isP7On by rememberSaveable { mutableStateOf(true) }
            var is28On by rememberSaveable { mutableStateOf(true) }
            var isP28On by rememberSaveable { mutableStateOf(true) }
            val isTodayOrFuture = baseLong >= today7amLong() - 7 * 60 * 60 * 1000 // Simple check for today or future
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MgText(text = stringResource(R.string._0th_day), basicFontSize = basicFontSize, modifier = Modifier.fillMaxWidth(0.25f))
                MgText(
                    text = SimpleDateFormat(
                        "dd/MM/yyyy", LocalLocale.current.platformLocale
                    ).format(Date(baseLong)),
                    basicFontSize = basicFontSize,
                    modifier = Modifier.fillMaxWidth(1f)
                )
            }
            Spacer(Modifier.height(basicFontSize.value.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth()) {
                    MgText(text = stringResource(R.string._3rd_day), basicFontSize = basicFontSize, modifier = Modifier.fillMaxWidth(0.25f))
                    MgText(
                        text = SimpleDateFormat(
                            "dd/MM/yyyy", LocalLocale.current.platformLocale
                        ).format(Date(calculateAlarmLong(baseLong, 3, 7))),
                        basicFontSize = basicFontSize,
                        modifier = Modifier.fillMaxWidth(1f)
                    )
                }
                AlarmEntry(basicFontSize, is3On, isP3On, { is3On = it }, { isP3On = it }, isTodayOrFuture)
            }
            Spacer(Modifier.height(basicFontSize.value.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth()) {
                    MgText(
                        text = stringResource(R.string._7th_day),
                        basicFontSize = basicFontSize,
                        modifier = Modifier.fillMaxWidth(0.25f)
                    )
                    MgText(
                        text = SimpleDateFormat(
                            "dd/MM/yyyy", LocalLocale.current.platformLocale
                        ).format(Date(calculateAlarmLong(baseLong, 7, 7))),
                        basicFontSize = basicFontSize,
                        modifier = Modifier.fillMaxWidth(1f)
                    )
                }
                AlarmEntry(basicFontSize, is7On, isP7On, { is7On = it }, { isP7On = it }, isTodayOrFuture)
            }
            Spacer(Modifier.height(basicFontSize.value.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth()) {
                    MgText(text = stringResource(R.string._28th_day), basicFontSize = basicFontSize, modifier = Modifier.fillMaxWidth(0.25f))
                    MgText(
                        modifier = Modifier.fillMaxWidth(1f),
                        text = SimpleDateFormat(
                            "dd/MM/yyyy", LocalLocale.current.platformLocale
                        ).format(Date(calculateAlarmLong(baseLong, 28, 7))),
                        basicFontSize = basicFontSize
                    )
                }
                AlarmEntry(basicFontSize, is28On, isP28On, { is28On = it }, { isP28On = it }, isTodayOrFuture)
            }
            MgButton(
                onClick = { isConfirmAlertDialogueShown = true },
                text = stringResource(R.string.confirm_schedule),
                basicFontSize = basicFontSize,
                modifier = Modifier.fillMaxWidth()
            )
            MgAlertDialogue(
                showDialog = isConfirmAlertDialogueShown,
                onDismissRequest = { isConfirmAlertDialogueShown = false },
                onConfirmation = {
                    coroutineScope.launch {
                        val injectionHour = if (isTodayOrFuture) 7 else 9
                        if (is3On) {
                            calculateAlarmLong(baseLong, 3, injectionHour).let {
                                val id = uiHolder.insert(
                                    MgRecord(
                                        schedule0 = PEPSchedule.IntraDermal,
                                        doseNumber = 2,
                                        long0 = it
                                    )
                                )
                                setMgAlarm(context, id.toInt(), 2, it, false)
                            }
                            if (isP3On) {
                                calculateAlarmLong(baseLong, 1, 9).let {
                                    val id = uiHolder.insert(
                                        MgRecord(
                                            schedule0 = PEPSchedule.IntraDermal,
                                            doseNumber = 2,
                                            long0 = it,
                                            isPreviousDay = true
                                        )
                                    )
                                    setMgAlarm(context, id.toInt(), 2, it, true)
                                }
                            }
                        }
                        if (is7On) {
                            calculateAlarmLong(baseLong, 7, injectionHour).let {
                                val id = uiHolder.insert(
                                    MgRecord(
                                        schedule0 = PEPSchedule.IntraDermal,
                                        doseNumber = 3,
                                        long0 = it
                                    )
                                )
                                setMgAlarm(context, id.toInt(), 3, it, false)
                            }
                            if (isP7On) {
                                calculateAlarmLong(baseLong, 5, 9).let {
                                    val id = uiHolder.insert(
                                        MgRecord(
                                            schedule0 = PEPSchedule.IntraDermal,
                                            doseNumber = 3,
                                            long0 = it,
                                            isPreviousDay = true
                                        )
                                    )
                                    setMgAlarm(context, id.toInt(), 3, it, true)
                                }
                            }
                        }
                        if (is28On) {
                            calculateAlarmLong(baseLong, 28, injectionHour).let {
                                val id = uiHolder.insert(
                                    MgRecord(
                                        schedule0 = PEPSchedule.IntraDermal,
                                        doseNumber = 4,
                                        long0 = it
                                    )
                                )
                                setMgAlarm(context, id.toInt(), 4, it, false)
                            }
                            if (isP28On) {
                                calculateAlarmLong(baseLong, 26, 9).let {
                                    val id = uiHolder.insert(
                                        MgRecord(
                                            schedule0 = PEPSchedule.IntraDermal,
                                            doseNumber = 4,
                                            long0 = it,
                                            isPreviousDay = true
                                        )
                                    )
                                    setMgAlarm(context, id.toInt(), 4, it, true)
                                }
                            }
                        }
                        onConfirm()
                    }
                    isConfirmAlertDialogueShown = false
                },
                onReject = { isConfirmAlertDialogueShown = false },
                title = stringResource(R.string.are_you_sure),
                message = stringResource(R.string.are_you_sure_you_want_to_confirm_this_schedule),
                confirmString = stringResource(R.string.yes),
                dismissString = stringResource(R.string.no),
                basicFontSize = basicFontSize
            )
        }
        PEPSchedule.IntraMuscular -> {
            val isTodayOrFuture = baseLong >= today7amLong() - 7 * 60 * 60 * 1000
            var isConfirmAlertDialogueShown by rememberSaveable {
                mutableStateOf(false)
            }
            var is3On by rememberSaveable { mutableStateOf(true) }
            var isP3On by rememberSaveable { mutableStateOf(true) }
            var is7On by rememberSaveable { mutableStateOf(true) }
            var isP7On by rememberSaveable { mutableStateOf(true) }
            var is14On by rememberSaveable { mutableStateOf(true) }
            var isP14On by rememberSaveable { mutableStateOf(true) }
            var is28On by rememberSaveable { mutableStateOf(true) }
            var isP28On by rememberSaveable { mutableStateOf(true) }
            Row(Modifier.fillMaxWidth()) {
                MgText(text = stringResource(R.string._0th_day), basicFontSize = basicFontSize, modifier = Modifier.fillMaxWidth(0.25f))
                MgText(
                    text = SimpleDateFormat(
                        "dd/MM/yyyy", LocalLocale.current.platformLocale
                    ).format(Date(baseLong)),
                    basicFontSize = basicFontSize,
                    modifier = Modifier.fillMaxWidth(1f)
                )
            }
            Spacer(Modifier.height(basicFontSize.value.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth()) {
                    MgText(text = stringResource(R.string._3rd_day), basicFontSize = basicFontSize, modifier = Modifier.fillMaxWidth(0.25f))
                    MgText(
                        text = SimpleDateFormat(
                            "dd/MM/yyyy", LocalLocale.current.platformLocale
                        ).format(Date(calculateAlarmLong(baseLong, 3, 7))),
                        basicFontSize = basicFontSize,
                        modifier = Modifier.fillMaxWidth(1f)
                    )
                }
                AlarmEntry(basicFontSize, is3On, isP3On, { is3On = it }, { isP3On = it }, isTodayOrFuture)
            }
            Spacer(Modifier.height(basicFontSize.value.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth()) {
                    MgText(text = stringResource(R.string._7th_day), basicFontSize = basicFontSize, modifier = Modifier.fillMaxWidth(0.25f))
                    MgText(
                        text = SimpleDateFormat(
                            "dd/MM/yyyy", LocalLocale.current.platformLocale
                        ).format(Date(calculateAlarmLong(baseLong, 7, 7))),
                        basicFontSize = basicFontSize,
                        modifier = Modifier.fillMaxWidth(0.5f)
                    )
                }
                AlarmEntry(basicFontSize, is7On, isP7On, { is7On = it }, { isP7On = it }, isTodayOrFuture)
            }
            Spacer(Modifier.height(basicFontSize.value.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth()) {
                    MgText(text = stringResource(R.string._14th_day), basicFontSize = basicFontSize, modifier = Modifier.fillMaxWidth(0.25f))
                    MgText(
                        text = SimpleDateFormat(
                            "dd/MM/yyyy", LocalLocale.current.platformLocale
                        ).format(Date(calculateAlarmLong(baseLong, 14, 7))),
                        basicFontSize = basicFontSize,
                        modifier = Modifier.fillMaxWidth(0.5f)
                    )
                }
                AlarmEntry(basicFontSize, is14On, isP14On, { is14On = it }, { isP14On = it }, isTodayOrFuture)
            }
            Spacer(Modifier.height(basicFontSize.value.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth()) {
                    MgText(text = stringResource(R.string._28th_day), basicFontSize = basicFontSize, modifier = Modifier.fillMaxWidth(0.25f))
                    MgText(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        text = SimpleDateFormat(
                            "dd/MM/yyyy", LocalLocale.current.platformLocale
                        ).format(Date(calculateAlarmLong(baseLong, 28, 7))),
                        basicFontSize = basicFontSize
                    )
                }
                AlarmEntry(basicFontSize, is28On, isP28On, { is28On = it }, { isP28On = it }, isTodayOrFuture)
            }
            MgButton(
                onClick = { isConfirmAlertDialogueShown = true },
                text = stringResource(R.string.confirm_schedule),
                basicFontSize = basicFontSize,
                modifier = Modifier.fillMaxWidth()
            )
            MgAlertDialogue(
                showDialog = isConfirmAlertDialogueShown,
                onDismissRequest = { isConfirmAlertDialogueShown = false },
                onConfirmation = {
                    coroutineScope.launch {
                        val injectionHour = if (isTodayOrFuture) 7 else 9
                        if (is3On) {
                            calculateAlarmLong(baseLong, 3, injectionHour).let {
                                val id = uiHolder.insert(
                                    MgRecord(
                                        schedule0 = PEPSchedule.IntraMuscular,
                                        doseNumber = 2,
                                        long0 = it
                                    )
                                )
                                setMgAlarm(context, id.toInt(), 2, it, false)
                            }
                            if (isP3On) {
                                calculateAlarmLong(baseLong, 1, 9).let {
                                    val id = uiHolder.insert(
                                        MgRecord(
                                            schedule0 = PEPSchedule.IntraMuscular,
                                            doseNumber = 2,
                                            long0 = it,
                                            isPreviousDay = true
                                        )
                                    )
                                    setMgAlarm(context, id.toInt(), 2, it, true)
                                }
                            }
                        }
                        if (is7On) {
                            calculateAlarmLong(baseLong, 7, injectionHour).let {
                                val id = uiHolder.insert(
                                    MgRecord(
                                        schedule0 = PEPSchedule.IntraMuscular,
                                        doseNumber = 3,
                                        long0 = it
                                    )
                                )
                                setMgAlarm(context, id.toInt(), 3, it, false)
                            }
                            if (isP7On) {
                                calculateAlarmLong(baseLong, 5, 9).let {
                                    val id = uiHolder.insert(
                                        MgRecord(
                                            schedule0 = PEPSchedule.IntraMuscular,
                                            doseNumber = 3,
                                            long0 = it,
                                            isPreviousDay = true
                                        )
                                    )
                                    setMgAlarm(context, id.toInt(), 3, it, true)
                                }
                            }
                        }
                        if (is14On) {
                            calculateAlarmLong(baseLong, 14, injectionHour).let {
                                val id = uiHolder.insert(
                                    MgRecord(
                                        schedule0 = PEPSchedule.IntraMuscular,
                                        doseNumber = 4,
                                        long0 = it
                                    )
                                )
                                setMgAlarm(context, id.toInt(), 4, it, false)
                            }
                            if (isP14On) {
                                calculateAlarmLong(baseLong, 12, 9).let {
                                    val id = uiHolder.insert(
                                        MgRecord(
                                            schedule0 = PEPSchedule.IntraMuscular,
                                            doseNumber = 4,
                                            long0 = it,
                                            isPreviousDay = true
                                        )
                                    )
                                    setMgAlarm(context, id.toInt(), 4, it, true)
                                }
                            }
                        }
                        if (is28On) {
                            calculateAlarmLong(baseLong, 28, injectionHour).let {
                                val id = uiHolder.insert(
                                    MgRecord(
                                        schedule0 = PEPSchedule.IntraMuscular,
                                        doseNumber = 5,
                                        long0 = it
                                    )
                                )
                                setMgAlarm(context, id.toInt(), 5, it, false)
                            }
                            if (isP28On) {
                                calculateAlarmLong(baseLong, 26, 9).let {
                                    val id = uiHolder.insert(
                                        MgRecord(
                                            schedule0 = PEPSchedule.IntraMuscular,
                                            doseNumber = 5,
                                            long0 = it,
                                            isPreviousDay = true
                                        )
                                    )
                                    setMgAlarm(context, id.toInt(), 5, it, true)
                                }
                            }
                        }
                        onConfirm()
                    }
                    isConfirmAlertDialogueShown = false
                },
                onReject = { isConfirmAlertDialogueShown = false },
                title = stringResource(R.string.are_you_sure),
                message = stringResource(R.string.are_you_sure_you_want_to_confirm_this_schedule),
                confirmString = stringResource(R.string.yes),
                dismissString = stringResource(R.string.no),
                basicFontSize = basicFontSize
            )
            MgButton(
                text = "Test Alarm",
                basicFontSize = basicFontSize,
                onClick = {
                    coroutineScope.launch{
                        setMgAlarm(context, 0, 1, Date().time + 10000L, false)
                    }
                }
            )
        }
    }
}
